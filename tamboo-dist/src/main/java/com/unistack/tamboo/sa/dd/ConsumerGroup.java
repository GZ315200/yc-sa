package com.unistack.tamboo.sa.dd;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.constant.DdType;

/**
 * @author anning
 * @date 2018/6/12 下午7:35
 * @description: 接口类
 */
public interface ConsumerGroup {
    ConsumerGroup initGroup(String connectorName, String groupId, String topic, DdType ddType, JSONObject args);
    void execute();         //多线程执行方法
}
