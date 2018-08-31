package com.unistack.tamboo.sa.dd2.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * @author anning
 * @date 2018/7/20 下午5:45
 * @description: 常量
 */
public class Constants {
    public static void main(String[] args) {
        System.out.println(Constants.ConverterType.AVRO.converter);
    }
    public static  String HTTP_PRE = "http://";
    public static  String IS_SUCCEED = "isSucceed";
    public enum InsertMode{
        INSERT("insert"),
        UPSERT("upsert"),
        UPDATE("update");

        public String mode;
        InsertMode(String mode) {
            this.mode = mode;
        }
    }

    public enum Pk_mode {
        NONE("none"),
        KAFKA("kafka"),
        RECORD_KEY("record_key"),
        RECORD_VALUE("record_value");

        public String mode;

        Pk_mode(String mode) {
            this.mode = mode;
        }
    }

    public enum ConnectorClass {
        JDBC_SINK_CONNECTOR("JdbcSinkConnector"),
        JDBC_SOURCE_CONNECTOR("JdbcSourceConnector"),
        ES_SINK_CONNECTOR("ElasticsearchSinkConnector"),
        HDFS_SINK_CONNECTOR("HdfsSinkConnector"),
        FILE_SINK_CONNECTOR("FileStreamSink");
        public String cClazz;

        ConnectorClass(String cClazz) {
            this.cClazz = cClazz;
        }
    }


    public enum ConverterType {
        STRING("org.apache.kafka.connect.storage.StringConverter"),
        JSON("org.apache.kafka.connect.json.JsonConverter"),
        AVRO("io.confluent.connect.avro.AvroConverter");

        public String converter;

        ConverterType(String converter) {
            this.converter = converter;
        }
    }

    /**
     * Jdbc Driver class
     */
    public enum DriverClass {
        MYSQL("com.mysql.jdbc.Driver", "mysql"),
        ORACLE("oracle.jdbc.driver.OracleDriver", "oracle"),
        SQLServer("com.microsoft.sqlserver.jdbc.SQLServerDriver", "sqlserver"),
        POSTGRE("org.postgresql.Driver", "postgre");

        private String driverClass;
        private String dataBaseType;

        DriverClass(String driverClass, String dataBaseType) {
            this.driverClass = driverClass;
            this.dataBaseType = dataBaseType;
        }

        public static String getDriverClassByDatabaseTyoe(String dataBaseType) {
            for (DriverClass driverClass :DriverClass.values()){
                if (StringUtils.equalsIgnoreCase(driverClass.dataBaseType, dataBaseType)) {
                    return driverClass.driverClass;
                }
            }
            return null;
        }
    }

    /**
     * 数据下发Hdfs时可选的Format
     */
    public enum HdfsFormatClass {
        //avro
        AVRO("io.confluent.connect.hdfs.avro.AvroFormat"),
        //parquent
        PARQUET("io.confluent.connect.hdfs.parquet.ParquetFormat"),
        //string
        STRING("io.confluent.connect.hdfs.string.StringFormat"),
        //json
        JSON("io.confluent.connect.hdfs.json.JsonFormat");

        private String formatClass;

        HdfsFormatClass(String s) {
            this.formatClass = s;
        }

        public String getFormatClass() {
            return this.formatClass;
        }
    }
}
