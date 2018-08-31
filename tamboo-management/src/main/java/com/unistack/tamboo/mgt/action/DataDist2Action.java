package com.unistack.tamboo.mgt.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.mgt.dao.calc.OfflineDataSourceDao;
import com.unistack.tamboo.mgt.dao.monitor.MTopicGroupIdDao;
import com.unistack.tamboo.mgt.model.calc.OfflineDataSource;
import com.unistack.tamboo.mgt.model.monitor.MTopicGroupId;
import com.unistack.tamboo.mgt.utils.SpringContextHolder;
import com.unistack.tamboo.sa.dd.util.DdUtil;
import com.unistack.tamboo.sa.dd2.ConnectSinkService;
import java.util.List;
import static java.util.stream.Collectors.toList;


/**
 * @author anning
 * @date 2018/6/8 下午3:23
 * @description:
 */
public class DataDist2Action {
    private static MTopicGroupIdDao mTopicGroupIdDao = SpringContextHolder.getBean(MTopicGroupIdDao.class);
    private static OfflineDataSourceDao offlineDataSourceDao = SpringContextHolder.getBean(OfflineDataSourceDao.class);
    /*
    {
    "type": "mysql/oracle",
    "kafkaAclName":"acl连接用户名#string",
    "kafkaAclPassword":"acl连接用户密码#string",
    "wf_id":"#int",
    "fields": [{
        "jdbc_url":"数据库连接url#string",
        "jdbc_username":"数据库用户名#string",
        "jdbc_password":"数据库密码#string",
        "table_name":"表名#string",
        "tasks_max":"最大任务数#int",
        "topics":"topic名称#string",
        "whitelist":"字段白名单#string",
        "pk_fields":"主键字段#string"
          }]
    }
     */

    /**
     * @param args 配置
     * @return {"groupId":"group-sink1","topic":"test-all5","connectName":"sink1","isSucceed":true}
     */
    public static JSONObject startKafkaSink(JSONObject args) {
        String dataSourceName = args.getString("dataSourceName");

        List<OfflineDataSource> byDataSourceNameIgnoreCase = offlineDataSourceDao.findByDataSourceNameIgnoreCase(dataSourceName);
        OfflineDataSource offlineDataSource = byDataSourceNameIgnoreCase.get(0);
        if (offlineDataSource != null) {
            String table_name = args.getString("table_name");
            String username = offlineDataSource.getUsername();
            String password = offlineDataSource.getPassword();
            String url = offlineDataSource.toUrl();

            JSONObject fields = args.getJSONObject("fields");
            fields.put("table_name", table_name);
            fields.put("user_name", username);
            fields.put("user_pwd", password);
            fields.put("jdbc_url", url);
            fields.put("pk_field", fields.getString("primaryKey"));
            args.put("fields", fields);
        }
        JSONObject checkResult = ConnectSinkService.checkConfig(args);

        if (!checkResult.getBoolean("isSucceed")) {
            return checkResult;
        }
        JSONObject startSink = ConnectSinkService.startSink(args);
        if (!startSink.getBoolean("isSucceed")) {
            return startSink;
        }
        String connectName = startSink.getString("connectName");
        String groupId = startSink.getString("groupId");
        String topic = startSink.getString("topic");
        int wf_id = (int) args.getOrDefault("wf_id", 3);
//            int wf_id = 1;
        //存表中
        MTopicGroupId mTopicGroupId = new MTopicGroupId();
        mTopicGroupId.setConnectorName(connectName);
        mTopicGroupId.setTopic_name(topic);
        mTopicGroupId.setGroup_id(groupId);
        mTopicGroupId.setStatus(1);
        mTopicGroupId.setConfig(args.toJSONString());
        mTopicGroupId.setWfId(wf_id);
        mTopicGroupIdDao.save(mTopicGroupId);
        return startSink;
    }

    /**
     * 通过connectName来关闭下发操作,  更新m_topic_groupId表中的数据
     *
     * @param wf_id 停止by
     * @return {"isSucceed":true,"msg":"成功停止:sink1"}
     */
    public static JSONObject stopKafkaSink(int wf_id) {
        JSONObject result = new JSONObject();
        List<MTopicGroupId> byWf_id = mTopicGroupIdDao.findByWfId(wf_id);
        List<String> collect = byWf_id.stream().map(MTopicGroupId::getConnectorName).collect(toList());
        for (String connectName : collect) {
            if (mTopicGroupIdDao.findByConnectorName(connectName) == null) {
                result = DdUtil.failResult(String.format("未发现%s信息", connectName));
            } else {
                result = ConnectSinkService.stopSink(connectName);
                MTopicGroupId one = mTopicGroupIdDao.findOne(connectName);
                one.setStatus(0);
                mTopicGroupIdDao.save(one);
            }
        }
        return result;
    }

    /**
     * 启动下发任务，重启
     *
     * @param wf_id 服务id
     * @return json
     */
    public static JSONObject startKafkaSink(int wf_id) {
        List<MTopicGroupId> mList = mTopicGroupIdDao.findByWfId(wf_id);
        List<String> connectNameList = mList.stream().map(MTopicGroupId::getConnectorName).collect(toList());
        for (String connectName : connectNameList) {
            MTopicGroupId mtg = mTopicGroupIdDao.findByConnectorName(connectName);
            JSONObject conf = JSON.parseObject(mtg.getConfig());
            conf.put("connectName", mtg.getConnectorName());
            JSONObject startResult = startKafkaSink(conf);

            if (!startResult.getBoolean("isSucceed")) {
                return DdUtil.failResult("数据下发模块启动失败：" + connectName);
            }
        }
        return DdUtil.succeedResult("数据下发模块启动成功！");
    }

    /**
     * 通过connectName来查询下发状态
     *
     * @param connectName 查询by
     * @return {"isSucceed":true,"msg":1}  0代表正常停止 1代表正常运行 2代表异常停止
     * {"isSucceed":false,"msg":errmsg}
     */
    public static JSONObject getStatusByConnectName(String connectName) {
        MTopicGroupId byConnectorName = mTopicGroupIdDao.findByConnectorName(connectName);
        if (byConnectorName.getStatus() == 0) {
            return DdUtil.succeedResult(0);
        }
        JSONObject statusByConnectName = ConnectSinkService.getStatusByConnectName(connectName);
        if (!statusByConnectName.getBoolean("isSucceed")) {
            return statusByConnectName;
        }
        JSONObject msg = statusByConnectName.getJSONObject("msg");
        String state = msg.getString("state");
        if ("FAILED".equalsIgnoreCase(state)) {
            JSONObject monitorMsg = DdUtil.succeedResult(2);
            monitorMsg.put("trace",msg.getString("trace"));
            return monitorMsg;
        } else {
            return DdUtil.succeedResult(1);
        }
    }

    /**
     * 获得所有的connectName
     *
     * @return connectNameList
     */
    public static List<String> getAllConnectName() {
        //调用监控，将下发线程状态更新到数据库，目前是告警，用户手动停止，以达到更新目的
//        JSONObject runningConnectors = ConnectSinkService.showRunningConnectors();
//        if (!runningConnectors.getBoolean("isSucceed")){
//            return new ArrayList<String>();
//        }
//        JSONArray array = runningConnectors.getJSONArray("msg");
//        List<String> list = JSONObject.parseArray(array.toJSONString(), String.class);
//        return list;
        List<MTopicGroupId> runningList = mTopicGroupIdDao.findByStatus(1);
        return runningList.stream().map(MTopicGroupId::getConnectorName).collect(toList());
    }


    /**
     * 获取所有运行中的connector
     *
     * @return json
     * 成功:{"isSucceed":true,"msg":[n1,n2,n3...]}
     * 失败:{"isSucceed":false,"msg":"失败信息"}
     */
    public static List<String> showRunningConnector() {
        /*List<MTopicGroupId> byStatus = mTopicGroupIdDao.findByStatus(1);
        JSONArray array = new JSONArray();
        for (int i = 0; i < byStatus.size(); i++) {
            array.add(byStatus.get(i).getConnectorName());
        }
        return DdUtil.succeedResult(array);*/
//        return ConnectSinkService.showRunningConnectors();
        List<MTopicGroupId> runningList = mTopicGroupIdDao.findByStatus(1);
        return runningList.stream().map(MTopicGroupId::getConnectorName).collect(toList());
    }
}
