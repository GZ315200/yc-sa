package com.unistack.tamboo.mgt.model.dashboard;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.base.MoreObjects;

/**
 * @author Gyges Zean
 * @date 2018/5/30
 * 封装dashboard实时数据／s，实时总量数据
 */
public class RealTimeData {


    @JSONField(name = "total_message_rate")
    private String totalMessageRate;

    @JSONField(name = "total_message")
    private String totalMessage;


    public RealTimeData() {
    }

    public RealTimeData(String totalMessageRate, String totalMessage) {
        this.totalMessageRate = totalMessageRate;
        this.totalMessage = totalMessage;
    }

    public String getTotalMessageRate() {
        return totalMessageRate;
    }

    public void setTotalMessageRate(String totalMessageRate) {
        this.totalMessageRate = totalMessageRate;
    }

    public String getTotalMessage() {
        return totalMessage;
    }

    public void setTotalMessage(String totalMessage) {
        this.totalMessage = totalMessage;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("totalMessageRate", totalMessageRate)
                .add("totalMessage", totalMessage)
                .toString();
    }
}
