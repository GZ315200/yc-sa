package com.unistack.tamboo.mgt.common.enums;

/**
 * @author Gyges Zean
 * @date 2018/6/25
 */

public enum AlertLevel {
    INFO(1), WARNING(2), ERROR(3);
    private  Integer value;

    AlertLevel(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    @Override
	public String toString() {
        return this.name();
    }
}

