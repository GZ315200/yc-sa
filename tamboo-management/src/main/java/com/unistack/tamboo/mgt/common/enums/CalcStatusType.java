package com.unistack.tamboo.mgt.common.enums;

public enum CalcStatusType {
    START(1),
    STOP(0),
    WARNING(-1);

    private int type;

    CalcStatusType(int type) {
        this.type=type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}