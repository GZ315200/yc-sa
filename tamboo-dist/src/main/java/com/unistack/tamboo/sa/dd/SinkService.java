package com.unistack.tamboo.sa.dd;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.constant.DdType;
import com.unistack.tamboo.sa.dd.invoking.DdInvoking;
import com.unistack.tamboo.sa.dd.util.ConverterUtil;
import com.unistack.tamboo.sa.dd.util.DdUtil;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.unistack.tamboo.sa.dd.util.DdUtil.date2String;
import static com.unistack.tamboo.sa.dd.util.DdUtil.failResult;

/**
 * @author anning
 * @date 2018/6/7 下午5:54
 * @description: jdbc sink
 */
public class SinkService {
    private static  Logger logger = LoggerFactory.getLogger(SinkService.class);

    public static ConcurrentHashMap<String, ArrayList<Thread>> threadHashMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, ArrayList<Connection>> connectionHashMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, ArrayList<KafkaConsumer>> consumerHashMap = new ConcurrentHashMap<>();
    public static volatile boolean isRunning = true;           //关闭时 将isRunning设置为false
    public static HashMap<String, Boolean> statusMap = new HashMap<>();

//    public static int i = 0;


    /**
     * 检查配置是否可用
     *
     * @param args
     * @return
     */
    public static JSONObject checkConfig(JSONObject args) {
        String typeName = args.getString("type");
        SinkWorker sinkWorker = DdInvoking.getWorkerByName(typeName.toLowerCase());
        if (Objects.isNull(sinkWorker)) {
            return failResult("未找到对应的类型：" + typeName);
        }
        return sinkWorker.checkConfig(args);
    }


    /**
     * 开始下发
     *
     * @param args
     * @return
     */
    public static JSONObject startSink(JSONObject args) {
        JSONObject result = new JSONObject();
        String type = args.getString("type");
        DdType ddTypeByName = DdType.getDdTypeByName(type.toLowerCase());
        JSONObject config = args.getJSONObject("fields");
        String topic = args.getString("topic_name");
        String kafkaAclName = args.getString("kafkaAclName");
        String kafkaAclPassword = args.getString("kafkaAclPassword");
        config.put("kafkaAclName", kafkaAclName);
        config.put("kafkaAclPassword", kafkaAclPassword);

//        String jdbc_sink_ip = config.getString("ip");
//        String jdbc_sink_port = config.getString("port");
//        String jdbc_sink_database = config.getString("database");
//        String jdbcUrl = String.format(ddTypeByName.getFormat(), jdbc_sink_ip, jdbc_sink_port, jdbc_sink_database) + "?useSSL=false";
        String jdbcUrl = config.getString("jdbc_url");
        config.put("jdbc_url", jdbcUrl);

        String connectName = args.getOrDefault("connectName","sink-" + date2String(new Date())).toString();
//        String connectName = "sink-" + date2String(new Date());
//        String connectName = "sink";
        String groupId = "group2-" + connectName;

        //让connectName对应的线程启动，关闭时设置对应的status为false
        statusMap.put(connectName, true);
        ConsumerGroup consumerGroupByName = DdInvoking.getConsumerGroupByName(type.toLowerCase());
        if (consumerGroupByName == null) {
            return DdUtil.failResult("实例化对象失败：" + ConsumerGroup.class);
        }
        ConsumerGroup consumerGroup = consumerGroupByName.initGroup(connectName, groupId, topic, ddTypeByName, config);
//            JdbcConsumerGroup consumerGroup = new JdbcConsumerGroup(connectName, groupId, topic, ddTypeByName, config);
        consumerGroup.execute();
        result.put("isSucceed", true);
        result.put("connectName", connectName);
        result.put("groupId", groupId);
        result.put("topic", topic);
        /*} else {
            result = writelog;
        }*/
        return result;
    }


    /**
     * 停止下发
     *
     * @return com.unistack.tamboo.commons.json
     */
    public static JSONObject stopSink(String connectName) {
        JSONObject closeResult;

        if (threadHashMap.get(connectName) == null) {
            closeResult = DdUtil.failResult(connectName + "未在运行状态！");
        } else {
            //删除false的connectName
            statusMap.entrySet().removeIf(sbe -> !sbe.getValue());
            statusMap.put(connectName, false);
            closeResult = DdUtil.succeedResult("成功停止:" + connectName);
        }

        /*if (connectionHashMap.get(connectName)!=null){
            for (Connection connection:connectionHashMap.get(connectName)) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            connectionHashMap.remove(connectName);
        }*/

//        if (consumerHashMap.get(connectName)!=null){
//            for (KafkaConsumer consumer : consumerHashMap.get(connectName)){
//                consumer.close();
//            }
//        }

        return closeResult;
    }

    /**
     * 查询下发状态
     *
     * @param connectName name
     * @return
     */
    public static JSONObject getStatusByConnectName(String connectName) {
        JSONObject result;
        if (statusMap.containsKey(connectName)) {
            boolean isRunning = statusMap.get(connectName);
            if (isRunning) {
                if (threadHashMap.get(connectName) == null) {
                    result = DdUtil.succeedResult(2);  //异常关闭
                } else {
                    result = DdUtil.succeedResult(1);   //正常运行
                }
            } else {
                result = DdUtil.succeedResult(0);       //正常关闭
            }
        } else {
            result = DdUtil.succeedResult(2);      //connector没有经过stop,status未置0
        }
        return result;
    }

}
