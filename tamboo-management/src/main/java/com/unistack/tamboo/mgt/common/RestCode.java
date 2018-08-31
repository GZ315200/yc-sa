package com.unistack.tamboo.mgt.common;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */
public enum RestCode {

    ILLEGAL_ARGUMENT(400, "ILLEGAL_ARGUMENT"),
    UNAUTHORIZED(401,"UNAUTHORIZED"),
    NEED_LOGIN(301, "NEED_LOGIN"),
    ERROR(0, "ERROR"),
    //操作错误
    OPERATION_ERROR(2, "OPERATION ERROR"),
    SUCCESS(200, "SUCCESS");

    private int status;
    private String msg;

    RestCode() {
    }

    RestCode(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
