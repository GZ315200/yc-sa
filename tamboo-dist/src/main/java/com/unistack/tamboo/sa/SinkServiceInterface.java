package com.unistack.tamboo.sa;

import com.alibaba.fastjson.JSONObject;

/**
 * @author anning
 * @date 2018/7/20 下午5:31
 * @description: sink service 接口
 */
public interface SinkServiceInterface {
    JSONObject checkConfig(JSONObject var1);

    JSONObject startSink(JSONObject var1);

    JSONObject stopSink(String var1);

    JSONObject getStatusByConnectName(String var1);
}
