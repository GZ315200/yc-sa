package com.unistack.tamboo.mgt.alert;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.unistack.tamboo.commons.utils.OkHttpResponse;
import com.unistack.tamboo.commons.utils.OkHttpUtils;
import com.unistack.tamboo.mgt.common.enums.AlertCategory;
import com.unistack.tamboo.mgt.common.enums.AlertLevel;
import com.unistack.tamboo.mgt.common.enums.DataSourceEnum;
import com.unistack.tamboo.mgt.model.monitor.AlertRecord;
import com.unistack.tamboo.mgt.model.monitor.MCollectorVo;
import com.unistack.tamboo.mgt.utils.MonitorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/6/25
 */
public class ActiveFlumeCollector implements Alert {

    public static  Logger log = LoggerFactory.getLogger(ActiveFlumeCollector.class);

    @Override
    public List<AlertRecord> checkout(JSONObject threshold) {

        List<AlertRecord> alertRecords = Lists.newArrayList();

        //get 所有运行中的source connector
        //
        // if(list.size()>0){
        //  遍历 请求task状态
        //
        //

        List<MCollectorVo> mCollectorVos = MonitorUtils.getRunningUrls();

        for (MCollectorVo dcMonitor : mCollectorVos) {
            try {
                String dcUrl = dcMonitor.getUrl();
                OkHttpResponse response = OkHttpUtils.get(dcUrl);
                if (response.getCode() != 200) {
                    AlertRecord alertRecord = new AlertRecord(this, "flumeCollector", AlertCategory.FLUME_COLLECTOR.getCode());
                    alertRecord.setLevel(AlertLevel.ERROR.getValue());
                    String message = "Flume collector is down." + "with datasourceId " + dcMonitor.getDataSourceId() + " on topic " + dcMonitor.getTopicId();
                    alertRecord.setMessage(message);
                    alertRecords.add(alertRecord);
                    MonitorUtils.updateFlumeState(dcMonitor.getDataSourceId(), DataSourceEnum.DataSourceState.STOPPED.getValue());
                } else {
                    AlertRecord alertRecord = new AlertRecord(this, "flumeCollector", AlertCategory.FLUME_COLLECTOR.getCode());
                    alertRecord.setLevel(AlertLevel.INFO.getValue());
                    String message = "Flume collector is running.";
                    alertRecord.setMessage(message);
                    alertRecords.add(alertRecord);
                    MonitorUtils.updateFlumeState(dcMonitor.getDataSourceId(), DataSourceEnum.DataSourceState.STARTED.getValue());
                }
            } catch (Exception e) {
                log.error("", e);
            }

        }

        return alertRecords;
    }
}
