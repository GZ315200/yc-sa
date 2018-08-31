package com.unistack.tamboo.mgt.common.enums;

/**
 * @author Gyges Zean
 * @date 2018/6/25
 */
public enum AlertCategory {

    CONNECT_DIST(1L),
    FLUME_COLLECTOR(2L),
    SPARK_CLUSTER(3L),
    KAFKA_BROKER(4L),

    ;


    private Long code;

    AlertCategory(Long i) {
        this.code = i;
    }


    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }
}
