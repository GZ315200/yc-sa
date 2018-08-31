package com.unistack.tamboo.sa.dd2.worker;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd2.KafkaSinkWorker;

/**
 * @author anning
 * @date 2018/7/20 下午5:41
 * @description: hdfs worker
 */
public class HdfsWorker implements KafkaSinkWorker{
    @Override
    public JSONObject checkConfig(JSONObject var1) {
        return null;
    }

    @Override
    public JSONObject createConfigJson(JSONObject var1) {
        return null;
    }
}
