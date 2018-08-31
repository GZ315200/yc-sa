package com.unistack.tamboo.commons.utils.errors;

/**
 * @author Gyges Zean
 * @date 2018/5/14
 */
public class NotFoundException extends DataException {

    public NotFoundException(String s) {
        super(s);
    }

    public NotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NotFoundException(Throwable throwable) {
        super(throwable);
    }
}
