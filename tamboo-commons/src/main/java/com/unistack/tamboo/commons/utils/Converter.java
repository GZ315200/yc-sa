package com.unistack.tamboo.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author Gyges Zean
 * @date 2018/5/2
 */
public class Converter {

    private static  Logger log = LoggerFactory.getLogger(Converter.class);

    /**
     * convert OBJECT to byte array.
     *
     * @param value
     * @param <T>
     * @return
     */
    public static <T> byte[] toBytes(T value) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(value);
        } catch (IOException e) {
            log.error("Failed to write to OBJECT.This Object can not be written.");
        }
        return bos.toByteArray();
    }

    /**
     * convert int to byte.
     * @param value
     * @return
     */
    public static byte toByte(int value) {
        return Integer.valueOf(value).byteValue();
    }



}
