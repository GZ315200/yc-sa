package com.unistack.tamboo.mgt.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.mgt.common.FlowNodeType;
import com.unistack.tamboo.mgt.common.enums.CalcStatusType;
import com.unistack.tamboo.mgt.model.collect.DataFlowInfo;
import com.unistack.tamboo.mgt.model.collect.TopicInfo;
import com.unistack.tamboo.mgt.model.dataFlow.CalcNode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DataFlowUtilSimp {

    //节点类型 key   key_code
    private HashMap<String,String> key_type=new HashMap<>();

    //节点内容key nodeData
    private HashMap<String,JSONObject> key_nodes=new HashMap<>();

    //节点流 linkDataArray
    private JSONArray nodeFlows;

    //数据源id
    private int sourceId;

    //数据源 topic  用哈希是为了判断topic_name是否对应
    private HashMap<String,TopicInfo> sourceTopic;

    //key topicInfo
    private HashMap<String,TopicInfo> keytopicNames;

    //topic_name topicInfo
    private HashMap<String,TopicInfo> nametopicNames;

    //topic 开放权限
    private HashMap<String,int[]> topics_open;

    // 下发数据源
    private HashMap<String,JSONObject> dists=new HashMap<>();

    private String group;

    private int username;

    private String name;

    private int wf_id;

    private JSONObject jsonAll;

    private String topicDataSource;

    public HashMap<String,TopicInfo> getTopics() {
        return keytopicNames;
    }

    public int getWf_id() {
        return wf_id;
    }

    public void setWf_id(int wf_id) {
        this.wf_id = wf_id;
    }

    private String nodeDataArray="nodeDataArray";
    private String linkDataArray="linkDataArray";
    private String key_code="key_code";

    private int calc_cpu=0;
    private int calc_mem=0;

    private String acl_username;
    private String acl_passwd;

    private String date_Str;

    public DataFlowUtilSimp(JSONObject jsonObject, String group, int user, String name,String acl_username,String acl_passwd){
        this.jsonAll=jsonObject;
        this.group=group;
        this.username=user;
        this.name=name;
        this.sourceId=getSourceId(this.jsonAll.getJSONArray(nodeDataArray));
        topics_open=new HashMap<>();
        this.acl_username=acl_username;
        this.acl_passwd=acl_passwd;
        this.date_Str=new  SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    public int getSourceId(){
        return sourceId;
    }

    public void setSourceTopic(TopicInfo topic){
        this.sourceTopic=new HashMap<>();
        this.sourceTopic.put(topic.getTopicName(),topic);
        this.topicDataSource=topic.getTopicName();
        this.key_type=getKeyType(this.jsonAll.getJSONArray(nodeDataArray));
        this.key_nodes=getKeyNodes(this.jsonAll.getJSONArray(nodeDataArray));
        this.nodeFlows=this.jsonAll.getJSONArray(linkDataArray);

        setTopicNames();
        setCalcInfo();

    }


    private void setCalcInfo() {
        for (Object flowObject:nodeFlows){
            JSONObject flow= (JSONObject) flowObject;
            String type=key_type.get(flow.getString("from"));
            String type2=key_type.get(flow.getString("to"));
            String from_key=flow.getString("from");
            String to_key=flow.getString("to");
            if(type.equalsIgnoreCase(FlowNodeType.NODETPYE.DATASOURCE.getType())){
                key_nodes.get(to_key).put("topic_from",this.topicDataSource);
//                key_nodes.get(to_key).put("topic_from",key_nodes.get(from_key).getJSONObject("data").getString("topic_name"));
            }else if(type.startsWith("1")){
                key_nodes.get(from_key).put("topic_to",keytopicNames.get(to_key).getTopicName());
                key_nodes.get(to_key).put("topic_to",key_nodes.get(from_key).getString("topic_name"));
            }else if(type.equalsIgnoreCase("21")){
                key_nodes.get(from_key).put("topic_to",key_nodes.get(to_key).getString("topic_name"));
                key_nodes.get(to_key).put("topic_from",key_nodes.get(from_key).getString("topic_name"));
            }else if(type.equalsIgnoreCase("31")){

            }
           if(type2.startsWith("2")){
               key_nodes.get(from_key).put("topic_to",keytopicNames.get(to_key).getTopicName());
           }


        }

    }

    private void setTopicNames() {
        keytopicNames=new HashMap<>();
        nametopicNames=new HashMap<>();
        for(String node:key_nodes.keySet()){
            if(key_type.get(node).equalsIgnoreCase("21")){
                JSONObject topic=new JSONObject();
                topic.put("data",key_nodes.get(node).getJSONObject("data"));
                TopicInfo topicInfo=new TopicInfo();
                topicInfo.setConf(topic.getString("data"));
                topicInfo.setTopicAclType("SASL_PLAINTEXT");
                topicInfo.setTopicName(getTopicName(node));
                topicInfo.setTopicAclUsername(this.acl_username);
                topicInfo.setTopicAclPassword(this.acl_passwd);
                topicInfo.setTopicPartition(topic.getInteger("partitions")==null? TambooConfig.KAFKA_PARTITION:topic.getInteger("partitions"));
                topicInfo.setTopicReplication(topic.getInteger("replitas")==null? TambooConfig.KAFKA_REPLICA:topic.getInteger("replitas"));
                topic.put("topic_name",topicInfo.getTopicName());
                topic.put(key_code,"21");
                key_nodes.put(node,topic);
                keytopicNames.put(node,topicInfo);
                nametopicNames.put(topicInfo.getTopicName(),topicInfo);
            }
            if(key_type.get(node).startsWith("1")){
                calc_cpu +=key_nodes.get(node).getInteger("cpu");
                calc_mem +=key_nodes.get(node).getInteger("mem");
            }

            if(key_type.get(node).equalsIgnoreCase("31")){
                if(key_nodes.get(node).getString("topic_from")==null||key_nodes.get(node).getString("topic_from").equalsIgnoreCase("null")){
                   continue;
                }
                JSONObject dist=new JSONObject();
                dist.put("data",key_nodes.get(node));
                dists.put(key_nodes.get(node).getString("topic_from")+"_"+node,dist);


            }

//            if(key_type.get(node).equalsIgnoreCase("0")){
//                JSONObject topic=key_nodes.get(node).getJSONObject("data");
//                topicDataSource=topic.getString("topic_name");
////                nametopicNames.put(topic.getString("topic_name"),sourceTopic.get(topic.getString("topic_name")));
//            }
        }
    }




    public DataFlowInfo getDataFlowInfo() {
        DataFlowInfo dataFlowInfo=new DataFlowInfo();
        dataFlowInfo.setDataSourceId(sourceId);
        dataFlowInfo.setTopicNum(keytopicNames.size());
        dataFlowInfo.setWfName(name);
        dataFlowInfo.setUserId(username);

        dataFlowInfo.setFlag(CalcStatusType.START.getType());
        dataFlowInfo.setConf(jsonAll.toJSONString());
        dataFlowInfo.setWfDsc(jsonAll.getString("taskInfo"));

        dataFlowInfo.setCreateBy(jsonAll.getString("creater"));
        dataFlowInfo.setCalcCpu(calc_cpu);
        dataFlowInfo.setCalcMem(calc_mem);
        dataFlowInfo.setTopicNum(keytopicNames.size());
        return dataFlowInfo;
    }

    private int getSourceId(JSONArray nodeDataArray) {
        int sourceId=-1;
        for (Object flow:nodeDataArray){
            JSONObject fl= (JSONObject) flow;
            if(fl.getString(key_code).equalsIgnoreCase(FlowNodeType.NODETPYE.DATASOURCE.getType())){
                JSONObject json=fl.getJSONObject("data");
                sourceId=json.getInteger("topic_id");
                break;
            };
        }
        return sourceId;
    }

    private HashMap<String,JSONObject> getKeyNodes(JSONArray jsonArray) {
        HashMap<String,JSONObject> nodes=new HashMap<>();
        for (Object flow:jsonArray){
            JSONObject fl= (JSONObject) flow;
            nodes.put(fl.getString("key"),fl);
        }
        return nodes;
    }


    public boolean checkFlows(){
//        JSONArray key_type=jsonAll.getJSONArray(nodeDataArray);
        //获取节点对应类型
//        HashMap<String,String> kv=getKeyType(key_type);

        //判断节点流转是否符合规则
        JSONArray flows=jsonAll.getJSONArray("linkDataArray");
        for (Object flow:flows){
            JSONObject fl= (JSONObject) flow;
            if(!FlowNodeType.checkNode(FlowNodeType.nodeTypes.get(key_type.get(fl.getString("from"))),FlowNodeType.nodeTypes.get(key_type.get(fl.get("to"))))){
                return false;
            }
        }
        return true;
    }

    /**
     * 节点对应类型解析
     * @param key_type  小类
     * @return
     */
    public HashMap<String,String> getKeyType(JSONArray key_type){
        HashMap<String,String> kv=new HashMap<>();

        for(Object json:key_type){
            JSONObject js= (JSONObject) json;
            kv.put(js.getString("key"),js.getString(key_code));
        }
        return kv;
    }

    public boolean checkNode(){
        for (JSONObject node:key_nodes.values()){
//            if(node.getString(key_code)<0){
//                return false;
//            }

            if(node.getString(key_code).equalsIgnoreCase(FlowNodeType.NODETPYE.DATASOURCE.getType())){

            }else if(node.getString(key_code).equalsIgnoreCase(FlowNodeType.NODETPYE.CALC_CLEAN.getType())){

            }else if(node.getString(key_code).equalsIgnoreCase(FlowNodeType.NODETPYE.CALC_JOIN.getType())){

            }else if(node.getString(key_code).equalsIgnoreCase(FlowNodeType.NODETPYE.CALC_SPLIT.getType())){

            }else if(node.getString(key_code).equalsIgnoreCase(FlowNodeType.NODETPYE.CALC_SQL.getType())){

            }else if(node.getString(key_code).equalsIgnoreCase(FlowNodeType.NODETPYE.MQ_KAFKA.getType())){

            }else if(node.getString(key_code).equalsIgnoreCase(FlowNodeType.NODETPYE.DIST.getType())){

            }else{
                return false;

            }
        }
        return true;

    }

    public String getTopicName(String node){
        return (group+"-"+username+"-"+name+"-"+date_Str+"-"+node).toLowerCase();
    }



    public HashMap<String,CalcNode> getCalcInfos(HashMap<String, TopicInfo> topicInfos) {

        this.keytopicNames=topicInfos;
        HashMap<String,CalcNode> calcConfHashMaps=new HashMap<>();
        for (String key:key_nodes.keySet()){
            JSONObject flow= key_nodes.get(key);
            if(flow.getString(key_code).startsWith("1")){
                CalcNode calcNode=new CalcNode();
                calcNode.setConf(flow.getString("data"));
                calcNode.setType(flow.getInteger(key_code));
                calcNode.setTopic_from(nametopicNames.get(flow.getString("topic_from"))==null?sourceTopic.get(flow.getString("topic_from")):nametopicNames.get(flow.getString("topic_from")));
                calcNode.setTopic_to(keytopicNames.get(flow.getString("key"))==null?nametopicNames.get(flow.getString("topic_to")):keytopicNames.get(flow.getString("key")));
                calcNode.setCpu(flow.getInteger("cpu"));
                calcNode.setMem(flow.getInteger("mem"));
                calcConfHashMaps.put(key,calcNode);
            }
        }
        return calcConfHashMaps;
    }

    /**
     * 测试计算流程是否
     * @param \\\\jsonObject
     * @return
     */
//    public boolean checkCalcs(JSONObject jsonObject,String msg){
//        Object res=CalcAction.checkConf(jsonObject.toString(),msg);
//
//        return true;
//    }


    public void updateDataFlow(DataFlowInfo dataFlowInfo) {
        this.setWf_id(dataFlowInfo.getWfId());
    }


    public String getSourceTopic() {
        return topicDataSource;
    }

    public void setTopics(HashMap<String,TopicInfo> topicInfos) {
        this.keytopicNames=topicInfos;
    }

    public HashMap<Integer,Object[]> getOpenTopic() {
//        keytopicNames;
        HashMap<Integer,Object[]> openTopic=new HashMap<>();
        for (String node : key_nodes.keySet()) {
            if (key_type.get(node).equalsIgnoreCase("21")) {
                JSONObject group=key_nodes.get(node).getJSONObject("data");

                String topicName=key_nodes.get(node).getString("topic_name");
                int topic_id= (int) keytopicNames.get(node).getTopicId();
                if(group.getBoolean("openAuthority")){
                    JSONArray jsonArray=group.getJSONArray("group");
                    Object[] groups=  jsonArray.toArray();
                openTopic.put(topic_id,groups);
                }
            }

        }
        return openTopic;
    }

    public HashMap getDist() {
        HashMap<String,JSONObject> res=new HashMap<>();
        for(String key:dists.keySet()){
            JSONObject jsonDist=dists.get(key);
            JSONObject jsonRes=new JSONObject();
            jsonRes.put("dataSourceName",jsonDist.getJSONObject("data").getJSONArray("data").getJSONObject(0).getString("dataSourceName"));
            jsonRes.put("table_name",jsonDist.getJSONObject("data").getJSONArray("data").getJSONObject(0).getString("table_name"));
            jsonRes.put("fields",jsonDist.getJSONObject("data").getJSONArray("data").getJSONObject(0).getJSONArray("fields").getJSONObject(0));
            jsonRes.put("type",jsonDist.getJSONObject("data").getJSONArray("data").getJSONObject(0).getString("type"));
            jsonRes.put("topic_name",jsonDist.getJSONObject("data").getString("topic_from"));
//            jsonRes.put("wf_id",wf_id);
            jsonRes.put("kafkaAclName",acl_username);
            jsonRes.put("kafkaAclPassword",acl_passwd);
            jsonRes.put("wf_id",wf_id);
            res.put(key,jsonRes);
        }

        return res;
    }
}
    /*
    {
    "type": "mysql/oracle",
    "kafkaAclName":"acl连接用户名#string",
    "kafkaAclPassword":"acl连接用户密码#string",
    "wf_id":"#int",
    "fields": {
        "jdbc_url":"数据库连接url#string",
        "jdbc_username":"数据库用户名#string",
        "jdbc_password":"数据库密码#string",
        "table_name":"表名#string",
        "tasks_max":"最大任务数#int",
        "topics":"topic名称#string",
        "whitelist":"字段白名单#string",
        "pk_fields":"主键字段#string"
          }
    }
     */