package com.unistack.tamboo.commons.utils.errors;

/**
 * @author Gyges Zean
 * @date 2018/5/28
 */
public class DataFormatErrorException extends DataException {
    public DataFormatErrorException(String s) {
        super(s);
    }

    public DataFormatErrorException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DataFormatErrorException(Throwable throwable) {
        super(throwable);
    }
}
