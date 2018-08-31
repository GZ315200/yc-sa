package com.unistack.tamboo.sa.dd.constant;

/**
 * @author anning
 * @date 2018/5/22 上午10:20
 */
public enum DdType {
    MYSQL("com.unistack.tamboo.sa.dd.dist.JdbcSinkConnector", "mysql", "com.mysql.jdbc.Driver", "jdbc:mysql://%s:%s/%s"),
    ORACLE("com.unistack.tamboo.sa.dd.dist.JdbcSinkConnector", "oracle", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@%s:%s:%s"),
    SQLSERVER("","sqlserver","com.microsoft.sqlserver.jdbc.SQLServerDriver","jdbc:sqlserver://%s:%s;databaseName=%s"),
    POSTGRE("","postgre","org.postgresql.Driver","jdbc:postgresql://%s:%s/%s"),
    HDFS("com.unistack.tamboo.sa.dd.dist.HdfsSinkConnector","hdfs","",""),
    FILE("com.unistack.tamboo.sa.dd.dist.FileSinkConnector","file","",""),
    KAFKA("com.unistack.tamboo.sa.dd.dist.KafkaMqSink","kafka","",""),
    IBMMQ("com.unistack.tamboo.sa.dd.dist.IbmMqSink","ibm_mq","","");

    private String kinds;
    private String connectorType;
    private String driverClass;
    private String format;


    DdType(String kinds, String connectorType, String driverClass, String format) {
        this.kinds = kinds;
        this.connectorType = connectorType;
        this.driverClass = driverClass;
        this.format = format;
    }

    public String getKinds() {
        return kinds;
    }

    public String getConnectorType() {
        return connectorType;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getFormat() {
        return format;
    }

    public static DdType getDdTypeByName(String typeName) {
        for (DdType ddType:
             DdType.values()) {
            if (typeName.equals(ddType.connectorType))
                return ddType;
        }
        return null;
    }
}
