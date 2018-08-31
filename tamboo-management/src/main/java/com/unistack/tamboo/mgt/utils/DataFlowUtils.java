package com.unistack.tamboo.mgt.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.mgt.common.FlowNodeType;
import com.unistack.tamboo.mgt.common.enums.CalcStatusType;
import com.unistack.tamboo.mgt.model.CalcInfo;
import com.unistack.tamboo.mgt.model.collect.DataFlowInfo;
import com.unistack.tamboo.mgt.model.collect.TopicInfo;
import com.unistack.tamboo.mgt.model.dataFlow.CalcNode;

import java.sql.Timestamp;
import java.util.HashMap;

public class DataFlowUtils {

    //节点类型 key   key_code
    private HashMap<String,String> key_type=new HashMap<>();

    //节点内容key nodeData
    private HashMap<String,JSONObject> key_nodes=new HashMap<>();

    //节点流 linkDataArray
    private JSONArray nodeFlows;

    //数据源id
    private int sourceId;

    private String dataSourceName;

    private String sourceTopicName;

    //key topicInfo
//    private HashMap<String,TopicInfo> keytopicNames;

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

    private TopicInfo sourceTopic;

    public HashMap<String,TopicInfo> getTopics() {
        return nametopicNames;
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



    public DataFlowUtils(JSONObject jsonObject, String group, int user, String name, String acl_username, String acl_passwd){
        this.jsonAll=jsonObject;
        this.group=group;
        this.username=user;
        this.name=name;
        this.sourceId=getSourceId(this.jsonAll.getJSONArray(nodeDataArray));
        topics_open=new HashMap<>();
        this.acl_username=acl_username;
        this.acl_passwd=acl_passwd;
        this.key_type=getKeyType(this.jsonAll.getJSONArray(nodeDataArray));
        this.key_nodes=getKeyNodes(this.jsonAll.getJSONArray(nodeDataArray));
        this.nodeFlows=this.jsonAll.getJSONArray(linkDataArray);

    }

    public int getSourceId(){
        return sourceId;
    }

    public void setSourceTopic(TopicInfo topic){

        this.sourceTopicName=topic.getTopicName();
        this.sourceTopic=topic;
        setSourceName(topic);
        setTopicInfos();
        setCalcInfo();

    }



    private void setCalcInfo() {
        for (Object flowObject:nodeFlows){
            JSONObject flow= (JSONObject) flowObject;

            String from_key=flow.getString("from");
            String to_key=flow.getString("to");

            String from_type=key_type.get(from_key);
            String to_type=key_type.get(to_key);

            if(from_type.equalsIgnoreCase(FlowNodeType.NODETPYE.DATASOURCE.getType())){
                key_nodes.get(to_key).put("topicFrom",key_nodes.get(from_key).getString("topicName"));
            }
            if(from_type.startsWith("1")){
                key_nodes.get(to_key).put("topicFrom",key_nodes.get(from_key).getString("topicName"));
            }
        }
    }

    private void setTopicInfos() {
//        keytopicNames=new HashMap<>();
        nametopicNames=new HashMap<>();
        for(String node:key_nodes.keySet()){
            if(key_type.get(node).startsWith("1")){
                JSONObject topic=new JSONObject();
                topic.put("data",key_nodes.get(node).getJSONObject("data"));
                TopicInfo topicInfo=new TopicInfo();
//                topicInfo.setConf(topic.getString("data"));
                topicInfo.setTopicAclType("SASL_PLAINTEXT");
                topicInfo.setTopicName(key_nodes.get(node).getString("topicName"));
                topicInfo.setTopicAclUsername(this.acl_username);
                topicInfo.setTopicAclPassword(this.acl_passwd);
                topicInfo.setTopicPartition(topic.getInteger("partitions")==null? TambooConfig.KAFKA_PARTITION:topic.getInteger("partitions"));
                topicInfo.setTopicReplication(topic.getInteger("replitas")==null? TambooConfig.KAFKA_REPLICA:topic.getInteger("replitas"));
                topic.put("topicName",topicInfo.getTopicName());
                topic.put(key_code,"21");
//                key_nodes.put(node,topic);
//                keytopicNames.put(node,topicInfo);
                nametopicNames.put(topicInfo.getTopicName(),topicInfo);
                calc_cpu +=key_nodes.get(node).getInteger("cpu");
                calc_mem +=key_nodes.get(node).getInteger("mem");
            }

            if(key_type.get(node).equalsIgnoreCase("31")){
                if(key_nodes.get(node).getString("topicFrom")==null||key_nodes.get(node).getString("topicFrom").equalsIgnoreCase("null")){
                   continue;
                }
                JSONObject dist=new JSONObject();
                dist.put("data",key_nodes.get(node));
                dists.put(key_nodes.get(node).getString("topicFrom")+"_"+node,dist);
            }
        }
    }




    public DataFlowInfo getDataFlowInfo() {
        DataFlowInfo dataFlowInfo=new DataFlowInfo();
        dataFlowInfo.setDataSourceId(sourceId);
        dataFlowInfo.setWfName(name);
        dataFlowInfo.setUserId(username);
        dataFlowInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
        dataFlowInfo.setFlag(CalcStatusType.START.getType());
        dataFlowInfo.setConf(jsonAll.toJSONString());
        dataFlowInfo.setWfDsc(jsonAll.getString("taskInfo"));

        dataFlowInfo.setCreateBy(jsonAll.getString("creater"));
        dataFlowInfo.setCalcCpu(calc_cpu);
        dataFlowInfo.setCalcMem(calc_mem);
        dataFlowInfo.setTopicNum(nametopicNames.size()-1);
        return dataFlowInfo;
    }


    /**
     * 获取数据源id
     * @param nodeDataArray
     * @return
     */
    private int getSourceId(JSONArray nodeDataArray){
        int sourceId=-1;
        for (Object flow:nodeDataArray){
            JSONObject fl= (JSONObject) flow;
            if(!fl.getString(key_code).equalsIgnoreCase(FlowNodeType.NODETPYE.DATASOURCE.getType())){
                continue;
            }

            JSONObject json=fl.getJSONObject("data");
            sourceId=json.getInteger("topicId");
            dataSourceName=json.getString("alias");
        }
        return sourceId;
    }

    private boolean setSourceName(TopicInfo topicInfo){
        for (Object flow:this.jsonAll.getJSONArray(nodeDataArray)){
            JSONObject fl= (JSONObject) flow;
            if(fl.getString(key_code).equalsIgnoreCase(FlowNodeType.NODETPYE.DATASOURCE.getType())){
                fl.put("topicName",topicInfo.getTopicName());
                break;
            };
        }
        return true;
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





    public HashMap<String,CalcNode> getCalcInfos(HashMap<String, TopicInfo> topicInfos) {

        this.nametopicNames=topicInfos;
        HashMap<String,CalcNode> calcConfHashMaps=new HashMap<>();
        for (String key:key_nodes.keySet()){
            JSONObject flow= key_nodes.get(key);
            if(flow.getString(key_code).startsWith("1")){
                CalcNode calcNode=new CalcNode();
                calcNode.setNode(key);
                calcNode.setConf(flow.getString("data"));
                calcNode.setType(flow.getInteger(key_code));
                String topicFrom=flow.getString("topicFrom");
                calcNode.setTopic_from(nametopicNames.get(topicFrom)!=null?nametopicNames.get(topicFrom):(topicFrom.equalsIgnoreCase(sourceTopicName)?sourceTopic:null));
                calcNode.setTopic_to(nametopicNames.get(flow.getString("topicName")));
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


    public void setTopics(HashMap<String,TopicInfo> topicInfos) {
        this.nametopicNames=topicInfos;
    }

    public HashMap<Integer,Object[]> getOpenTopic() {
//        keytopicNames;
        HashMap<Integer,Object[]> openTopic=new HashMap<>();
        for (String node : key_nodes.keySet()) {
            if (key_type.get(node).startsWith("1")) {
                JSONArray group=key_nodes.get(node).getJSONObject("data").getJSONArray("group");

                if(group==null || group.size()==0){
                    continue;
                }
                String topicName=key_nodes.get(node).getString("topicName");
                int topic_id= (int) nametopicNames.get(topicName).getTopicId();
                openTopic.put(topic_id,group.toArray());
                }
            }
        return openTopic;
    }

    public HashMap getDist() {
        HashMap<String,JSONObject> res=new HashMap<>();
        for(String key:dists.keySet()){
            JSONObject jsonDist=dists.get(key);
            JSONObject jsonRes=new JSONObject();
            JSONObject distJson = jsonDist.getJSONObject("data").getJSONObject("data").getJSONArray("calcInfoList").getJSONObject(0);
            jsonRes.put("dataSourceName",distJson.getString("dataSourceName"));
            jsonRes.put("table_name",distJson.getString("table_name"));
            jsonRes.put("fields",distJson.getJSONArray("fields").getJSONObject(0));
            jsonRes.put("type",distJson.getString("type"));
            jsonRes.put("topic_name",jsonDist.getJSONObject("data").getString("topicFrom"));
//            jsonRes.put("wf_id",wf_id);
//            jsonRes.put("kafkaAclName",acl_username);
//            jsonRes.put("kafkaAclPassword",acl_passwd);
            jsonRes.put("wf_id",wf_id);
            res.put(key,jsonRes);
        }

        return res;
    }

    public String getSourceTopic() {
        return this.sourceTopicName;
    }

    public String getDataSourceName(){
        return dataSourceName;
    };
    public String getGroup(String node){
        return (group+"-"+username+"-"+name+"-"+node).toLowerCase();
    }

    public JSONObject setCalcIdsAndGetJsonAll(HashMap<String,CalcInfo> calcInfoNews) {
        for(String node:calcInfoNews.keySet()){
            CalcInfo calcInfo=calcInfoNews.get(node);
            JSONObject jsonObject=key_nodes.get(node);
            jsonObject.put("data_wf",calcInfo.getDataWf());
            jsonObject.put("calcId",calcInfo.getDataWf());
            key_nodes.put(node,jsonObject);
        }
        JSONArray jsonArray=new JSONArray();
        for(JSONObject json:key_nodes.values()){
            jsonArray.add(json);
        }
        jsonAll.put(nodeDataArray,jsonArray);

        return jsonAll;
    }
}