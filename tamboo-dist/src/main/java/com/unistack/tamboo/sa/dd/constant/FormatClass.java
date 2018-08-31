package com.unistack.tamboo.sa.dd.constant;

/**
 * @author anning
 * @date 2018/5/30 上午10:24
 * @description: format class hdfs中涉及
 */
public enum FormatClass {
    AVRO("io.confluent.connect.hdfs.avro.AvroFormat"),
    PARQUET("io.confluent.connect.hdfs.parquet.ParquetFormat"),
    STRING("io.confluent.connect.hdfs.string.StringFormat"),
    JSON("io.confluent.connect.hdfs.json.JsonFormat");

    private String formatClass;
    FormatClass(String s) {
        formatClass = s;
    }

    public String getFormatClass() {
        return formatClass;
    }



}
