package com.unistack.tamboo.mgt.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.mgt.dao.monitor.MTopicGroupIdDao;
import com.unistack.tamboo.mgt.model.monitor.MTopicGroupId;
import com.unistack.tamboo.mgt.utils.SpringContextHolder;
import com.unistack.tamboo.sa.dd.util.DdUtil;

import java.util.List;

/**
 * @author anning
 * @date 2018/5/31 上午9:40
 * @description: 数据下发模块
 */
public class DataDistAction {
    private static MTopicGroupIdDao mTopicGroupIdDao = SpringContextHolder.getBean(MTopicGroupIdDao.class);

    /**
     * 向connect_url post connector配置，开始下发数据 ,成功后将topic_name和group_id存到表里
     * args即为页面传递来的参数
     *
     * @param args {}
     * @return com.unistack.tamboo.commons.json {"isSucceed":boolean,"msg":msg,"connectorName":name,"group_id":group_id} 状态码信息已经判断
     */
    /*public JSONObject startKafkaSinkConnector(JSONObject args) {
        JSONObject startResult = ddAction.startKafkaSinkConnector(args);
        if (startResult.getBoolean("isSucceed")) {
            String group_id = startResult.getString("group_id");
            String topic = startResult.getString("topic");
            String connectorName = startResult.getString("connectorName");
            MTopicGroupId mTopicGroupId = new MTopicGroupId();
            mTopicGroupId.setConnectorName(connectorName);
            mTopicGroupId.setGroup_id(group_id);
            mTopicGroupId.setTopic_name(topic);
            mTopicGroupIdDao.save(mTopicGroupId);
        }
        return startResult;
    }*/

    /**
     * name是用户指定停止的connector名称
     *
     * @param name connector名称
     * @return com.unistack.tamboo.commons.json {"isSucceed":boolean,"msg":msg}  状态码信息已经判断
     */
    /*public JSONObject stopKafkaSinkConnectorByName(String name) {
        return ddAction.stopKafkaSinkConnectorByName(name);
    }*/

    /**
     * 获得所有connector的status,state只有RUNNING/FAILED/UNASSIGNED/STOPPED四种
     *
     * @return com.unistack.tamboo.commons.json
     * {"isSucceed":true,"msg":[{"name":"connector_name","status":{"state":"RUNNING/FAILED/UNASSIGNED/STOPPED"}}...]
     * {"isSucceed",false,"msg":"错误信息"}
     */
    /*public JSONObject getTaskStatus() {
        return ddActi on.getTaskStatus();
    }*/

    /**
     * @param name 获得指定connector的状态信息
     * @return com.unistack.tamboo.commons.json
     * 成功:{"isSucceed":true,"status":{"state":"RUNNING/FAILED/PAUSED/",...}
     * 失败:{"isSucceed":false,"msg":"错误信息"}
     */
    /*public JSONObject getTaskStatusByConnectorName(String name) {
        return ddAction.getTaskStatusByConnectorName(name);
    }*/


    /**
     * 获取所有运行中的connector
     *
     * @return com.unistack.tamboo.commons.json
     * 成功:{"isSucceed":true,"msg":[n1,n2,n3...]}
     * 失败:{"isSucceed":false,"msg":"失败信息"}
     */
    public JSONObject showRunningConnector() {
        List<MTopicGroupId> byStatus = mTopicGroupIdDao.findByStatus(1);
        JSONArray array = new JSONArray();
        for (int i = 0; i < byStatus.size(); i++) {
            array.add(byStatus.get(i).getConnectorName());
        }
//        return ddAction.showRunningConnector();
        return DdUtil.succeedResult(array);
    }


//    /**
//     *
//     * @param topic 操作的topic名称
//     * @param seed  kafka连接ip
//     * @param port  kafka连接端口
//     * @return com.unistack.tamboo.commons.json {"isSucceed":true,"msg":long}
//     */
//    public JSONObject getTopicIncrement(String topic, String seed, int port){
//        return ddAction.getTopicIncrement(topic,seed,port);
//    }


    /**
     * 停止所有运行中的connector 后面可能改为停止所有运行中的sinkConnector
     *
     * @return com.unistack.tamboo.commons.json {"isSucceed":boolean,"msg":string}
     */
    /*public JSONObject stopAllConnector() {
        return ddAction.stopAllConnector();
    }*/

    /**
     * 获得给定groupId在给定topic中的总offset
     *
     * @param topic
     * @param groupId
     * @param partitionNum
     * @return
     */
    /*public JSONObject getTotalOffsetByGroupId(String topic, String groupId, int partitionNum) {
        return ddAction.getTotalOffsetByGroupId(topic, groupId, partitionNum);
    }*/


}
