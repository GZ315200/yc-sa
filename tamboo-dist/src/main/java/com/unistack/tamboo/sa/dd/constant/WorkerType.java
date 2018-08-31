package com.unistack.tamboo.sa.dd.constant;

/**
 * @author anning
 * @date 2018/6/4 上午10:54
 * @description: woker type
 */
public enum WorkerType {
    KAFKA("com.unistack.tamboo.sa.dd.kafka.ConsumerKafkaWorker", "kafka", ""),
    MYSQL("com.unistack.tamboo.sa.dd.jdbc.ConsumerMYSQLWorker", "mysql", "com.unistack.tamboo.sa.dd.jdbc.JdbcConsumerGroup"),
    ORACLE("com.unistack.tamboo.sa.dd.jdbc.ConsumerORACLEWorker", "oracle", "com.unistack.tamboo.sa.dd.jdbc.JdbcConsumerGroup"),
    SQLSERVER("com.unistack.tamboo.sa.dd.jdbc.ConsumerSQLSERVERWorker","sqlserver","com.unistack.tamboo.sa.dd.jdbc.JdbcConsumerGroup"),
    POSTGRE("com.unistack.tamboo.sa.dd.jdbc.ConsumerPOSTGREWorker","postgre","com.unistack.tamboo.sa.dd.jdbc.JdbcConsumerGroup"),
    FILE("com.unistack.tamboo.sa.dd.file.ConsumerFileWorker", "file", "com.unistack.tamboo.sa.dd.file.FileConsumerGroup");
    private String clazz;
    private String name;
    private String groupType;

    WorkerType(String clazz, String name, String groupType) {
        this.clazz = clazz;
        this.name = name;
        this.groupType = groupType;
    }

    public String getClazz() {
        return clazz;
    }

    public String getGroupType() {
        return groupType;
    }

    public static WorkerType getClazzByName(String name) {

        for (WorkerType workerType :
                WorkerType.values()) {
            if (name.equals(workerType.name)) {
                return workerType;
            }
        }
        return null;
    }


}
