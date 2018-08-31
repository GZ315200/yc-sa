package com.unistack.tamboo.mgt.service.monitor;

import com.alibaba.fastjson.JSON;
import com.unistack.tamboo.commons.utils.CommonUtils;
import com.unistack.tamboo.message.kafka.runtime.Runner;
import com.unistack.tamboo.mgt.common.enums.EWebSocketTopic;
import com.unistack.tamboo.mgt.job.*;
import com.unistack.tamboo.mgt.model.monitor.CalcServiceInfoVo;
import com.unistack.tamboo.mgt.model.monitor.MClusterServiceVo;
import com.unistack.tamboo.mgt.service.BaseService;
import com.unistack.tamboo.mgt.helper.ConfigHelper;
import com.unistack.tamboo.mgt.utils.MonitorUtils;
import com.unistack.tamboo.mgt.utils.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;


/**
 * @author Gyges Zean
 * @date 2018/5/23
 */

@Service(value = "dataInfoCollectorService")
public class DataInfoCollectorService extends BaseService {
    public static  Logger logger = LoggerFactory.getLogger(DataInfoCollectorService.class);

    @Autowired
    private OffsetCollectorService offsetCollectorService;

    @Autowired
    private FlumeDataCollectorService flumeDataCollectorService;

    @Autowired
    private SparkDataCollectorService sparkDataCollectorService;

    @Autowired
    private WorkoutPeakOffsetJob workoutPeakOffsetJob;

    @Autowired
    private WorkoutPeakConnectDataJob workoutPeakConnectDataJob;

    @Autowired
    private ClusterInfoSaving clusterInfoSaving;

    @Autowired
    private MetricsService metricsService;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private CalcServiceInfoSaving calcServiceInfoSaving;


    private static  long DELAY_TIME = 10;
    private static  long DEFAULT_INTERVAL_TIME = 30;

    private Map<String, Object> config = ConfigHelper.setRunnerConfig();

    private Runner runner = new Runner(config);


    private void schedule( Callable<Void> callable,  long interval) {
        new Thread(() -> {
            long pointcut = CommonUtils.formatTimestamp(System.currentTimeMillis()) + interval * 1000;
            while (true) {
                try {
                    if (System.currentTimeMillis() > pointcut) {
                        pointcut += interval * 1000;
                        // creating new thread instead of just call it
                        callable.call();
//                        executorService.submit(callable);
                    } else {
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    logger.error("failed to execute the job, continue.", e);
                }
            }
        }).start();
    }


    /**
     * 开启offset采集器
     */
    private void startOffsetSaving() {
        logger.info("start to save offset data in Asyn.");
        schedule(() -> {
            offsetCollectorService.storeConsumerGroupOffsets(runner);
            return null;
        }, PropertiesUtil.getLong("monitor.offset.interval"));
    }

    /**
     * 开启采集监控flume source data，并保存数据
     */
    private void startSourceDataSaving() {
        logger.info("start to save flume source record in Asyn.");
        schedule(() -> {
            flumeDataCollectorService.receiveFlumeDataCollector();
            return null;
        }, PropertiesUtil.getLong("monitor.source.data.interval"));
    }


    /**
     * 开启采集spark straming中的监控数据
     */
    private void startCollectStreamingData() {
        logger.info("start to save spark streaming record in Asyn.");
        schedule(() -> {
            sparkDataCollectorService.receiveCalculateData();
            return null;
        }, PropertiesUtil.getLong("monitor.streaming.data.interval"));
    }


    /**
     * 开启采集spark streaming中的监控数据
     */
    private void startMonitorSinkData() {
        logger.info("start to save sink data in Asyn.");
        schedule(() -> {
            workoutPeakConnectDataJob.receiveTheSinkMonitorData();
            return null;
        }, PropertiesUtil.getLong("monitor.streaming.data.interval"));
    }


    /**
     * 开启采集spark streaming中的监控数据
     */
    private void startWorkoutPeakOffset() {
        logger.info("start to save peak offset data in Asyn.");
        schedule(() -> {
            workoutPeakOffsetJob.workoutPeakNum();
            return null;
        }, PropertiesUtil.getLong("monitor.offset.interval"));
    }

    /**
     * 开启采集zookeeper metrics的信息
     */
    private void collectZookeeperMetricsInKafka() {
        logger.info("start to collect Kafka zookeeper metrics in Asyn.");
        schedule(() -> {
            metricsService.zookeeperMetricsInKafka(System.currentTimeMillis());
            return null;
        }, PropertiesUtil.getLong("collect.metrics.interval"));
    }


    /**
     * 开启采集brokers metrics的信息
     */
    private void collectBrokersMetrics() {
        logger.info("start to collect brokers metrics in Asyn.");
        schedule(() -> {
            metricsService.getBrokersMetrics(System.currentTimeMillis());
            return null;
        }, PropertiesUtil.getLong("collect.metrics.interval"));
    }

    /**
     * 开启采集brokers metrics的信息
     */
    private void saveBrokerServiceInfoToDB() {
        logger.info("start to broker service info in Asyn.");
        schedule(() -> {
            MClusterServiceVo mClusterServiceVo = metricsService.getBrokersMetrics();
            logger.info("save broker service info to DB down: {}", JSON.toJSON(mClusterServiceVo));
            return null;
        }, PropertiesUtil.getLong("save.broker.info.interval"));
    }


    /**
     * 开启采集calc workers的信息
     */
    private void saveCalcServiceInfoToDB() {
        logger.info("start to calc service info in Asyn.");
        schedule(() -> {
            CalcServiceInfoVo calcServiceInfoVo = calcServiceInfoSaving.savingCalcWorkersInfo(System.currentTimeMillis() / 1000);
            logger.info("save calc service info to DB down: {}", JSON.toJSON(calcServiceInfoVo));
            return null;
        }, PropertiesUtil.getLong("save.calc.info.interval"));
    }


    /**
     * 开启采集brokers metrics的信息
     */
    private void saveZkMetricsInfo() {
        logger.info("start to zookeeper metrics info in Asyn.");
        schedule(() -> {
            metricsService.getZookeeperMetrics(System.currentTimeMillis() / 1000);
            return null;
        }, PropertiesUtil.getLong("collect.metrics.interval"));
    }


    /**
     * 开启采集producers metrics的信息
     */
    private void saveConnectMetricsInfo() {
        logger.info("start to connect metrics info in Asyn.");
        schedule(() -> {
            metricsService.getProducerMetrics(System.currentTimeMillis() / 1000);
            return null;
        }, PropertiesUtil.getLong("collect.metrics.interval"));
    }


    /**
     * 保存 producers metrics的信息到数据库
     */
    private void saveConnectSourceMetricsIntoDB() {
        logger.info("start to save connect metrics info to DB in Asyn.");
        schedule(() -> {
            metricsService.saveConnectRecordToDb();
            return null;
        }, PropertiesUtil.getLong("collect.metrics.interval"));
    }


    /**
     * 启动定时任务,采集BrokerInfo,推送monitor data
     */
    public void start() {

        startOffsetSaving();
        startMonitorSinkData();
        startSourceDataSaving();
        startCollectStreamingData();
        startWorkoutPeakOffset();

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
        executorService.scheduleAtFixedRate(() ->
                        clusterInfoSaving.saveBrokerInfo(),
                DELAY_TIME, DEFAULT_INTERVAL_TIME, TimeUnit.SECONDS);

        executorService.scheduleAtFixedRate(() -> {
            logger.info("send runtime monitor data: {}", MonitorUtils.monitorDataRate().toJSONString());
            template.convertAndSend(EWebSocketTopic.TAMBOO_RUNTIME_DATA_RATE.getName(), MonitorUtils.monitorDataRate());
        }, 1, 9, TimeUnit.SECONDS);

        collectZookeeperMetricsInKafka();
        collectBrokersMetrics();
        saveZkMetricsInfo();
        saveBrokerServiceInfoToDB();
        saveCalcServiceInfoToDB();
        saveConnectMetricsInfo();
        saveConnectSourceMetricsIntoDB();
    }


}

