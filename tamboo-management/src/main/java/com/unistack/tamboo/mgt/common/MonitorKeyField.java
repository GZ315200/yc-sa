package com.unistack.tamboo.mgt.common;

/**
 * @author Gyges Zean
 * @date 2018/7/19
 *
 * this class supply for some configuration of monitor Brokers and Zookeeper metric's keys
 */
public class MonitorKeyField {


    public static  String ZOOKEEPER_METRICS = "zookeeperMetrics";

    public static  String ZOOKEEPER_METRICS_DATA = "zookeeperMetricsData";

    public static  String BROKERS_METRICS = "brokersMetrics";

    public static  String CONNECT_METRICS = "connectMetrics";

    public static  String KAFKA_SERVER = "kafkaServer";

    public static  String KAFKA_CONTROLLER= "kafkaController";
    public static  String SESSION_EXPIRE_LISTENER = "SessionExpireListener";
    public static  String ZOOKEEPER_CLIENT_METRICS = "ZooKeeperClientMetrics";

    public static  String BROKER_STATE = "BrokerState";
    public static  String CLUSTER_ID = "ClusterId";
    public static  String CONTROLLER_STATE = "ControllerState";
    public static  String GLOBAL_PARTITION_COUNT = "GlobalPartitionCount";
    public static  String PREFERRED_REPLICA_IMBALANCE_COUNT = "PreferredReplicaImbalanceCount";

    public static  String ACTIVE_CONTROLLER_COUNT = "ActiveControllerCount";
    public static  String GLOBAL_TOPIC_COUNT = "GlobalTopicCount";
    public static  String OFFLINE_PARTITION_COUNT = "OfflinePartitionsCount";



    public static  String HOST = "host";
    public static  String BROKER_ID = "brokerId";

    public static  String SESSION_STATE = "SessionState";
    public static  String ZOOKEEPER_REQUEST_LATENCY_MS = "ZooKeeperRequestLatencyMs";


    public static  String TIMESTAMP = "timestamp";


    protected interface BrokerState {
        String ALIVE = "ALIVE";
        String DOWN = "DOWN";
    }

}
