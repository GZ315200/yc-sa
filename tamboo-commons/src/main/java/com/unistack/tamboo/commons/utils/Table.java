package com.unistack.tamboo.commons.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Gyges Zean
 * @date 2018/5/10
 * 封装自定义数据结构。R:行 C:列 V:值
 */
public class Table<R, C, V> {

    private Map<R, Map<C, V>> table = new HashMap<>();

    public V put(R row, C column, V value) {
        Map<C, V> columns = table.get(row);
        if (Objects.isNull(columns)) {
            columns = new HashMap<>();
            table.put(row, columns);
        }
        return columns.put(column, value);
    }

    public V get(R row, C column) {
        Map<C, V> columns = table.get(row);
        if (columns == null) {
            return null;
        }
        return columns.get(column);
    }

    public V get(R row) {
        Map<C, V> columns = table.get(row);
        if (columns == null) {
            return null;
        }
        return columns.get(row);
    }


    public Map<C, V> remove(R row) {
        return table.remove(row);
    }

    public V remove(R row, C column) {
        Map<C, V> columns = table.get(row);
        if (columns == null) {
            return null;
        }

        V value = columns.remove(column);
        if (columns.isEmpty()) {
            table.remove(row);
        }
        return value;
    }

    public Map<C, V> row(R row) {
        Map<C, V> columns = table.get(row);
        if (columns == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(columns);
    }


}
