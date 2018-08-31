package com.unistack.tamboo.mgt.common.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @program: tamboo-sa
 * @description: 关于dataSource和Resource模块应用的枚举
 * @author: Asasin
 * @create: 2018-06-02 17:33
 **/
public class DataSourceEnum{
    public static enum DataSourceState {
        STOPPING(2, "dataSource-停止中"),
        STOPPED(0, "dataSource-已停止"),
        STARTING(3,"dataSource-启动中"),
        STARTED(1, "dataSource-已启动");
        private int state;
        private String tip;

        private DataSourceState(int state, String tip) {
            this.state = state;
            this.tip = tip;
        }

        public int getValue() {
            return this.state;
        }

        public int getResolvedValue() {
            return 1;
        }

        public String getTip() {
            return this.tip;
        }
    }

    public static enum TopicType {
        SOURCE(1, "数据源Topic"),
        OPEN(2, "开放Topic"),
        CLOSE(0, "临时Topic");

        private int state;
        private String tip;

        private TopicType(int state, String tip) {
            this.state = state;
            this.tip = tip;
        }

        public int getValue() {
            return this.state;
        }

        public int getResolvedValue() {
            return 1;
        }

        public String getTip() {
            return this.tip;
        }
    }

    public static enum ConnectMode {
        BULK(0, "bulk"),
        INCREAMENT(1, "incrementing"),
        TIMESTAMP(2, "timestamp"),
        INCREAANDTIME(3, "timestamp+incrementing");

        private int state;
        private String tip;

        private ConnectMode(int state, String tip) {
            this.state = state;
            this.tip = tip;
        }

        public int getValue() {
            return this.state;
        }

        public String getTip() {
            return this.tip;
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
            for (DataSourceEnum.DriverClass driverClass : DataSourceEnum.DriverClass.values()){
                if (StringUtils.equalsIgnoreCase(driverClass.dataBaseType, dataBaseType)) {
                    return driverClass.driverClass;
                }
            }
            return null;
        }
    }


}
    