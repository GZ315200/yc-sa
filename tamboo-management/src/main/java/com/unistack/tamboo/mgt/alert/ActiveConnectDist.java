package com.unistack.tamboo.mgt.alert;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.unistack.tamboo.mgt.action.DataDist2Action;
import com.unistack.tamboo.mgt.common.enums.AlertCategory;
import com.unistack.tamboo.mgt.common.enums.AlertLevel;
import com.unistack.tamboo.mgt.model.monitor.AlertRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/6/25
 */
public class  ActiveConnectDist implements Alert {

    public static  Logger logger = LoggerFactory.getLogger(ActiveConnectDist.class);

    /**
     * {"isSucceed":true,"msg":1}
     *
     * @param threshold
     * @return
     */

    @Override
    public List<AlertRecord> checkout(JSONObject threshold) {

        List<AlertRecord> alertRecords = Lists.newArrayList();

        try {
            List<String> connectNames = DataDist2Action.getAllConnectName();

            for (String connector : connectNames) {

                JSONObject data = DataDist2Action.getStatusByConnectName(connector);

                boolean isSucceed = data.getBoolean("isSucceed");
                int code = data.getInteger("msg");

                if (isSucceed) {
                    if (code == 2) {
                        AlertRecord alertRecord = new AlertRecord(this, "connectDist", AlertCategory.CONNECT_DIST.getCode());
                        alertRecord.setLevel(AlertLevel.ERROR.getValue());
//                        String message = "this connector " + connector + " is down.Please check it and fix it.";
                        String message = data.getString("trace");
                        alertRecord.setMessage(message);
                        alertRecords.add(alertRecord);
                    } else if (code == 1) {
                        AlertRecord alertRecord = new AlertRecord(this, "connectDist", AlertCategory.CONNECT_DIST.getCode());
                        alertRecord.setLevel(AlertLevel.INFO.getValue());
                        String message = "this connector is running";
                        alertRecord.setMessage(message);
                        alertRecords.add(alertRecord);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return alertRecords;
    }
}
