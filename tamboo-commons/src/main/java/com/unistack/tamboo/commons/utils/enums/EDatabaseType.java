package com.unistack.tamboo.commons.utils.enums;

/**
 * @author Gyges Zean
 * @date 2018/5/22
 */
public enum EDatabaseType {

    FLUME("FLUME", 4),
    KAFKA("KAFKA", 1),
    SPARK("SPARK", 2),
    CONNECT("CONNECT", 3);

    private String name;

    private Integer code;

    EDatabaseType(String name, Integer code) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
