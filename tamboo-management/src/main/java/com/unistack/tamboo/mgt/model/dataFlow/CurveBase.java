package com.unistack.tamboo.mgt.model.dataFlow;

public class CurveBase {
    private long timestamp;
    private int event;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }
}
