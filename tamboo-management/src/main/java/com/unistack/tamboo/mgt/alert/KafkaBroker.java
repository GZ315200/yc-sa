package com.unistack.tamboo.mgt.alert;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.unistack.tamboo.mgt.common.enums.AlertCategory;
import com.unistack.tamboo.mgt.common.enums.AlertLevel;
import com.unistack.tamboo.mgt.model.monitor.AlertRecord;
import com.unistack.tamboo.mgt.utils.MonitorUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/6/25
 */
public class KafkaBroker implements Alert {

    @Override
    public List<AlertRecord> checkout(JSONObject threshold) {


        List<AlertRecord> alertRecords = Lists.newArrayList();

        List<String> actualBrokers = MonitorUtils.actualBrokers();
        List<String> expectBrokers = MonitorUtils.expectBrokers();

        List<String> intersection = Lists.newArrayList(actualBrokers);
        intersection.retainAll(expectBrokers);

        expectBrokers.removeAll(intersection);
        actualBrokers.removeAll(intersection);


        if (CollectionUtils.isNotEmpty(expectBrokers)) {
            AlertRecord record = new AlertRecord(this, "brokers", AlertCategory.KAFKA_BROKER.getCode());
            record.setLevel(AlertLevel.ERROR.getValue());
            String message = "broker is down.hosts = " + expectBrokers.toString();
            record.setMessage(message);
            alertRecords.add(record);
        }

        if (CollectionUtils.isNotEmpty(actualBrokers)) {
            AlertRecord record = new AlertRecord(this, "brokers", AlertCategory.KAFKA_BROKER.getCode());
            record.setLevel(AlertLevel.WARNING.getValue());
            String message = "new broker, please add into cluster_info,hosts = " + actualBrokers.toString();
            record.setMessage(message);
            alertRecords.add(record);
        }


        return alertRecords;

    }

}
