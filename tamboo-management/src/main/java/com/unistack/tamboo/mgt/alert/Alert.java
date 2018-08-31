package com.unistack.tamboo.mgt.alert;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.mgt.model.monitor.AlertRecord;

import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/6/25
 */
public interface Alert {

    List<AlertRecord> checkout(JSONObject threshold);
}
