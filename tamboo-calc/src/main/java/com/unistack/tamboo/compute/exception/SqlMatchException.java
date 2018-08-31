package com.unistack.tamboo.compute.exception;

/**
 * @author hero.li
 * 这个类代表SQL匹配异常,如sql何参数不匹配等<br/>
 */
public class SqlMatchException extends Exception{

    public SqlMatchException(){}

    public SqlMatchException(String message) {
        super(message);
    }
}