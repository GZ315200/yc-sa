package com.unistack.tamboo.compute.calc;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.StreamProcess;
import com.unistack.tamboo.compute.process.impl.CleanProcess;
import com.unistack.tamboo.compute.process.impl.JoinProcess;
import com.unistack.tamboo.compute.process.impl.MultiSqlProcess;
import com.unistack.tamboo.compute.process.impl.SplitProcess;
import com.unistack.tamboo.compute.utils.KafkaUtil;
import com.unistack.tamboo.compute.utils.commons.JDBCUtil;
import com.unistack.tamboo.compute.utils.spark.CalcConfig;
import com.unistack.tamboo.compute.utils.spark.CalcType;
import com.unistack.tamboo.compute.utils.spark.CalcUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;


/**
 * @author hero.li
 */
public class CalcStart implements java.io.Serializable{
    private static Logger LOGGER = Logger.getLogger(CalcStart.class);
    public static JavaStreamingContext STREAMING_CONTEXT = null;
    private static JSONObject KAFKA_CONF = null;
    private static String TYPE = null;
    private static JSONObject GLOBAL_CONFIG;
    private static SparkConf SPARK_CONF;
    private static String APP_NAME;

    /**
     * 如果接受不到数据
     * 1、注意acl.conf的配置是否是正确的用户名和密码
     * 2、注意消费策略是否为earliest
     * 3、查看topic是不是真的有数据
     * 4、看看是不是kafka本身的原因
     * @param args
     */
    public static void main(String[] args){
        //1、加载日志文件
        File log_conf_file;
        File f1 = new File("config"+File.separator+"log4j.properties");
        File f2 = new File("log4j.properties");
        log_conf_file = f1.exists() ? f1 : f2;
        PropertyConfigurator.configure(log_conf_file.getAbsolutePath());

        //2、读取配置,解析配置
        APP_NAME = (args.length > 0 && !"".equals(args[0])) ? args[0] : "acl";
        String config = "";
        if(CalcConfig.CALC_IS_TEST){
            config = CalcUtil.readConfig();
        }else{
            JSONObject configResult = JDBCUtil.getAppConf(APP_NAME);
            config = configResult.getString("calcConf");
        }

        if(StringUtils.isBlank(config)){
            LOGGER.error("SPARK:从数据库/配置文件中获取的配置信息为空! 是否为测试环境:"+CalcConfig.CALC_IS_TEST);
            return;
        }
        LOGGER.info("SPARK:任务运行模式:"+(CalcConfig.CALC_IS_TEST?"本地":"集群")+"   appName:"+APP_NAME+"    配置信息:"+config);

        try{
            GLOBAL_CONFIG = JSONObject.parseObject(config);
            KAFKA_CONF = GLOBAL_CONFIG.getJSONObject("kafka");
            TYPE  = GLOBAL_CONFIG.getString("type");
        }catch(Exception e){
            LOGGER.error("SPARK:任务[" + APP_NAME + "]开启失败!原因:配置信息不是标准的json格式,config:" + config);
            return;
        }

        //3、初始化sparkContext
        if(CalcConfig.CALC_IS_TEST){
            File conf_file1 = new File("temp"+File.separator+APP_NAME+".conf");
            String conf_file = conf_file1.exists() ? conf_file1.getAbsolutePath() : APP_NAME+".conf";
            LOGGER.info("SPARK:acl配置文件的路径:"+conf_file);
            System.setProperty("java.security.auth.login.config",conf_file);
        }

        SPARK_CONF = new SparkConf();
        SPARK_CONF.set("spark.streaming.kafka.maxRatePerPartition",CalcConfig.CALC_SPARK_STREAMING_KAFKA_MAX_RATE_PER_PARTITION);
        SPARK_CONF.setAppName(APP_NAME);
        if(CalcConfig.CALC_IS_TEST){SPARK_CONF.setMaster("local[4]");}
        LOGGER.info("SPARK:任务计算准备启动,计算类型:"+TYPE);

        //4、根据类型的不同启动不同的计算
        switch(TYPE){
            case CalcType.CLEAN:
                calcStart(new CleanProcess(GLOBAL_CONFIG));          break;
            case CalcType.JOIN:
                calcStart(new JoinProcess(GLOBAL_CONFIG));           break;
            case CalcType.SPLIT:
                calcStart(new SplitProcess(GLOBAL_CONFIG));          break;
            case CalcType.SQL:
                new MultiSqlProcess(GLOBAL_CONFIG).logic(null); break;
            default:
                throw new RuntimeException(TYPE + "类型未定义!程序退出");
        }
    }

    /**
     * 正真计算的方法，使用策略模式，动态替换 StreamProcess 的实现即可<br/>
     * @param process            流数据处理类a
     */
    public static void calcStart(StreamProcess process){
        LOGGER.info("SPARK:任务开启准备!");
        String interval = KAFKA_CONF.getString("batchInterval");
        int batchInterval = (null == interval || "".equals(interval)) ? 2000 : Integer.parseInt(interval);
        STREAMING_CONTEXT = new JavaStreamingContext(SPARK_CONF,Durations.milliseconds(batchInterval));

        LOGGER.info("SPARK:流任务上下文初始化成功!");
        Collection<String> topics = Arrays.asList(KAFKA_CONF.getString("from_topic"));

        Map<String, Object> kafkaInfo = KafkaUtil.getInputKafkaInfo();
        JavaInputDStream<ConsumerRecord<String, String>>
                        line = KafkaUtils.createDirectStream(STREAMING_CONTEXT,LocationStrategies.PreferConsistent(),ConsumerStrategies.<String, String>Subscribe(topics, kafkaInfo));

        LOGGER.info("SPARK:kafka连接成功!");
        process.logic(line);
        STREAMING_CONTEXT.start();
        LOGGER.info("SPARK:任务开启成功!");

        if(!CalcConfig.CALC_IS_TEST){
            CalcStop lunchStop = new CalcStop(APP_NAME, STREAMING_CONTEXT);
            lunchStop.lunchStop();
        }else{
            try{STREAMING_CONTEXT.awaitTermination();}catch (InterruptedException e){e.printStackTrace();}
        }
    }
}