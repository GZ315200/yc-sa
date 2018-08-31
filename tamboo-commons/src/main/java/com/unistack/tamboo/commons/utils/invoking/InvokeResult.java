package com.unistack.tamboo.commons.utils.invoking;


public class InvokeResult {

    public  InvokeResult(boolean code,String msg){
        this.code=code;
        this.msg=msg;

    }

    public InvokeResult(){}

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
