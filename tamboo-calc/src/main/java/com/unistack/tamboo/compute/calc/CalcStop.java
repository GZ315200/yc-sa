package com.unistack.tamboo.compute.calc;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.utils.commons.JDBCUtil;
import org.apache.log4j.Logger;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;
import java.util.Properties;


/**
 * @author hero.li
 * 这个类用来关闭计算任务 <br/>
 * 具体的思路是:
 *    定时轮询数据库查看data_flow_info表的flag字段是否为0 <br/>
 *    如果为0就表示这个应用停止要停止 <br/>
 */
public class CalcStop {
    private static Logger LOGGER = Logger.getLogger(CalcStop.class);
    private static Connection CONN;
    private String type;
    private String appName;
    private JavaStreamingContext jssc;
    private StreamingQuery query;
    private long count = 0;

    static{
         File f1 = new File("config"+File.separator+"application.properties");
         File f2 = new File("application.properties");
         File f  = f1.exists() ? f1 : f2;
         InputStream in = null;
         try{
             new FileInputStream(f.getAbsoluteFile());
             in = new FileInputStream(f.getAbsoluteFile());
             Properties p = new Properties();
             p.load(in);

             Class.forName("com.mysql.jdbc.Driver");
             String url = p.getProperty("mgt.dataSource.jdbc.url");
             String user = p.getProperty("mgt.dataSource.jdbc.username");
             String password = p.getProperty("mgt.dataSource.jdbc.password");
             CONN = DriverManager.getConnection(url,user,password);
             LOGGER.info("SPARK:数据库连接池创建成功!");
         }catch(Exception e){
             e.printStackTrace();
             LOGGER.info("SPARK:数据库连接池创建异常!",e);
         }finally{
             if(null != in){try{in.close();}catch(IOException e){e.printStackTrace();}}
         }
    }


    public CalcStop(String appName,Object jssc){
        this.appName = appName;
        Class clazz =  jssc.getClass();
        LOGGER.info("SPARK:停止线程初始化,要停止的上下文:"+clazz.getName());
        boolean isStreaming = JavaStreamingContext.class == clazz;
        if(isStreaming){
            this.jssc = (JavaStreamingContext)jssc;
            this.type = "streaming";
        }

        boolean isQuery = jssc instanceof StreamingQuery;
        if(isQuery){
            this.query = (StreamingQuery)jssc;
            this.type = "query";
        }

        if(!isStreaming && !isQuery){
            LOGGER.error("SPARK:未知的上下文:"+jssc.getClass().getName());
            throw new RuntimeException("SPARK:未知的上下文:"+jssc.getClass().getName());
        }
    }

    public void lunchStop(){
        switch(type){
            case "streaming": calcStop("streaming");  break;
            case "query":     calcStop("query");      break;
            default:          throw new RuntimeException("SPARK:无效的类型["+type+"]");
        }
    }

//    private void streamingStop(){
//        boolean isStoped = false;
//        while(!isStoped){
//            try{
//                isStoped = jssc.awaitTerminationOrTimeout(5000);
//                if(isStoped){
//                    LOGGER.info("SPARK:流计算程序appName=["+appName+"]准备关闭,这可能需要一段时间,请耐心等待!");
//                    jssc.stop(true,true);
//                    LOGGER.info("SPARK:流计算程序appName=["+appName+"]安全关闭");
//                    return;
//                }
//            }catch(InterruptedException e){
//                e.printStackTrace();
//                LOGGER.error("SPARK:流计算程序appName=["+appName+"]异常",e);
//                return;
//            }
//        }
//    }


    private void calcStop(String type){
        boolean isStopped = false;
        while(!isStopped){
            try{
                ++count;
                if("query".equalsIgnoreCase(type)){
                    isStopped = query.awaitTermination(5000);
                }else{
                    isStopped = jssc.awaitTerminationOrTimeout(5000);
                }

                Optional<JSONObject> op_dataFlowInfo = JDBCUtil.getDataFlowInfo(CONN, appName);
                if(!op_dataFlowInfo.isPresent()){
                    LOGGER.info("SPARK:计算停止线程appName["+appName+"],第["+(count)+"]次轮询时查询数据库失败!程序跳过");
                    break;
                }

                JSONObject flowInfo = op_dataFlowInfo.get();

                boolean isRunning = !"0".equalsIgnoreCase(flowInfo.getString("flag"));
                LOGGER.info("SPARK:结构化流计算程序appName=["+appName+"]轮询["+count+"]次,是否运行:"+isRunning);
                if(!isRunning){
                    LOGGER.info("SPARK:结构化流计算程序appName=["+appName+"]准备关闭,这可能需要一段时间,请耐心等待!");
                    if("query".equals(type)){
                        this.query.stop();
                    }else{
                        this.jssc.stop(true,true);
                    }
                    LOGGER.info("SPARK:结构化流计算程序appName=["+appName+"]安全关闭");
                    return;
                }
            }catch(Exception e){
                LOGGER.error("SPARK:流计算程序appName=["+appName+"]异常",e);
                return;
            }
        }
    }





//    @Override
//    public void run(){
//        LOGGER.info("SPARK:appName=["+appName+"]注册了定时关闭");
//        for(;;){
//            try{
//                LOGGER.info("SPARK:准备睡5s");
//                TimeUnit.SECONDS.sleep(5);
//                ++count;
//                LOGGER.info("SPARK:5s睡醒了，准备从数据库拉取监控数据");
//                Optional<JSONObject> dataFlowInfo = JDBCUtil.getDataFlowInfo(CONN, appName);
//                if(!dataFlowInfo.isPresent()){
//                    LOGGER.info("SPARK:计算停止线程appName["+appName+"],第["+(count)+"]次轮询时查询数据库失败!程序跳过");
//                    continue;
//                }
//                LOGGER.info("SPARK:从数据库拉取的监控数据:"+dataFlowInfo.get());
//                String flag = dataFlowInfo.get().getString("flag");
//                boolean  isRunning = !"0".equalsIgnoreCase(flag);
//                LOGGER.info("SPARK:计算停止线程appName=["+appName+"] 轮询["+(count)+"]次,是否在运行:"+isRunning);
//                if(!isRunning){
//                    if(null != this.jssc){
//                        LOGGER.info("SPARK:流计算程序appName=["+appName+"]准备关闭,这可能需要一段时间,请耐心等待!");
//                        this.jssc.stop(true);
//                        LOGGER.info("SPARK:流计算程序appName=["+appName+"]安全关闭");
//                        break;
//                    }
//
//                    if(null != this.query){
//                        LOGGER.info("SPARK:结构化流计算程序appName=["+appName+"]准备关闭,这可能需要一段时间,请耐心等待!");
//                        query.stop();
//                        LOGGER.info("SPARK:结构化流计算程序appName=["+appName+"]安全关闭");
//                        break;
//                    }
//                    LOGGER.error("SPARK:计算程序关闭异常");
//                }
//            }catch(InterruptedException e){
//                e.printStackTrace();
//                LOGGER.error("SPARK:计算停止线程中断/异常appName="+appName,e);
//            }
//        }
//    }

//    public static void main(String[] args){
//        File log_conf_file;
//        File f1 = new File("config"+File.separator+"log4j.properties");
//        File f2 = new File("log4j.properties");
//        log_conf_file = f1.exists() ? f1 : f2;
//        PropertyConfigurator.configure(log_conf_file.getAbsolutePath());
//
//        CalcStop cs = new CalcStop("calc_source_test_lyzx_test_14_20180822174533","");
//        new Thread(cs,"AAA").start();
//        try{
//            Thread.sleep(Integer.MAX_VALUE);
//        } catch (InterruptedException e){
//            e.printStackTrace();
//        }
//    }
}