package com.unistack.tamboo.mgt.alert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unistack.tamboo.mgt.common.enums.EWebSocketTopic;
import com.unistack.tamboo.mgt.model.monitor.AlertItem;
import com.unistack.tamboo.mgt.model.monitor.AlertRecord;
import com.unistack.tamboo.mgt.utils.MonitorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Gyges Zean
 * @date 2018/6/25
 */
@Component
public class AlertEngine {


    public static  Logger logger = LoggerFactory.getLogger(AlertEngine.class);


    public static  int MERGE_CONDITION = 10;

    @Autowired
    private SimpMessagingTemplate template;


    private ScheduledExecutorService scheduler;


    private Map<String, Object> data = Maps.newConcurrentMap();

    /**
     *
     */
    private long count = 0;
    /**
     * 上一个记录
     */
    private String lastRecord;


    /**
     * 刷新AlertItem列表
     */
    private List<AlertItem> refreshAlertItems() {
        return MonitorUtils.getAlertItems();
    }


    /**
     * 采集告警信息,并将信息推送
     */
    public void rescheduleAlerts() {
        List<AlertItem> items = refreshAlertItems();
        logger.info("rescheduleAlerts, items = " + JSON.toJSONString(items));
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        scheduler = Executors.newScheduledThreadPool(items.size());
        for ( AlertItem item : items) {
            if (!item.getActive()) {
                continue;
            }
            try {
                if (item.getClassName() != null) {
                    String className = "com.unistack.tamboo.mgt.alert." + item.getClassName();
                     Alert alert = (Alert) Class.forName(className).newInstance();
                     long interval = item.getAlertInterval();
                    scheduler.scheduleAtFixedRate(() -> {
                        JSONObject thresholds = JSON.parseObject(item.getThreshold());
                        List<AlertRecord> list = null;
                        try {
                            list = alert.checkout(thresholds);
                        } catch (Exception e) {
                            logger.error("get exceptions while checking " + item.getAlertName(), e);
                        }

                        list = merge(list, item.getAlertLevel());

                        for (AlertRecord record : list) {
                            if (record.getLevel() >= item.getAlertLevel()) {
                                //此次告警标记为未解决

                                lastRecord = record.getMessage();

                                if (lastRecord.equals(record.getMessage())) {
                                    count++;
                                }
                                record.setResolved(false);
                                record.setAlertCount(count);
                                AlertRecord alertRecord = MonitorUtils.saveAlertRecord(record);
                                template.convertAndSend(EWebSocketTopic.TAMBOO_MANAGER_ALERT.getName(), alertRecord);
                            }
                        }
                        logger.debug("ALERT: " + JSON.toJSON(list));
                    }, 10, interval, TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                logger.error("failed to check for " + item.getAlertName(), e);
            }
        }
    }


    List<AlertRecord> merge(List<AlertRecord> records, Integer alertLevel) {
        List<AlertRecord> recordList = Lists.newArrayList();

        for (AlertRecord record : records) {
            if (record.getLevel() > alertLevel) {
                recordList.add(record);
            }
        }

        if (recordList.size() >= MERGE_CONDITION) {
            AlertRecord record = mergeAlertRecord(recordList);
            records.removeAll(recordList);
            records.add(record);
        }

        return records;

    }


    /**
     * Alert信息过多时,进行合并(默认大于5条)
     *
     * @param mergeRecords
     * @return
     */
    public static AlertRecord mergeAlertRecord(List<AlertRecord> mergeRecords) {
        StringBuilder message = new StringBuilder();
        StringBuilder cluster = new StringBuilder();
        int count = 0;
        for (AlertRecord alertRecord : mergeRecords) {
            message.append("@").append(alertRecord.getMessage());
            cluster.append("@").append(alertRecord.getClusterName());
            if (++count > 5) {
                break;
            }
        }
        AlertRecord record = new AlertRecord();
        BeanUtils.copyProperties(mergeRecords.get(0), record);
        record.setMessage(message.toString().substring(1));
        record.setClusterName(cluster.toString().substring(1));

        return record;
    }

}
