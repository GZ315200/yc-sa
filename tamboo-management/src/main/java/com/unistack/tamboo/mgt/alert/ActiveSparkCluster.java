package com.unistack.tamboo.mgt.alert;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.unistack.tamboo.commons.utils.OkHttpResponse;
import com.unistack.tamboo.commons.utils.OkHttpUtils;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.mgt.common.enums.AlertCategory;
import com.unistack.tamboo.mgt.common.enums.AlertLevel;
import com.unistack.tamboo.mgt.model.monitor.AlertRecord;
import com.unistack.tamboo.mgt.utils.MonitorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/6/25
 */
public class ActiveSparkCluster implements Alert {

    public static  Logger log = LoggerFactory.getLogger(ActiveSparkCluster.class);

    @Override
    public List<AlertRecord> checkout(JSONObject threshold) {

        List<AlertRecord> alertRecords = Lists.newArrayList();

        List<String> appNames = MonitorUtils.getAppNames();

        for (String appName : appNames) {
            String calUrl = "http://" + TambooConfig.CALC_CLUSTER_IPS+ ":4040/api/v1/applications";
            String statisticsUrl = calUrl + "/" + appName + "/streaming/statistics";
            try {
                OkHttpResponse response = OkHttpUtils.get(statisticsUrl);
                if (response.getCode() != 200) {
                    AlertRecord alertRecord = new AlertRecord(this, "sparkCluster", AlertCategory.SPARK_CLUSTER.getCode());
                    alertRecord.setLevel(AlertLevel.ERROR.getValue());
                    String message = "spark cluster is down." + "the appName is:" + appName;
                    alertRecord.setMessage(message);
                    alertRecords.add(alertRecord);
                    MonitorUtils.updateSparkState(appName, false);
                } else {
                    AlertRecord alertRecord = new AlertRecord(this, "sparkCluster", AlertCategory.SPARK_CLUSTER.getCode());
                    alertRecord.setLevel(AlertLevel.INFO.getValue());
                    String message = "spark cluster is alive";
                    alertRecord.setMessage(message);
                    alertRecords.add(alertRecord);
                    MonitorUtils.updateSparkState(appName, true);
                }
            } catch (Exception e) {
                log.info("", e);
            }
        }
        return alertRecords;
    }
}
