package com.unistack.tamboo.compute.utils.spark;

import org.apache.log4j.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author hero.li
 */
public class CalcConfig{
    private static Logger LOGGER = Logger.getLogger(CalcConfig.class);
    /**
     * 计算模块
     */
    public static  final Boolean CALC_IS_TEST;
    public static  final String CALC_MASTER_IP;
    public static  final String CALC_SPARK_DEPLOY_DIR;
    public static  final int CALC_JOIN_DBPOOL_INITIAL_SIZE;
    public static  final int CALC_JOIN_DBPOOL_MAX_TOTAL;
    public static  final int CALC_JOIN_DBPOOL_MAX_IDLE;
    public static  final int CALC_JOIN_DBPOOL_MIN_IDLE;
    public static  final int CALC_JOIN_DBPOOL_MAX_WAIT_MILLIS;
    public static  final String CALC_SPARK_STREAMING_KAFKA_MAX_RATE_PER_PARTITION;
    public static  final String CALC_SPARK_DEPLOY_MODEL;
    public static  final String CALC_MULTISQL_CHECK_POINT_PATH;
    public static  final String KAFKA_BROKER_LIST;
    public static  final String KAFKA_REGISTRY_URL;
    public static  final String REDIS_HOST;
    public static  final String REDIS_PASSWORD;
    public static  final int CALC_BATCH_INTERVAL;
    public static  final int REDIS_PORT;
    public static  final String CALC_UPLOAD_JAR_PATH;
    public static  final String CALC_JDBC_URL;
    public static  final String CALC_JDBC_USER;
    public static  final String CALC_JDBC_PASSWORD;

    static{
            /**
             * 这么做的原因是在集群模式下
             * 配置文件放置在不同位置，Driver和Worker端都是同一套代码，所有在这里做一个兼容
             */
            File fileWorker = new File("application.properties");
            File fileDriver = new File("config"+File.separator+"application.properties");
            File file = fileDriver.exists() ? fileDriver : fileWorker;
            if(!file.exists()){throw new RuntimeException("SPARK:配置文件不存在,"+file.getAbsolutePath());}

            Properties p = new Properties();
            try(FileInputStream in = new FileInputStream(file)){
                p.load(in);
            }catch(IOException e){
                e.printStackTrace();
            }

            CALC_IS_TEST = "T".equalsIgnoreCase(p.getProperty("calc.isTest"));
            CALC_MASTER_IP = p.getProperty("calc.spark.master_ip");
            CALC_SPARK_DEPLOY_DIR = p.getProperty("calc.spark.deploy.dir");
            CALC_JOIN_DBPOOL_INITIAL_SIZE = Integer.parseInt(p.getProperty("calc.join.dbPool.initialSize"));
            CALC_JOIN_DBPOOL_MAX_TOTAL = Integer.parseInt(p.getProperty("calc.join.dbPool.maxTotal"));
            CALC_JOIN_DBPOOL_MAX_IDLE = Integer.parseInt(p.getProperty("calc.join.dbPool.maxIdle"));
            CALC_JOIN_DBPOOL_MIN_IDLE = Integer.parseInt(p.getProperty("calc.join.dbPool.minIdle"));
            CALC_JOIN_DBPOOL_MAX_WAIT_MILLIS = Integer.parseInt(p.getProperty("calc.join.dbPool.maxWaitMillis"));
            CALC_SPARK_STREAMING_KAFKA_MAX_RATE_PER_PARTITION = p.getProperty("calc.spark.streaming.kafka.maxRatePerPartition");
            CALC_SPARK_DEPLOY_MODEL = p.getProperty("calc.spark.deploy.model");
            CALC_MULTISQL_CHECK_POINT_PATH = p.getProperty("calc.multiSql.check.point.path");
            KAFKA_BROKER_LIST = p.getProperty("kafka.broker_list");
            KAFKA_REGISTRY_URL = p.getProperty("kafka.registry.url");
            CALC_BATCH_INTERVAL = Integer.parseInt(p.getProperty("calc.batchInterval"));
            REDIS_HOST = p.getProperty("spring.redis.host");
            REDIS_PORT = Integer.parseInt(p.getProperty("spring.redis.port"));
            REDIS_PASSWORD = p.getProperty("spring.redis.password");
            CALC_UPLOAD_JAR_PATH = p.getProperty("calc.upload.jar.path");
            CALC_JDBC_URL = p.getProperty("mgt.dataSource.jdbc.url");
            CALC_JDBC_USER = p.getProperty("mgt.dataSource.jdbc.username");
            CALC_JDBC_PASSWORD = p.getProperty("mgt.dataSource.jdbc.password");
    }
}