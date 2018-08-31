package com.unistack.tamboo.commons.utils;

import com.google.common.base.MoreObjects;

/**
 * Created by Gyges Zean on 16/8/16.
 */
public class OkHttpResponse {
    private int code;
    private String body;

    public OkHttpResponse(int code, String body) {
        this.code = code;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("code", code)
                .add("body", body)
                .toString();
    }
}
