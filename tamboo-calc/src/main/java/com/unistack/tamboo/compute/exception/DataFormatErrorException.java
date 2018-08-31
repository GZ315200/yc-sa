package com.unistack.tamboo.compute.exception;

/**
 * @author hero.li
 * 这个自定义异常表示数据的格式错误
 */
public class DataFormatErrorException extends Exception {
    public DataFormatErrorException(String reason){
        super(reason);
    }
}