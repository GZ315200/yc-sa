package com.unistack.tamboo.mgt.common.enums;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */
public enum EPermissionType {

    EDIT("edit", 0),
    VIEW("view", 1),
    OPERATION("operation", 2);

    private String name;

    private Integer code;

    private EPermissionType(String name, Integer code) {
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
