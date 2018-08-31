package com.unistack.tamboo.mgt.common.enums;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */
public enum ERoleType {

    USER("USER", 1),

    ADMIN("ADMIN", 0);

    private String name;

    private Integer code;

    private ERoleType(String name, Integer code) {
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
