package com.unistack.tamboo.commons.utils;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.Boolean;
import java.util.List;
import java.util.Properties;

/**
 * @author hero.li
 */
public class TambooConfig {
    private static Logger LOGGER = LoggerFactory.getLogger(TambooConfig.class);
    /*
     * NOTE: DO NOT CHANGE EITHER CONFIG NAMES AS THESE ARE PART OF THE PUBLIC
     * API AND CHANGE WILL BREAK USER CODE.
     */
    public static final String TOPIC_CONFIG = "topic";
    public static final String PUBLIC_CREDENTIAL_CONFIG = "publicCredential";
    public static final String PRIVATE_CREDENTIAL_CONFIG = "privateCredential";
    public static final String METRICS_REPORT_INTERVAL_CONFIG = "metrics_report_interval";
    public static final String DEFAULT_METRICS_REPORT_INTERVAL = "10000";
    public static final String POLL_TIMEOUT_CONFIG = "poll_timeout";
    public static final String DEFAULT_POLL_TIMEOUT = "1000";
    public static final List<String> SENSITIVE_CONFIGS;
    public static final int CALC_BATCH_INTERVAL;

    public static final String SYSTEM_QUEUE;

    static{
        SENSITIVE_CONFIGS = Lists.newArrayList(TOPIC_CONFIG, PUBLIC_CREDENTIAL_CONFIG, PRIVATE_CREDENTIAL_CONFIG);
    }


    /**
     * 计算模块
     */
    public static final Boolean CALC_IS_TEST;
    public static final String CALC_CLUSTER_IPS;
    public static final String CALC_MASTER_IP;
    public static final String CALC_SPARK_DEPLOY_DIR;
    public static final int CALC_JOIN_DBPOOL_INITIAL_SIZE;
    public static final int CALC_JOIN_DBPOOL_MAX_TOTAL;
    public static final int CALC_JOIN_DBPOOL_MAX_IDLE;
    public static final int CALC_JOIN_DBPOOL_MIN_IDLE;
    public static final int CALC_JOIN_DBPOOL_MAX_WAIT_MILLIS;
    public static final String CALC_SPARK_STREAMING_KAFKA_MAX_RATE_PER_PARTITION;
    public static final String CALC_SPARK_DEPLOY_MODEL;
    public static final String CALC_MULTISQL_CHECK_POINT_PATH;
    public static final String CALC_UPLOAD_JAR_PATH;
    public static final String CALC_MAIN_CLASS;
    public static final String CALC_JAR_PATH;
    public static final String CALC_LIVY_SERVER_URL;
    public static final String CALC_MONITOR_PORT;

    /**
     * REDIS相关配置文件
     */
    public static String REDIS_HOST;
    public static int REDIS_PORT;
    public static final String REDIS_PASSWORD;


    /**
     * zookeeper
     */
    public static final String ZOOKEEPER_HOST;
    public static final String ZOOKEEPER_JMXPORT;

    /**
     * kafka相关
     */
    public static final String KAFKA_BROKER_LIST;
    public static final String KAFKA_CONNECTOR_URL;
    public static final String KAFKA_REGISTRY_URL;
    public static final String ZOOKEEPER_URL;
    public static final int KAFKA_PARTITION;
    public static final int KAFKA_REPLICA;
    public static final String CONNECT_JMXPORT;

    /**
     * collect相关
     */
    public static final String COLLECT_FLUME_PATH;
    public static final String DATASOURCE_CONF_PATH;

    static{
        Properties p = new Properties();
        FileInputStream in = null;

        /**
         * 对配置文件做一个兼容处理,当在"正常"路径下找不到配置文件时会在"非正常"路径下寻找配置文件,对于其他的模块不会产生影响
         * 所谓的正常路径是原来规定好的路径 当前工作目录下的config下的application.properties 文件
         * 非正常路径是指 spark集群使用的路径  => spark集群会把jar包拷贝别的目录下执行,而配置文件不在sparkJar同目录的config下
         * 修改人:李耀辉
         */
        String path = System.getProperty("user.dir") + "/config/application.properties";
        File file1 = new File(path);
        File file2 = new File("application.properties");
        File file = file1.exists() ? file1 : file2;
        try {
            if(file.exists()){
                in = new FileInputStream(file);
                p.load(in);
            }else {
                throw new RuntimeException(file.getAbsolutePath()+" 下配置文件不存在!");
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try {if (in != null){in.close();}} catch (IOException e){e.printStackTrace();}
        }

        CALC_IS_TEST = "T".equalsIgnoreCase(p.getProperty("calc.isTest"));
        CALC_CLUSTER_IPS = p.getProperty("calc.spark.cluster_ips");
        CALC_MASTER_IP = p.getProperty("calc.spark.master_ip");
        CALC_SPARK_DEPLOY_DIR = p.getProperty("calc.spark.deploy.dir");
        REDIS_HOST = p.getProperty("spring.redis.host");
        REDIS_PORT = Integer.parseInt(p.getProperty("spring.redis.port"));
        REDIS_PASSWORD = p.getProperty("spring.redis.password");
        KAFKA_BROKER_LIST = p.getProperty("kafka.broker_list");
        KAFKA_CONNECTOR_URL = p.getProperty("kafka.connect.url");
        KAFKA_REGISTRY_URL = p.getProperty("kafka.registry.url");
        ZOOKEEPER_URL = p.getProperty("zookeeper.url");
        ZOOKEEPER_HOST = p.getProperty("zookeeper.host");
        ZOOKEEPER_JMXPORT = p.getProperty("zookeeper.jmxPort");
        COLLECT_FLUME_PATH = p.getProperty("flume.path");
        DATASOURCE_CONF_PATH = p.getProperty("dataSource.config.path");
        KAFKA_PARTITION = Integer.parseInt(p.getProperty("kafka.partition"));
        KAFKA_REPLICA = Integer.parseInt(p.getProperty("kafka.replica"));
        CONNECT_JMXPORT = p.getProperty("kafka.connect.jmxPort");
        CALC_BATCH_INTERVAL = Integer.parseInt(p.getProperty("calc.batchInterval"));
        CALC_MONITOR_PORT = p.getProperty("calc.monitor.port");

        CALC_JOIN_DBPOOL_INITIAL_SIZE = Integer.parseInt(p.getProperty("calc.join.dbPool.initialSize"));
        CALC_JOIN_DBPOOL_MAX_TOTAL = Integer.parseInt(p.getProperty("calc.join.dbPool.maxTotal"));
        CALC_JOIN_DBPOOL_MAX_IDLE = Integer.parseInt(p.getProperty("calc.join.dbPool.maxIdle"));
        CALC_JOIN_DBPOOL_MIN_IDLE = Integer.parseInt(p.getProperty("calc.join.dbPool.minIdle"));
        CALC_JOIN_DBPOOL_MAX_WAIT_MILLIS = Integer.parseInt(p.getProperty("calc.join.dbPool.maxWaitMillis"));
        CALC_SPARK_STREAMING_KAFKA_MAX_RATE_PER_PARTITION = p.getProperty("calc.spark.streaming.kafka.maxRatePerPartition");
        CALC_SPARK_DEPLOY_MODEL = p.getProperty("calc.spark.deploy.model");
        CALC_MULTISQL_CHECK_POINT_PATH = p.getProperty("calc.multiSql.check.point.path");
        CALC_UPLOAD_JAR_PATH = p.getProperty("calc.upload.jar.path");
//        CALC_MULTISQL_GETSCHEMA_URL = p.getProperty("calc.multiSql.getschema.url");

        SYSTEM_QUEUE = p.getProperty("user.queue.root");
        CALC_MAIN_CLASS = p.getProperty("calc.main.class");
        CALC_JAR_PATH = p.getProperty("calc.jar.path");
        CALC_LIVY_SERVER_URL = p.getProperty("calc.livy.server.url");

    }
}