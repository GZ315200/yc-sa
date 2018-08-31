package com.unistack.tamboo.compute.process.impl;

import com.unistack.tamboo.compute.process.StreamProcess;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.utils.KafkaUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;


/**
 * @author hero.li
 */
public class SplitProcess implements StreamProcess {
    private static Logger LOGGER = LoggerFactory.getLogger(SplitProcess.class);

    private Properties outputInfo;
    private String toTopic;
    private JSONObject splitRules;


    public SplitProcess(JSONObject globalConfig){
        KafkaUtil kafkaUtil = new KafkaUtil();
        this.outputInfo = kafkaUtil.getOutputInfo();
        this.toTopic = globalConfig.getJSONObject("kafka").getString("to_topic");
        this.splitRules = globalConfig.getJSONObject("template");
    }


    @Override
    public void logic(JavaInputDStream<ConsumerRecord<String,String>> line) {
        line.foreachRDD(rdd->{
            rdd.foreachPartition(itr->{
                Producer p = new KafkaProducer<>(outputInfo);
                while(itr.hasNext()){
                    ConsumerRecord<String, String> item = itr.next();
                    Object record = item.value();
                    LOGGER.info("SPARK:数据Split收到的数据:"+record);
                    splitJson(record.toString(),p);
                }
                p.close();
            });
        });
    }


    /**
     * 通过json格式的配置文件拆分json格式的数据 <br/>
     * @param jsonMsg   json格式的原始数据
     *
     * @return
     */
    private void splitJson(String jsonMsg,Producer producer){
        JSONObject data;
        try{
            data = JSONObject.parseObject(jsonMsg);
        }catch(Exception e){
            LOGGER.error("数据不是标准的json格式 msg="+jsonMsg+",拆分规则="+splitRules);
            return;
        }

            String rule = splitRules.getString("distributeRule");
            String topicName = splitRules.getString("topicName");
            String targetTopic = StringUtils.isBlank(topicName) ? toTopic : topicName;
//          splitArrayJson(data,rule,producer,targetTopic);
            splitSimpleJson(data,rule,producer,targetTopic);
    }


    /**
     * 把数据data按照rule的规则拆分并使用producer发送到topicName <br/>
     * @param data          要拆分的数据      eg:{"A":{"B":{"C":{"D":"dd"}}}}
     * @param rule          拆分规则          eg:A.B.C.D => 把data数据的A属性的B属性的C属性的D属性拆分出来
     * @param producer
     * @param topicName
     *
     *   eg1: 单项普通模式
     *      data:     {"A":{"B":{"C":{"D":"dd"}}}}
     *      rule:     A.B.C
     *      result :  {"D":"dd"}
     *
     *
     *   eg2: 单项等值模式
     *      eg2.1:
     *          data:    {"A":{"B":{"C":{"D":"dd"}}}}
     *          rule:    A.B.C.D=dd
     *          result:  dd        => 发送dd数据
     *
     *      eg2.2:
     *          data:   {"A":{"B":{"C":{"D":"abc"}}}}
     *          rule:   A.B.C.D=dd
     *          result: null   => 拆分后的数据为null,默认不发送
     *
     *
     *   eg3: 单项通配模式
     *
     *        eg3.1:
     *              data:   {"A":{"B":{"C":{"D":"alibaba"}}}}
     *              rule:   A.B.C.D==ali*   =>拆分出该json数据的A属性下的B属性下的C属性的D属性的值以ali开头
     *              result:  alibaba
     *
     *        eg3.2:
     *              data:    {"A":{"B":{"C":{"D":"alibabaAndTencent"}}}}
     *              rule:    A.B.C.D==*Tencent   =>拆分出该json数据的A属性下的B属性下的C属性的D属性的值以Tencent结尾
     *              result:  alibabaAndTencent
     *
     *        eg3.3:
     *              data:    {"A":{"B":{"C":{"D":"alibabaAndTencent"}}}}
     *              rule:    A.B.C.D==ali*Tencent   =>拆分出该json数据的A属性下的B属性下的C属性的D属性的值以ali开头并且以Tencent结尾
     *              result:  alibabaAndTencent
     *
     *        eg3.4:
     *              data:    {"A":{"B":{"C":{"D":"alibabaAndTencent"}}}}
     *              rule:    A.B.C.D==*And*   =>拆分出该json数据的A属性下的B属性下的C属性的D属性的值包含And
     *              result:  alibabaAndTencent
     */
    private static void splitSimpleJson(JSONObject data,String rule,Producer producer,String topicName){
        String[] rules = rule.split("\\.");
        JSONObject tempData = data;
        for(int i=0;i<rules.length;i++){
            String ruleItem = rules[i];
            if(i == rules.length-1){
                if(ruleItem.contains("==")){
                    //单项通配模式
                    String resultData = singleWildCardModel(tempData,ruleItem);
                    logAndSend(data, rule, producer, topicName, resultData);
                    break;
                }else if(ruleItem.contains("=")){
                    //单项等值模式
                    String resultData = singleEqualsValueModel(tempData, ruleItem);
                    logAndSend(data, rule, producer, topicName, resultData);
                    break;
                }else{
                    //单项普通模式
                    String resultData = singleSimpleModel(tempData, ruleItem);
                    logAndSend(data,rule,producer,topicName,resultData);
                }
            }else{
                tempData = tempData.getJSONObject(ruleItem);
                if(null == tempData){
                    LOGGER.error("拆分规则与数据不符合,拆分规则["+rule+"]不能拆分数据["+data+"]");
                    return;
                }
            }
        }
    }

    /**
     * 发送数据并打印日志
     * @param data 未处理的数据
     * @param rule 拆分规则
     * @param producer
     * @param topicName
     * @param resultData 最终要发送的数据
     */
    private static void logAndSend(JSONObject data, String rule, Producer producer, String topicName, String resultData) {
        if(!StringUtils.isBlank(resultData)){
            LOGGER.info("SPARK:数据Split,发送到["+topicName+"]的数据:"+resultData);
            producer.send(new ProducerRecord(topicName,String.valueOf(System.currentTimeMillis()),resultData));
            return;
        }
        LOGGER.info("SPARK:数据Split,拆分后为空,拆分规则["+rule+"],拆分前的数据："+data.toString());
    }


    /**
     * 单项 普通模式
     */
    private static String singleSimpleModel(JSONObject tempData, String ruleItem){
        String target = tempData.getString(ruleItem);
        return (!StringUtils.isBlank(target)) ? target : "";
    }


    /**
     * 单项 等值模式
     */
    private static String singleEqualsValueModel(JSONObject tempData,String ruleItem) {
        String[] split = ruleItem.split("=");
        String target = tempData.getString(split[0]);
        return target.equals(split[1])? target :"";
    }


    /**
     * 单项通配模式 参看 splitSimpleJsonAndSend的eg3
     * @param tempData
     * @param ruleItem
     * @return
     */
    private static String singleWildCardModel(JSONObject tempData, String ruleItem) {
        String[] split = ruleItem.split("==");
        String target = tempData.getString(split[0]);

        if(split[1].startsWith("*") && split[1].endsWith("*")){
            //eg3.4: *And*
            String keyWord = split[1].replace("*", "");
            return (!StringUtils.isBlank(target) && target.contains(keyWord)) ? target : "";
        }else if(split[1].startsWith("*")){
            //eg3.2: *Tencent
            String keyWord = split[1].replace("*", "");
            return (!StringUtils.isBlank(target) && target.endsWith(keyWord)) ? target : "";
        }else if(split[1].endsWith("*")){
            //eg3.1: ali*
            String keyWord = split[1].replace("*", "");
            return (!StringUtils.isBlank(target) && target.startsWith(keyWord)) ? target : "";
        }else if(split[1].contains("*")){
            //eg3.3: ali*Tencent
            String[] keyWord = split[1].split("\\*");
            return (!StringUtils.isBlank(target) && target.startsWith(keyWord[0]) && target.endsWith(keyWord[1])) ? target : "";
        }
        LOGGER.info("SPARK:数据Split,单项通配模式拆分规则["+ruleItem+"]写法错误");
        return "";
    }


    /**
     * 把数据data按照rule的规则拆分并使用producer发送到topicName <br/>
     * @param data          要拆分的数据
     * @param rule          拆分规则
     * @param producer
     * @param topicName
     *
     *  eg1:数组普通模式
     *      data: {"A":{"B":{"C":[{"D":"d1","E":"ev1"},{"D":"d2","E":"ev2"}]}},"D":"D-value"}
     *      rule: A.B.C.# =>把A的B属性的C属性(数组)的每一项都拆出来并发送
     *      result:
     *            {"D":"d1","E","ev1"}
     *            {"D":"d2","E","ev2"}
     *
     *  eg2: 数组等值模式
     *      data: {"A":{"B":{"C":[{"D":"d1","E":"ev1"},{"D":"d2","E":"ev2"}]}}}
     *      rule: A.B.C.D=d1 =>把A的B属性的C属性(数组)下的项中D属性的值为d1拆出来并发送
     *      result:
     *          {"D":"d1","E","ev1"}
     *          {"D":"d2","E","ev2"}
     *
     *  eg3: 数组通配模式
     *      data: {"A":{"B":{"C":[{"D":"div1","E":"ev1"},{"D":"cw2018","E":"ev2"}]}}}
     *      eg3.1 :
     *          rule: A.B.C.D==d*      =>把A的B属性的C属性(数组)下的项中D属性的值以d1开头的拆出来
     *
     *      eg3.2:
     *          rule: A.B.C.D==*2018   =>把A的B属性的C属性(数组)下的项中D属性的值以2018结尾的拆出来
     *
     *      eg3.3:
     *          rule: A.B.C.D==d*2018  =>把A的B属性的C属性(数组)下的项中D属性的值以d开头并且以2018结尾的拆出来
     *
     *      eg3.4:
     *          rule: A.B.C.D==*2018*  =>把A的B属性的C属性(数组)下的项中D属性的值包含2018的拆出来
     *
     */
    private static void splitArrayJson(JSONObject data, String rule,Producer producer, String topicName){
        String[] rules = rule.split("\\.");
        JSONObject tempData = data;
        for(int i=0;i<rules.length;i++){
            String ruleItem = rules[i];
            if(rules.length == 1 || i == rules.length-2 ){
                String nextRuleItem = (rules.length == 1 ) ? rules[0] : rules[i+1];
                    JSONArray targetArray = tempData.getJSONArray(ruleItem);
                    for(Object o : targetArray){
                        JSONObject targetJson = (JSONObject)o;
                        if(nextRuleItem.contains("==") && nextRuleItem.contains("*")){
                            //数组通配模式
                            String resultData = arrayWildCardModel(nextRuleItem,targetJson);
                            System.out.println(resultData);
                        }else if(nextRuleItem.contains("=")){
                            //数组等值模式
                            String resultData = arrayEqualsModel(nextRuleItem,targetJson);
                            System.out.println(resultData);
                        }else{
                            //数组普通模式
                            System.out.println(targetJson);
                        }
                    }
                break;
            }else{
                tempData = tempData.getJSONObject(ruleItem);
                if(null == tempData){
                    LOGGER.error("SPARK:数据Split,拆分规则["+rule+"]书写错误,拆分之前的数据:"+data);
                    return;
                }
            }
        }
    }

    /**
     * 数组等值模式
     * @param nextRuleItem 拆分规则对的最后一项
     * @param targetJson  数组的每一项
     */
    private static String arrayEqualsModel(String nextRuleItem, JSONObject targetJson){
        String[] lastEle = nextRuleItem.split("=");
        String realValue = targetJson.getString(lastEle[0]);
        return (!StringUtils.isBlank(realValue) && realValue.equals(lastEle[1])) ? realValue : "";
    }


    /**
     * 数组通配模式
     * 参看:splitArrayJsonAndSend 的eg3
     *
     * @param nextRuleItem   拆分规则中的最后一项  eg:A.B.C.D==dd 中的D==dd*
     * @param targetJson     数组中的每一项    {"A":[{"K1":"v1","K2":"V2"},{"K1":"v11","K2":"V22"}]} 中的{"K1":"v1","K2":"V2"}
     */
    private static String arrayWildCardModel(String nextRuleItem, JSONObject targetJson){
        String[] lastEle = nextRuleItem.split("==");
        //数组通配模式
        if(lastEle[1].startsWith("*") && lastEle[1].endsWith("*")){
            // *And*
            String keyWord = lastEle[1].replace("*", "");
            String target = targetJson.getString(lastEle[0]);
            return (!StringUtils.isBlank(target) && target.contains(keyWord)) ? target :"";
        }else if(lastEle[1].endsWith("*")){
            // ali*
            String dataHead = lastEle[1].replace("*","");
            String realValue = targetJson.getString(lastEle[0]);
            return (!StringUtils.isBlank(realValue) && realValue.startsWith(dataHead) ) ? realValue : "";
        }else if(lastEle[1].startsWith("*")){
            // *Tencent
            String tailHead = lastEle[1].replace("*","");
                String realValue = targetJson.getString(lastEle[0]);
                return (!StringUtils.isBlank(realValue) && realValue.endsWith(tailHead)) ? realValue : "";
        }else if(lastEle[1].contains("*")){
            //ali*Tencent
            String[] split = lastEle[1].split("\\*");
                String realValue = targetJson.getString(lastEle[0]);
                return (!StringUtils.isBlank(realValue) && realValue.startsWith(split[0]) && realValue.endsWith(split[1])) ? realValue : "" ;
        }
        LOGGER.info("SPARK:数据Split数组通配模式,数据拆分规则书写错误:"+nextRuleItem);
        return "";
    }



//    public static void main(String[] args){
//        PropertyConfigurator.configure("config"+ File.separator+"log4j.properties");
//        String com.unistack.tamboo.commons.json = "{\"A\":[{\"k1\":\"v1\"},{\"k1\":\"v11\"}],\"C\":\"c-v\"}";
//
//
//        String distribute = "[{\"distributeRule\":\"A.#\",\"topicName\":\"\"}]";
//        JSONArray config = JSONArray.parseArray(distribute);
//        SplitProcess sp = new SplitProcess(new Properties(),"xx",config);
//        sp.splitJson(com.unistack.tamboo.commons.json,null);
//    }
}