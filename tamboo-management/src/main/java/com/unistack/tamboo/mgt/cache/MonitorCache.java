package com.unistack.tamboo.mgt.cache;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Gyges Zean
 * @date 2018/5/24
 */
public class MonitorCache {

    public MonitorCache() {
    }

    private static Map monitors = Collections.synchronizedMap(new LinkedHashMap());

    @SuppressWarnings(value = "unchecked")
    public static <K, V> void put(K k, V v) {
        monitors.put(k, v);
    }

    @SuppressWarnings(value = "unchecked")
    public static <K, V> Map<K, V> get() {
        if (monitors == null) {
            return null;
        }
        return monitors;
    }

}
