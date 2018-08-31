package com.unistack.tamboo.mgt.common;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * @author GygesM
 * 服务器响应json
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerResponse<T> implements Serializable {

    private T data;
    private int status;
    private String msg;


    private ServerResponse(int status) {
        this.status = status;
    }

    public ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(T data, int status) {
        this.data = data;
        this.status = status;
    }


    private ServerResponse(T data, int status, String msg) {
        this.data = data;
        this.status = status;
        this.msg = msg;
    }


    /**
     * 是否返回成功
     * @return
     */
    @JsonIgnore
//    不在序列化结果当中
    public boolean isSuccess(){
        return this.status == RestCode.SUCCESS.getStatus();
    }

    public int getStatus(){
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }


    /**
     * 返回状态码
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>( RestCode.SUCCESS.getStatus());
    }

//    /**
//     * 返回状态信息、状态码
//     * @param msg
//     * @param <T>
//     * @return
//     */
//    public static <T> ServerResponse<T> createBySuccess(String msg){
//        return new ServerResponse<T>( RestCode.SUCCESS.getStatus(),msg);
//    }

    /**
     * 返回data
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(data,RestCode.SUCCESS.getStatus());
    }

    /**
     * 返回data 、 code 、msg
     * @param data
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(data,RestCode.SUCCESS.getStatus(),msg);
    }

    /**
     * 创建错误code
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(RestCode.ERROR.getStatus(),RestCode.ERROR.toString());
    }


    public static <T> ServerResponse<T> createByErrorMsg(String errorMsg){
        return new ServerResponse<T>(RestCode.ERROR.getStatus(),errorMsg);
    }


    public static <T> ServerResponse<T> createByErrorCodeAndMsg(int errorCode,String errorMsg){
        return new ServerResponse<T>(errorCode,errorMsg);
    }

    public static <T> ServerResponse<T> createByResult(boolean res){
        if(res){
            return ServerResponse.createBySuccess();
        }else{
            return ServerResponse.createByError();
        }
    }
}
