package com.unistack.tamboo.sa.dd.common;

import java.io.Serializable;

/**
 * @author anning
 * @date 2018/5/22 上午11:00
 */
public class DdResult implements Serializable {

    private static  long serialVersionUID = 1L;
    private boolean isSucceed;
    private String msg;

    public DdResult(boolean isSucceed, String msg) {
        this.isSucceed = isSucceed;
        this.msg = msg;
    }

    public DdResult() {
    }

    public boolean isSucceed() {
        return isSucceed;
    }

    public void setSucceed(boolean succeed) {
        isSucceed = succeed;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "DdResult{" +
                "isSucceed=" + isSucceed +
                ", msg='" + msg + '\'' +
                '}';
    }
}
