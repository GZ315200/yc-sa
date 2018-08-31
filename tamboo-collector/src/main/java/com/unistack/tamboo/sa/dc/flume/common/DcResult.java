package com.unistack.tamboo.sa.dc.flume.common;


public class DcResult {

    public  DcResult(boolean code,String msg){
        this.code=code;
        this.msg=msg;

    }

    public DcResult(){}

    private String path;
    private boolean code;
    private String msg;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isCode() {
        return code;
    }

    public void setCode(boolean code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
