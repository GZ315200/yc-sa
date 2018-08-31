package com.unistack.tamboo.sa.dd;

import com.alibaba.fastjson.JSONObject;

/**
 * @author anning
 * @date 2018/6/7 下午6:24
 * @description: jdbc
 */
public interface SinkWorker {
    /**
     * 检查配置
     * @param json 参数
     * @return
     */
    JSONObject checkConfig(JSONObject json);

}
