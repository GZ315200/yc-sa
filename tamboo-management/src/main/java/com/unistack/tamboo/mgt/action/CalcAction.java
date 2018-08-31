package com.unistack.tamboo.mgt.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.commons.utils.enums.EDatabaseType;
import com.unistack.tamboo.compute.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import java.io.*;

/**
 * @author hero.li
 */
public class CalcAction{
    private static  Logger LOGGER = LoggerFactory.getLogger(CalcAction.class);

    /**
     * 通过type类型开启不同类型的计算任务 <br/>
     * @param json
     *
     *     数据清洗格式:     {"template":[{"filedCount":1,"rule":"A.B.C","type":"Contains","fields":[{"rule":"A.B.C","value":"df"}],"value":"df","fieldsArr":[{"label":"JSON属性","type":"String","key":"rule"},{"label":"JSON值","type":"String","key":"value"}]}],"type":11,"appName":"calc_liyaohui_admin_11_20180710104335","kafka":{"acl_username":"Tu_adhpoc5","from_topic":"liyaohui_admin","to_topic":"admin-383-vf-20180710104335-B2","group_id":"admin-383-vf-20180710104335-20180710104335","acl_password":"Tp_nbrvwwp"}}
     *     数据join格式:    {"template":{"streamTable":["test1"],"datasources":[{"creator":"admin","addTime":1530849832000,"ip":"192.168.1.191","dbName":"test","dbType":"MYSQL","dbtable":"t1","dataSourceDesc":"193上面的test库","isActive":"1","dataSourceName":"191_test","dataSourceId":22576,"password":"welcome1","port":"3308","username":"root"}],"windowLen":2,"windowSlide":1,"group":["admin"],"sql":"select * from test1"},"type":14,"appName":"calc_test1_user_14_20180728114637","kafka":{"acl_username":"Tu_adhpoc5","from_topic":"test1_user","to_topic":"admin-383-vvf-1532749592836-E1","group_id":"admin-383-vvf-20180728114637","acl_password":"Tp_nbrvwwp"}}
     *     数据split格式:   {"template":[{"distributeRule":"xxx.ccc","topicName":""}],"type":13,"appName":"calc_syslog_test_admin_13_20180719160858","kafka":{"acl_username":"Tu_adhpoc5","from_topic":"syslog_test_admin","to_topic":"admin-383-cc-20180719160857-D2","group_id":"admin-383-cc-20180719160857-20180719160858","acl_password":"Tp_nbrvwwp"}}
     *     sql :           {"template":{"streamTable":[{"streamTable":"zc","alias":"zc_as"},{"streamTable":"an","alias":"an_as"}],"datasources":[{"creator":"admin","addTime":1530849832000,"ip":"192.168.1.191","dbName":"test","dbType":"MYSQL","dbtable":"lyzx_test_data2","dataSourceDesc":"193上面的test库","isActive":"1","dataSourceName":"191_test","dataSourceId":22576,"password":"welcome1","port":"3308","username":"root"}],"windowLen":2,"windowSlide":1,"group":[],"sql":"sssss"},"type":14,"appName":"calc_syslog_test_admin_14_20180730152345","kafka":{"acl_username":"Tu_adhpoc5","from_topic":"syslog_test_admin","to_topic":"admin-383-fff-1532935424770-E1","group_id":"admin-383-fff-20180730152345","acl_password":"Tp_nbrvwwp"}}
     *
     * @return {"code":"","msg":"","appName":""}
     * code:错误码 200表示正确,其余表示错误
     * msg: 错误描述
     * eg:
     * 正确开启计算任务的返回值: {"code":"200","msg":"","appName":"calc_123"}
     * 开启中途错误: {"code":"199","msg":"资源不足","appName":""}
     */
    public static JSONObject startCalc(String json){
        LOGGER.info("SPARK:任务启动配置文件为:"+json);
        JSONObject tempResult = checkConf(json);
        if(!"200".equals(tempResult.getString("code"))){
            LOGGER.error("SPARK:任务配置文件错误,"+tempResult);
            return tempResult;
        }

        JSONObject param = JSON.parseObject(json);
        JSONObject result = new JSONObject();
        String appName  = param.getString("appName");
        result.put("appName",appName);

        //3、启动脚本并执行脚本(目前脚本路径是写死的,就放在jar包同目录的config目录下)
        String shellPath = "bin";
        String shellName = "start_spark.sh";
        JSONObject kfkInfo = param.getJSONObject("kafka");
        String acl_username = kfkInfo.getString("acl_username");
        String acl_password = kfkInfo.getString("acl_password");

        /**
         * 在这个地方传参数给shell脚本
         * 因为在工程发布到服务器后需要通过脚本启动计算程序
         * 而此时脚本是需要这些参数的，所以要在这个地方把参数传递过去,通过本地的基本脚本登录远程服务器(含参数)
         * 在通过远程服务器端的脚本使用这些参数启动应用程序
         */
        ProcessBuilder pb;
        switch(TambooConfig.CALC_SPARK_DEPLOY_MODEL){
            case "standalone":
                pb = new ProcessBuilder("./"+shellName,"standalone",appName,acl_username,acl_password, TambooConfig.CALC_MASTER_IP, TambooConfig.CALC_SPARK_DEPLOY_DIR);
                break;
            case "yarn":
                pb = new ProcessBuilder("./"+shellName,"yarn",appName,acl_username,acl_password, TambooConfig.CALC_MASTER_IP, TambooConfig.CALC_SPARK_DEPLOY_DIR,param.getString("queue"));
                break;
            default:throw new RuntimeException("SPARK:运行模式配置错误,calc.spark.deploy.model="+TambooConfig.CALC_SPARK_DEPLOY_MODEL);
        }

        LOGGER.info("准备登陆远程服务器启动计算程序:appName="+appName+" ,acl_username="+acl_username+" ,acl_password="+acl_password);
        pb.directory(new File(shellPath));

        try{
            pb.start();
        }catch(Exception e){
            result.put("msg","ProcessBuilder.start()失败!");
            result.put("code","-1");
            LOGGER.error("SPARK:jvm启动进程失败!",e);
            return result;
        }

        /**
         * 把信息放入监控队列(内存中的一个Queue队列)
         */
//        CalcMonitor calcMonitor = new CalcMonitor();
//        calcMonitor.setUrl_cluster("http://192.168.1.201:4040/api/v1/applications");
//        calcMonitor.setApplicationName(appName);
//        MonitorQueue.setCaclQueue(calcMonitor);

        result.put("msg","");
        result.put("code","200");
        return result;
    }


    /**
     * @param json {"applicationName":"calc_abcdefghjklmnopqrst"}
     *             applicationName:应用程序id
     * @return {"code":"200","msg":"停止成功"}
     * code:错误码 200表示正确,其余表示错误
     * msg:错误描述,错误原因
     *
     * eg:
     * {"code":"200","msg":"停止成功"}
     * {"code":"201","msg":"没有这个任务 calc_abcdefghjklmnopqrst "}
     */
    public static JSONObject stopCalc(String json){
        JSONObject result = new JSONObject();
        try{
            JSONObject param = JSON.parseObject(json);
            if(StringUtils.isBlank(param.getString("appName"))){
                result.put("code", "199");
                result.put("msg", "SPARK:appName是必须的参数!");
                return result;
            }

            String applicationName = param.getString("appName");
            Jedis jedis = new RedisUtil(EDatabaseType.SPARK).getResource();
            try {
                Long count = jedis.del(applicationName);
                if(1 != count){
                    result.put("code", "198");
                    result.put("msg", "appName在redis中不存在");
                    return result;
                }
            }catch(Exception e){
                result.put("code", "198");
                result.put("msg", "redis客户端获取异常!");
                return result;
            }finally{
                if(null != jedis){jedis.close();}
            }

            result.put("code","200");
            result.put("msg", "任务关闭成功");
            return result;
        }catch(Exception e){
            e.printStackTrace();
            result.put("code", "205");
            result.put("msg", "任务关闭失败");
            return result;
        }
    }


    /**
     * 开启任务的参数检查,现阶段不检查任务所占用的核数和cpu资源 <br/>
     * @param jsonConfStr json格式的配置文件 <br/>
     * @return {"code":"200","error":"错误原因","description":"描述,在正确时查看这个描述"}
     * eg:
     * 完全正确:           {"code":"200","error":"","description":""}
     * 正确但是没有计算结果  {"code":"200","description":"该消息被过滤","error":""}
     * 错误消息:
     * {"code":"199","error":"参数为空","description":""}
     * {"code":"203","error":"SQL书写错误","description":""}
     */
    private static JSONObject checkConf(String jsonConfStr){
        JSONObject result = new JSONObject();
        if(StringUtils.isBlank(jsonConfStr)){
            result.put("code","-6");
            result.put("msg","消息不能为空!");
            return result;
        }

        JSONObject jsonConf;
        try{
            jsonConf = JSON.parseObject(jsonConfStr);
        }catch(Exception e){
            String error = "任务开启失败,参数不是标准的json格式!";
            LOGGER.error(error,e);
            result.put("code","-5");
            result.put("msg",error);
            return result;
        }

        String type = jsonConf.getString("type");
        String appName = jsonConf.getString("appName");
        if(StringUtils.isBlank(type) || StringUtils.isBlank(appName)){
            result.put("code", "-4");
            result.put("msg", "任务开启失败,启动信息必须包含[type]&&[appName],且不能为空!");
            return result;
        }

        Object o_kafka = jsonConf.get("kafka");
        Object o_template = jsonConf.get("template");
        String jsonObj = "JSONObject";
        if(null == o_kafka || null == o_template || !jsonObj.equals(o_kafka.getClass().getSimpleName()) || !jsonObj.equals(o_template.getClass().getSimpleName())){
            result.put("code","-3");
            result.put("msg","任务开启失败,启动信息必须包含[template]&&[kafka],不能为空且必须为对象,开启信息:"+jsonConf);
            return result;
        }

        JSONObject kafka = (JSONObject)o_kafka;
        String from_topic = kafka.getString("from_topic");
        String to_topic = kafka.getString("to_topic");
        String acl_password = kafka.getString("acl_password");
        String acl_username = kafka.getString("acl_username");

        if(StringUtils.isBlank(from_topic) || StringUtils.isBlank(to_topic) || StringUtils.isBlank(acl_password) || StringUtils.isBlank(acl_username)){
            result.put("code", "-2");
            result.put("msg", "任务开启失败,启动信息的kafka字段必须包含[from_topic]&&[to_topic]&&[acl_username]&&[acl_password]这4个参数,并且都不能为空!");
            return result;
        }
        result.put("code", "200");
        result.put("msg", "配置正确!");
        return result;
    }
}