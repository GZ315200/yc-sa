package com.unistack.tamboo.mgt.service.collect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.unistack.tamboo.commons.utils.OkHttpResponse;
import com.unistack.tamboo.commons.utils.OkHttpUtils;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.mgt.action.DcAction;
import com.unistack.tamboo.mgt.action.MqAction;
import com.unistack.tamboo.mgt.common.RestCode;
import com.unistack.tamboo.mgt.common.ServerResponse;
import com.unistack.tamboo.mgt.common.enums.DataSourceEnum;
import com.unistack.tamboo.mgt.common.enums.EWebSocketTopic;
import com.unistack.tamboo.mgt.common.page.PageConvert;
import com.unistack.tamboo.mgt.common.page.PaginationData;
import com.unistack.tamboo.mgt.dao.collect.*;
import com.unistack.tamboo.mgt.dao.sys.UserDao;
import com.unistack.tamboo.mgt.dao.sys.UserGroupDao;
import com.unistack.tamboo.mgt.model.collect.*;
import com.unistack.tamboo.mgt.model.monitor.MCollect;
import com.unistack.tamboo.mgt.model.sys.SysUser;
import com.unistack.tamboo.mgt.model.sys.SysUserGroup;
import com.unistack.tamboo.mgt.model.sys.UserSession;
import com.unistack.tamboo.mgt.service.BaseService;
import com.unistack.tamboo.mgt.helper.TopicHelper;
import com.unistack.tamboo.mgt.utils.CharacterUtils;
import com.unistack.tamboo.mgt.utils.MonitorUtils;
import com.unistack.tamboo.mgt.utils.SecUtils;
import com.unistack.tamboo.sa.dc.flume.common.DcConfig;
import com.unistack.tamboo.sa.dc.flume.common.DcResult;
import com.unistack.tamboo.sa.dc.flume.common.DcType;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;


/**
 * @program: tamboo-sa
 * @description: 数据源列表管理服务实现类
 * @author: Asasin
 * @create: 2018-05-17 13:52
 **/
@Service
public class DataSourceServiceImpl extends BaseService {

    private static  Logger logger = LoggerFactory.getLogger(DataSourceServiceImpl.class);
    private static Long HTTPPORT = Long.valueOf(6045);
    private static String INPUTCHARSET = "UTF-8";

    private static String START_CONNECTOR_URL = "%s/connectors";
    private static String DELETE_CONNECTOR_URL = "%s/connectors/%s";
    private static String SHOW_CONNECTOR_URL = "%s/connectors";
    private static String TASK_STATUS_URL = "%s/connectors/%s/tasks/0/status";
    private static String PAUSE_SINK_URL = "%s/connectors/%s/pause";
    private static String RESUME_SINK_URL = "%s/connectors/%s/resume";
    private static String RESTART_SINK_URL = "%s/connectors/%s/restart";

    @Autowired
    private DataSourceListDao dataSourceListDao;

    @Autowired
    private TopicInfoDao topicInfoDao;

    @Autowired
    private DataSourceDictDao dataSourceDictDao;

    @Autowired
    private DataSourceConfDao dataSourceConfDao;

    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    private TopicGroupDao topicGroupDao;

    @Autowired
    private DataFlowInfoDao dataFlowInfoDao;

    @Autowired
    private TopicHelper topicHelper;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private DataSourceHostDao dataSourceHostDao;

    @Autowired
    private DataSourceDbDao dataSourceDbDao;
    /**
     * dataSource paging list
     * @param queryType
     * @param queryName
     * @param orderBy
     * @param pageIndex
     * @param pageSize
     * @return
     */
    //不做类型检查
    @SuppressWarnings(value = "unchecked")
    public ServerResponse<PaginationData<DataSourceList>> list(String queryType, String queryName, String orderBy, int pageIndex, int pageSize) {
        List<String> userNames = null;
        try {
            userNames = getLoginUsers();
        } catch (Exception e) {
            logger.error("", e);
            return ServerResponse.createByErrorCodeAndMsg(RestCode.NEED_LOGIN.getStatus(), RestCode.NEED_LOGIN.getMsg());
        }
        Page<DataSourceList> dataSourceLists = null;
        Sort sort = new Sort(Sort.Direction.DESC,orderBy);
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);
        if (StringUtils.isBlank(queryType) && StringUtils.isNotBlank(queryName)) {
            dataSourceLists = dataSourceListDao.getDataSourceListByQueryName(pageable, queryName, userNames);
        } else if (StringUtils.isNotBlank(queryType) && StringUtils.isBlank(queryName)) {
            dataSourceLists = dataSourceListDao.getDataSourceListByQueryType(pageable, queryType, userNames);
        } else if (StringUtils.isNotBlank(queryType) && StringUtils.isNotBlank(queryName)) {
            dataSourceLists = dataSourceListDao.getDataSourceListByQuery(pageable, queryType, queryName, userNames);
        } else {
            dataSourceLists = dataSourceListDao.getAllByCreateByIn(pageable, userNames);
        }
        return ServerResponse.createBySuccess(PageConvert.convertJpaPage2PaginationData(dataSourceLists, pageSize));
    }

    /**
     * ************************************ DataSource by flume ********************************************************
     */

    /**
     * add Queue，File，HTTP dataSource by flume
     * Queue:KAFKA,ROCKETMQ,RABBITMQ,ACTIVEMQ
     * File:EXEC,TAILDIR,SYSLOG,NETCAT
     * Http:HTTP,WEBSERVICE,REST
     * @param dataSourceListAS
     * @return
     */
    public ServerResponse addByFlume(DataSourceListAS dataSourceListAS) {
        DataSourceList dataSourceList = dataSourceListAS.getDataSourceList();
        //get login session
        UserSession userSession = null;
        try {
            userSession = SecUtils.findLoggedUserSession();
        } catch (Exception e) {
            logger.error("", e);
            return ServerResponse.createByErrorCodeAndMsg(RestCode.NEED_LOGIN.getStatus(), RestCode.NEED_LOGIN.getMsg());
        }
        String userName = userSession.getUsername();
        //check dataSourceName
        if (!checkInputInfo(dataSourceListAS)) {
            return ServerResponse.createByErrorMsg("The dataSourceName already exists ");
        }
        //check exec collect command
        if (StringUtils.equals(dataSourceList.getDataSourceType(), "EXEC")){
            String input = dataSourceListAS.getJsonObject().getString("command");
        String[] commands1 = new String[]{"rm", "mv", "cp", "vi"};
        String[] commands2 = new String[]{"tail", "cat", "", "vi"};
            for (String str : commands1) {
                if (input.startsWith(str)) {
                    return ServerResponse.createByErrorMsg("The EXEC collect command :" + input + "does not support");
                }
            }
        }
        //save topicInfo
        ServerResponse serverResponse = saveTopicInfo(dataSourceListAS,userSession);
        long topicId = topicInfoDao.getTopicInfoByTopicName(serverResponse.getData().toString()).getTopicId();
        if (!serverResponse.isSuccess()){
            return ServerResponse.createByErrorMsg(serverResponse.getMsg());
        }
        //save dataSource
        HTTPPORT = HTTPPORT + 1;
        dataSourceList.setHttpPort(HTTPPORT);
        dataSourceList.setFlag(DataSourceEnum.DataSourceState.STOPPED.getValue());
        dataSourceList.setUseNum(0);
        dataSourceList.setTopicId(topicId);
        dataSourceList.setCreateTime(new Date());
        dataSourceList.setCreateBy(userName);
        dataSourceListDao.save(dataSourceList);
        //save dataSourceConf
        String dcType = dataSourceList.getDataSourceType();
        JSONObject conf = dataSourceListAS.getJsonObject();
        if (StringUtils.isBlank(conf.getString("inputCharset"))) {
            conf.put("inputCharset", INPUTCHARSET);
        }
        if ("WEBSERVICE".equals(dcType)) {
            saveConf(dcType, dataSourceListAS, conf);
        } else if ("EXEC".equals(dcType)) {
            saveConf(dcType, dataSourceListAS, conf);
        } else if ("NETCAT".equals(dcType)) {
            saveConf(dcType, dataSourceListAS, conf);
        } else if ("SYSLOG".equals(dcType)) {
            saveConf(dcType, dataSourceListAS, conf);
        } else if ("TAILDIR".equals(dcType)) {
            String str1  = conf.getString("filegroupsPath");
            if (str1.contains(",")) {
                String[] str2 = str1.split(",");
                List<String> list = getRandomNum(str2.length);
                String str3 = "";
                for (String string : list) {
                    str3 +=string + " ";
                }
                conf.put("filegroups", str3.trim());
            }else {
                conf.put("filegroups", getRandomNum(1).get(0));
            }
            saveConf(dcType, dataSourceListAS, conf);
        } else if ("HTTP".equals(dcType)){
            saveConf(dcType, dataSourceListAS, conf);
        }else {
            return ServerResponse.createByErrorMsg("add dataSource faild, there some wrong whith inputting dataSourceType");
        }
        return ServerResponse.createBySuccess();
    }

    /**
     * start dataSource
     * @param dataSourceId
     * @return
     */
    public ServerResponse startByFlume(Long dataSourceId) {
        //check runningStatus
        Integer flag = dataSourceListDao.getDataSourceListByDataSourceId(dataSourceId).getFlag();
        if (flag != DataSourceEnum.DataSourceState.STOPPED.getValue()) {
            return ServerResponse.createByErrorMsg("start dataSource failed,cause these dataSource is not stopped");
        }
        //encapsulation DcConfig
        DataSourceList dataSourceList = dataSourceListDao.getDataSourceListByDataSourceId(dataSourceId);
        TopicInfo topicInfo = topicInfoDao.getTopicInfoByTopicId(dataSourceList.getTopicId());
        DcConfig dcConfig = new DcConfig();
        Map<String, Object> data = new HashMap<String, Object>();
        String topic = topicInfo.getTopicName();
        data.put("type", dataSourceList.getDataSourceType());
        data.put("topic", topic);
        data.put("username", topicInfo.getTopicAclUsername());
        data.put("password", topicInfo.getTopicAclPassword());
        data.put("servers", TambooConfig.KAFKA_BROKER_LIST);
        data.put("httpPort", dataSourceList.getHttpPort());
        DataSourceHost dataSourceHost = dataSourceHostDao.getDataSourceHostsById(dataSourceList.getHostId());
        data.put("ip", dataSourceHost.getIp());
        data.put("user", dataSourceHost.getUsername());
        data.put("pwd", dataSourceHost.getPasswd());
        data.put("path", dataSourceHost.getPath());
        JSONObject obj = new JSONObject(data);
        dcConfig.setConf(obj);
        String dcType = dataSourceList.getDataSourceType();
        if ("WEBSERVICE".equals(dcType)) {
            dcConfig.setDcType(DcType.WEBSERVICE);
            getConf(dataSourceId, dcConfig);
        } else if ("EXEC".equals(dcType)) {
            dcConfig.setDcType(DcType.EXEC);
            getConf(dataSourceId, dcConfig);
        } else if ("NETCAT".equals(dcType)) {
            dcConfig.setDcType(DcType.NETCAT);
            getConf(dataSourceId, dcConfig);
        } else if ("SYSLOG".equals(dcType)) {
            dcConfig.setDcType(DcType.SYSLOG);
            getConf(dataSourceId, dcConfig);
        } else if ("TAILDIR".equals(dcType)) {
            dcConfig.setDcType(DcType.TAILDIR);
            getConf(dataSourceId, dcConfig);
        } else if ("HTTP".equals(dcType)){
            dcConfig.setDcType(DcType.HTTP);
            getConf(dataSourceId, dcConfig);
        } else if ("KAFKA".equals(dcType)){
            dcConfig.setDcType(DcType.KAFKA);
            getConf(dataSourceId, dcConfig);
        }else{
            return ServerResponse.createByErrorMsg("start dataSource failed, there some wrong whith inputting dataSourceType");
        }
        dataSourceListDao.setFlag(dataSourceId, DataSourceEnum.DataSourceState.STARTING.getValue());
        //asyn start
        new Thread() {
            @Override
            public void run() {
                try {
                    DcResult dcResult = DcAction.createAndStartDataCollector(dcConfig);
                    if (dcResult.isCode()) {
                        dataSourceListDao.setFlag(dataSourceId, DataSourceEnum.DataSourceState.STARTED.getValue());
                        JSONObject jsonObject  = new JSONObject();
                        jsonObject.put("dataSourceId",dataSourceId);
                        jsonObject.put("flag","1");
                        jsonObject.put("message","数据源已启动");
                        template.convertAndSend(EWebSocketTopic.TABOO_DATASOURCE_STATE.getName(),jsonObject);
                    }
                } catch (Exception e) {
                    dataSourceListDao.setFlag(dataSourceId, DataSourceEnum.DataSourceState.STOPPED.getValue());
                    logger.error("start dataSource failed", e);
                }
            }
        }.start();
        return ServerResponse.createBySuccess();

//        try {
//            ExecutorService executor = Executors.newFixedThreadPool(1);
//            Future<DcResult> result = executor.submit(new Callable<DcResult>() {
//                @Override
//                public DcResult call() throws Exception {
//                    DcResult dcResult = DcAction.createAndStartDataCollector(dcConfig);
//                    if (dcResult.isCode()) {
//                        dataSourceListDao.setFlag(dataSourceId, DataSourceEnum.DataSourceState.RUNNING.getValue());
//                        return new DcResult(true, "start dataSource successfully");
//                    }
//                    return new DcResult(false, "start dataSource failed");
//                }
//            });
//
//            if (result.isDone() && !result.isCancelled()) {
//                DcResult dcResult = result.get();
//                if (dcResult.isCode()) {
//                    return ServerResponse.createBySuccess();
//                }
//            }
//            if(!executor.isShutdown()){
//                executor.shutdown();
//            }
//        }catch (Exception e){
//            return ServerResponse.createByErrorMsg("start dataSource failed");
//        }
//        return ServerResponse.createBySuccess();

//        //同步
//        DcResult dcResult = DcAction.createAndStartDataCollector(dcConfig);
//        if (dcResult.isCode()) {
//            dataSourceListDao.setFlag(dataSourceId, DataSourceEnum.DataSourceState.RUNNING.getValue());
//            return ServerResponse.createBySuccess("start dataSource successfully");
//        }
//        return ServerResponse.createBySuccess(dcResult.getMsg());
    }

    /**
     * stop dataSource
     * @param dataSourceId
     * @return
     */
    public ServerResponse stopByFlume(Long dataSourceId) {
        //check
        Integer flag = dataSourceListDao.getDataSourceListByDataSourceId(dataSourceId).getFlag();
        if (flag != DataSourceEnum.DataSourceState.STARTED.getValue()) {
            return ServerResponse.createByErrorMsg("stop dataSource failed,cause these dataSource is not started");
        }
        DataSourceList dataSourceList = dataSourceListDao.getDataSourceListByDataSourceId(dataSourceId);
        JSONObject jsonObject = new JSONObject();
        DataSourceHost dataSourceHost = dataSourceHostDao.getDataSourceHostsById(dataSourceList.getHostId());
        jsonObject.put("ip", dataSourceHost.getIp());
        jsonObject.put("user", dataSourceHost.getUsername());
        jsonObject.put("httpPort", dataSourceList.getHttpPort());
        DcConfig dcConfig = new DcConfig();
        dcConfig.setConf(jsonObject);
        DcType dcType = getDcType(dataSourceList.getDataSourceType());
        if (dcType == null) {
            return ServerResponse.createByErrorMsg("failed,cause the dcType must be specified ");
        }
        dcConfig.setDcType(dcType);
        dataSourceListDao.setFlag(dataSourceId, DataSourceEnum.DataSourceState.STOPPING.getValue());
        //asyn stop
        new Thread() {
            @Override
            public void run() {
                try {
                    DcResult dcResult = DcAction.stopCollector(dcConfig);
                    if (dcResult.isCode()) {
                        dataSourceListDao.setFlag(dataSourceId, DataSourceEnum.DataSourceState.STOPPED.getValue());
                        JSONObject jsonObject  = new JSONObject();
                        jsonObject.put("dataSourceId",dataSourceId);
                        jsonObject.put("flag","0");
                        jsonObject.put("message","数据源已停止");
                        template.convertAndSend(EWebSocketTopic.TABOO_DATASOURCE_STATE.getName(),jsonObject);
                    }
                } catch (Exception e) {
                    dataSourceListDao.setFlag(dataSourceId, DataSourceEnum.DataSourceState.STARTED.getValue());
                    logger.error("stop dataSource failed", e);
                }
            }
        }.start();
        return ServerResponse.createBySuccess();
    }

    /**
     * @param dataSourceId
     * @param dcConfig
     */
    public void getConf(long dataSourceId, DcConfig dcConfig) {
        List<DataSourceConf> list = dataSourceConfDao.findAllByDataSourceId(dataSourceId);
        for (DataSourceConf dataSourceConf : list) {
            dcConfig.getConf().put(dataSourceConf.getDataKey(), dataSourceConf.getDataValue());
        }
    }

    /**
     *
     */
    public void saveConf(String dcType, DataSourceListAS dataSourceListAS, JSONObject conf) {
        Set<DataSourceDict> set = dataSourceDictDao.findByDataSourceType(dcType);
        for (DataSourceDict dataSourceDict : set) {
            DataSourceConf dataSourceConf = new DataSourceConf();
            long dataSourceId = dataSourceListDao.getDataSourceListByDataSourceName(dataSourceListAS.getDataSourceList().getDataSourceName()).getDataSourceId();
            dataSourceConf.setDataSourceId(dataSourceId);
            dataSourceConf.setDataKey(dataSourceDict.getConfName());
            String value = conf.getString(dataSourceDict.getConfName());
            dataSourceConf.setDataValue(value);
            dataSourceConfDao.save(dataSourceConf);
        }
    }

    /**
     * ************************************ DataSource by kafka connect ************************************************
     */

    /**
     * add DB dataSource by kafka connect(confluent)
     * DB:MYSQL,ORACLE,SQLSERVER,PG,HBASE
     * @param dataSourceListAS
     * @return
     */
    public ServerResponse addByConnect(DataSourceListAS dataSourceListAS) {
        DataSourceDb dataSourceDb = dataSourceListAS.getDataSourceDb();
        DataSourceList dataSourceList = dataSourceListAS.getDataSourceList();
        //get login session
        UserSession userSession = null;
        try {
            userSession = SecUtils.findLoggedUserSession();
        } catch (Exception e) {
            logger.error("", e);
            return ServerResponse.createByErrorCodeAndMsg(RestCode.NEED_LOGIN.getStatus(), RestCode.NEED_LOGIN.getMsg());
        }
        String userName = userSession.getUsername();
        if(dataSourceDb.getDbMaxtask()==0){
            return ServerResponse.createByErrorMsg("The matTask must Greater than 0");
        }
        //check dataSourceName
        if(!checkInputInfo(dataSourceListAS)){
            return ServerResponse.createByErrorMsg("The dataSourceName already exists ");
        }
        //save topicInfo
        ServerResponse serverResponse = saveTopicInfo(dataSourceListAS,userSession);
        long topicId = topicInfoDao.getTopicInfoByTopicName(serverResponse.getData().toString()).getTopicId();
        if (!serverResponse.isSuccess()){
            return ServerResponse.createByErrorMsg(serverResponse.getMsg());
        }
        //get db dataSource info
        //get connect conf info
        JSONObject dbConf = getDbConf(dataSourceList.getDataSourceName(),dataSourceList.getDataSourceType(),dataSourceDb);
        //save dataSource
        dataSourceList.setFlag(DataSourceEnum.DataSourceState.STOPPED.getValue());
        dataSourceList.setUseNum(0);
        dataSourceList.setTopicId(topicId);
        dataSourceList.setCreateTime(new Date());
        dataSourceList.setCreateBy(userName);
        dataSourceListDao.save(dataSourceListAS.getDataSourceList());
        //save db dataSource
        DataSourceList dataSourceList1 = dataSourceListDao.getDataSourceListByTopicId(topicId);
        dataSourceDb.setDataSourceId(dataSourceList1.getDataSourceId());
        dataSourceDb.setDbConfs(dbConf.toString());
        dataSourceDbDao.save(dataSourceDb);
        return ServerResponse.createBySuccess();
    }

    /**
     * 检查connector config配置
     * @param conf
     * @return
     */
    public ServerResponse checkConfig(JSONObject conf) {
        ServerResponse serverResponse1 = checkAllConnectUrl(TambooConfig.KAFKA_CONNECTOR_URL);
        if (!serverResponse1.isSuccess()){
            return serverResponse1;
        }
        ServerResponse serverResponse2 = checkRegistryUrl(TambooConfig.KAFKA_REGISTRY_URL);
        if (!serverResponse2.isSuccess()){
            return serverResponse2;
        }
        JSONObject fields = conf.getJSONObject("config");
        String jdbcUrl = fields.getString("connection.url");
        String userName = fields.getString("connection.user");
        String userPwd = fields.getString("connection.password");
        String tableName = fields.getString("table.whitelist");
        String[] strings = jdbcUrl.split(":");
        String type = strings[1];
        return getJdbcConnection(type,jdbcUrl,userName,userPwd);
    }

    public static ServerResponse checkAllConnectUrl(String connectUrl){
        String[] kafkaConnectorUrls = StringUtils.split(connectUrl, ",");
        for (int i = 0; i < kafkaConnectorUrls.length; i++) {
            ServerResponse connectCheck = checkConnectUrl(kafkaConnectorUrls[i]);
            if (!connectCheck.isSuccess()){
                return connectCheck;
            }
        }
        return ServerResponse.createBySuccess();
    }

    /**
     * 检查registry url 是否正常连接
     * @param registryUrl url
     * @return json
     */
    public static ServerResponse checkRegistryUrl(String registryUrl){
        OkHttpResponse response = OkHttpUtils.get(makeHttpPre(registryUrl) + "/subjects");
        int code = response.getCode();
        if (HttpStatus.SC_OK!=code){
            return ServerResponse.createByErrorMsg("连接不上connect_url!");
        }
        return ServerResponse.createBySuccess();
    }


    /**
     * 检查connector url 是否正常连接
     * @param connectUrl url
     * @return json
     */
    public static ServerResponse checkConnectUrl(String connectUrl){
        OkHttpResponse response = OkHttpUtils.get(makeHttpPre(connectUrl) + "/connectors");
        int code = response.getCode();
        if (HttpStatus.SC_OK!=code){
            return ServerResponse.createByErrorMsg("连接不上connect_url!");
        }
        return ServerResponse.createBySuccess();
    }

    public static String getFirstConnectUrl(){
        String[] kafkaConnectorUrls = StringUtils.split(TambooConfig.KAFKA_CONNECTOR_URL, ",");
        return makeHttpPre(kafkaConnectorUrls[0]);
    }


    public static String makeHttpPre(String url){
        if (!url.startsWith("http://")){
            url = "http://"+url;
        }
        return url;
    }

    /**
     * 判断数据库能否连接
     * @param dataBaseType  数据库类型
     * @param jdbcURL   jdbc url
     * @param userName  用户名
     * @param passwd    密码
     * @return  json
     */
    public static ServerResponse getJdbcConnection(String dataBaseType, String jdbcURL, String userName, String passwd) {
        Connection connection = null;

        ServerResponse result;
        try {
            Properties properties = new Properties();
            properties.setProperty("user", userName);
            properties.setProperty("password", passwd);
            properties.setProperty("useSSL", "false");
            if (!"mysql".equalsIgnoreCase(dataBaseType)) {
                Class.forName(DataSourceEnum.DriverClass.getDriverClassByDatabaseTyoe(dataBaseType));
            }

            connection = DriverManager.getConnection(jdbcURL, properties);
            result = ServerResponse.createBySuccess("数据库连接成功！");
            return result;
        } catch (SQLException var18) {
            logger.error("数据库连接失败 ========>" + var18.toString());
            result = ServerResponse.createByErrorMsg("连接数据库失败");
        } catch (ClassNotFoundException var19) {
            logger.error("未找到" + dataBaseType + "对应加载类 ========>" + var19.toString());
            result = ServerResponse.createByErrorMsg("未找到" + dataBaseType + "对应加载类！");
            return result;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException var17) {
                logger.error("连接数据库失败 ========>" + var17.toString());
                return ServerResponse.createByErrorMsg("连接数据库失败！");
            }
        }
        return result;
    }

    /**
     * start dataSource by connect
     * @param dataSourceId
     * @return
     */
    public ServerResponse startByConnect(Long dataSourceId){
        //check runningStatus
        DataSourceList  dataSourceList = dataSourceListDao.getDataSourceListByDataSourceId(dataSourceId);
        if (dataSourceList.getFlag() != DataSourceEnum.DataSourceState.STOPPED.getValue()){
            return ServerResponse.createByErrorMsg("start dataSource failed,cause these dataSource is not stopped");
        }
        DataSourceDb dataSourceDb = dataSourceDbDao.getByDataSourceId(dataSourceId);
        dataSourceListDao.setFlag(dataSourceId, DataSourceEnum.DataSourceState.STARTING.getValue());
        JSONObject config = JSON.parseObject(dataSourceDb.getDbConfs());
        //check jdbc
        ServerResponse serverResponse1 = checkConfig(config);
        if(!serverResponse1.isSuccess()){
            dataSourceListDao.setFlag(dataSourceId, DataSourceEnum.DataSourceState.STOPPED.getValue());
            return ServerResponse.createByErrorMsg(serverResponse1.getMsg());
        }
//        asyn start
//        new Thread() {
//            @Override
//            public void run() {
//                try {
                    ServerResponse serverResponse = startConnectSource(getFirstConnectUrl(), config);
//                    String connectorName = "source-"+dataSourceList.getDataSourceName();
//                    ServerResponse<JSONObject> serverResponse2 = getTaskStatus(TambooConfig.KAFKA_CONNECTOR_URL, connectorName);
                    if (!serverResponse.isSuccess()) {
                        dataSourceListDao.setFlag(dataSourceId, DataSourceEnum.DataSourceState.STOPPED.getValue());
                        return ServerResponse.createByErrorMsg(serverResponse.getMsg());
                    }else {
                        dataSourceListDao.setFlag(dataSourceId, DataSourceEnum.DataSourceState.STARTED.getValue());
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("dataSourceId", dataSourceId);
                        jsonObject.put("flag", "1");
                        jsonObject.put("message", "数据源已启动");
                        template.convertAndSend(EWebSocketTopic.TABOO_DATASOURCE_STATE.getName(), jsonObject);
//                    }
//                } catch (Exception e) {
//                    dataSourceListDao.setFlag(dataSourceId, DataSourceEnum.DataSourceState.STOPPED.getValue());
//                    logger.error("start dataSource failed", e);
//                }
//            }
//        }.start();
                        return ServerResponse.createBySuccess();
                    }
    }

    /**
     * stop dataSource by connect
     * @param dataSourceId
     * @return
     */
    public ServerResponse stopByConnect(Long dataSourceId) {
        //check
        DataSourceList dataSourceList = dataSourceListDao.getDataSourceListByDataSourceId(dataSourceId);
        if (dataSourceList.getFlag() != DataSourceEnum.DataSourceState.STARTED.getValue()) {
            return ServerResponse.createByErrorMsg("stop dataSource failed,cause these dataSource is not started");
        }
        dataSourceListDao.setFlag(dataSourceId, DataSourceEnum.DataSourceState.STOPPING.getValue());
        String connectorName = "source_"+dataSourceList.getDataSourceName();
        ServerResponse serverResponse = stopConnectSource(getFirstConnectUrl(), connectorName);
        if (!serverResponse.isSuccess()) {
            dataSourceListDao.setFlag(dataSourceId, DataSourceEnum.DataSourceState.STOPPED.getValue());
            return ServerResponse.createByErrorMsg(serverResponse.getMsg());
        }else {
            dataSourceListDao.setFlag(dataSourceId, DataSourceEnum.DataSourceState.STOPPED.getValue());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("dataSourceId", dataSourceId);
            jsonObject.put("flag", "0");
            jsonObject.put("message", "数据源已停止");
            template.convertAndSend(EWebSocketTopic.TABOO_DATASOURCE_STATE.getName(), jsonObject);
            return ServerResponse.createBySuccess();
        }
    }

    /**
     * create and start connect source
     * @param connectorUrl
     * @param config
     * @return
     */
    public ServerResponse startConnectSource(String connectorUrl, JSONObject config) {
        new JSONObject();
        String connectorName = config.getString("name");
        String format = String.format(START_CONNECTOR_URL, connectorUrl);
        ServerResponse result;
        try {
            OkHttpResponse okHttpResponse = OkHttpUtils.post(format, config.toJSONString());
            int statusCode = okHttpResponse.getCode();
            switch(statusCode) {
                case HttpStatus.SC_OK:
                case HttpStatus.SC_CREATED:
                    result =  ServerResponse.createBySuccess(connectorName + " 创建成功！");
                    break;
                case HttpStatus.SC_CONFLICT:
                    result =  ServerResponse.createByErrorMsg("创建connector冲突，查看connector名称是否已存在！");
                    break;
                default:
                    result = ServerResponse.createByErrorMsg("创建connector失败！状态码= " + statusCode);
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("start sink connector error ========>" + e.toString());
            return ServerResponse.createByErrorMsg("不支持的编码类型！");
        } catch (IOException e) {
            logger.error("start sink connector error ========>" + e.toString());
            return ServerResponse.createByErrorMsg("" +
                    "连接connector服务失败！");
        }
        return result;
    }

    /**
     * stop connect source
     * @param connectorUrl
     * @param connectorName
     * @return
     */
    public ServerResponse stopConnectSource(String connectorUrl, String connectorName) {
        String format = String.format(DELETE_CONNECTOR_URL, connectorUrl, connectorName);
        ServerResponse result;
        try {
            OkHttpResponse okHttpResponse = OkHttpUtils.delete(format);
            int statusCode = okHttpResponse.getCode();
            switch(statusCode) {
                case HttpStatus.SC_NO_CONTENT:
                    result =  ServerResponse.createBySuccess(connectorName + "成功停止connector!");
                    break;
                case HttpStatus.SC_NOT_FOUND:
                    result =  ServerResponse.createByErrorMsg("未发现删除的connector!");
                    break;
                default:
                    return ServerResponse.createByErrorMsg("出现异常，状态码=" + statusCode);
            }
        } catch (IOException e) {
            logger.error("stop connector:" + connectorName + "========>" + e.toString());
            return ServerResponse.createByErrorMsg("连接connector服务失败！");
        }
        return result;
    }

    /**
     *
     * @param connectorUrl
     * @return
     */
    public ServerResponse getRunningSourceConnector(String connectorUrl) {
        String format = String.format(SHOW_CONNECTOR_URL, connectorUrl);

        ServerResponse result;
        try {
            OkHttpResponse okHttpResponse = OkHttpUtils.get(format);
            int statusCode = okHttpResponse.getCode();
            if (statusCode != HttpStatus.SC_OK) {
                result=ServerResponse.createByErrorMsg("获取运行中的连接器失败！");
            } else {
                String responseBodyAsString = okHttpResponse.getBody();
                JSONArray runningConnectorArray = JSON.parseArray(responseBodyAsString);
                result=ServerResponse.createBySuccess(runningConnectorArray);
            }
        } catch (Exception e) {
            logger.error("get running connector,conenct url:" + connectorUrl + "========>" + e.toString());
            result=ServerResponse.createByErrorMsg("连接connector服务失败！");
        }

        return result;
    }

    /**
     *
     * @param connectorUrl
     * @param connectorName
     * @return
     */
    public ServerResponse<JSONObject> getTaskStatus(String connectorUrl, String connectorName) {
        String format = String.format(TASK_STATUS_URL, connectorUrl, connectorName);

        try {
            OkHttpResponse okHttpResponse = OkHttpUtils.get(format);
            int statusCode = okHttpResponse.getCode();
            JSONObject status = JSON.parseObject(okHttpResponse.getBody());
            //TODO trace内容有点多，目前不返回
            if (status.containsKey("trace")) {
                status.remove("trace");
            }
            status.put("connectName",connectorName);
            return ServerResponse.createBySuccess(status);
        } catch (Exception e) {
            logger.error(e.toString());
            return ServerResponse.createByErrorMsg("连接connector服务失败！");
        }
    }

    /**
     * get DBUrl
     * @param dcType
     * @param dbUrl
     * @param databaseName
     * @return
     */
    public static String getDbUrl(String dcType,String dbUrl,String databaseName){
        String str = "";
        switch (dcType) {
            case "MYSQL":
                str = "jdbc:mysql://"+dbUrl+"/"+databaseName; //+"?characterEncoding=utf-8&useSSL=true";
                return str;
            case "ORACLE":
                str = "jdbc:oracle:thin:@"+dbUrl+":"+databaseName;
                return str;
            case "SQLSERVER":
                str =  "jdbc:sqlserver://"+dbUrl+";databaseName="+databaseName;
                return str;
            case "PG":
                return str;
            case "HBASE":
                return str;
            default:
                return null;
        }
    }

    /**
     * get DBConf
     * @param dataSourceDb
     * @return
     */
    public static JSONObject getDbConf(String dataSourceName,String dcType,DataSourceDb dataSourceDb){
        //get db dataSource info
        String url = dataSourceDb.getDbUrl();
        String databaseName = dataSourceDb.getDbDatabase();
        int maxTask = dataSourceDb.getDbMaxtask();
        String dbUrl = getDbUrl(dcType,url,databaseName);
        String user = dataSourceDb.getDbUsername();
        String passwd = dataSourceDb.getDbPassword();
        String table = dataSourceDb.getDbTable();
        String mode = dataSourceDb.getDbMode();
        String incretColumn = dataSourceDb.getDbIncretColumn();
        String timstampColumn = dataSourceDb.getDbTimstampColumn();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("{").append("\n")
                .append("\"name\":\"").append("source_").append(dataSourceName).append("\",").append("\n")
                .append("\"config\":{").append("\n")
                .append("\"connector.class\":\"io.confluent.connect.jdbc.JdbcSourceConnector\",").append("\n")
                .append("\"topic.prefix\":\"source_").append(dataSourceName).append("_\",").append("\n")
                .append("\"tasks.max\":\"").append(maxTask).append("\",")
                .append("\"connection.url\":\"").append(dbUrl).append("\",").append("\n")
                .append("\"connection.user\":\"").append(user).append("\",").append("\n")
                .append("\"connection.password\":\"").append(passwd).append("\",").append("\n")
//                .append("\"connection.attempts\":\"5\",").append("\n")
                .append("\"table.whitelist\":\"").append(table).append("\",\n");
                if (StringUtils.equals(mode,DataSourceEnum.ConnectMode.BULK.getTip())){
                    stringBuffer.append("\"mode\":\"").append(mode).append("\"").append("}}");
                }
                if (StringUtils.equals(mode,DataSourceEnum.ConnectMode.INCREAMENT.getTip())){
                    stringBuffer.append("\"mode\":\"").append(mode).append("\",\n")
                            .append("\"").append(mode).append(".column.name\":\"").append(incretColumn).append("\"").append("}}");
                }
                if (StringUtils.equals(mode,DataSourceEnum.ConnectMode.TIMESTAMP.getTip())){
                    stringBuffer.append("\"mode\":\"").append(mode).append("\",\n")
                            .append("\"").append(mode).append(".column.name\":\"").append(timstampColumn).append("\"").append("}}");
                }
                if (StringUtils.equals(mode,DataSourceEnum.ConnectMode.INCREAANDTIME.getTip())){
                    stringBuffer.append("\"mode\":\"").append(mode).append("\",\n")
                            .append("\"timestamp.column.name\":\"").append(timstampColumn).append("\",\n")
                            .append("\"incrementing.column.name\":\"").append(incretColumn).append("\"").append("}}");
                }
        return JSON.parseObject(stringBuffer.toString());
    }


    /**
     * ************************************ DataSource common method ***************************************************
     */

    /**
     * save topicInfo to db
     * @param dataSourceListAS
     * @param userSession
     * @return
     */
    public ServerResponse<String> saveTopicInfo(DataSourceListAS dataSourceListAS,UserSession userSession){
        String topicName = null;
        if(ObjectUtils.isEmpty(dataSourceListAS.getDataSourceDb())){
            topicName = "source_"+dataSourceListAS.getDataSourceList().getDataSourceName() + "_" + userSession.getUsername();
        }else {
            topicName = "source_"+dataSourceListAS.getDataSourceList().getDataSourceName() + "_" + dataSourceListAS.getDataSourceDb().getDbTable();
        }
        //check topicName
        int count2 = topicInfoDao.countTopicInfoByTopicName(topicName);
        if (count2 > 0) {
            return ServerResponse.createByErrorMsg("The source topic already exists ");
        }
        //get topic acl
        SysUserGroup sysUserGroup = userGroupDao.getSysUserGroupByGroupName(userSession.getUserGroup());
        String aclUserName = sysUserGroup.getAclUsername();
        String aclPassWord = sysUserGroup.getAclPassword();
        //create topic information and save it to zookeeper
        MqAction.mQcreateTopic(topicName, 0, (short) 0, aclUserName, aclPassWord, true);
        //save source topicInfo
        TopicInfo topicInfo = new TopicInfo();
        topicInfo.setTopicName(topicName);
        topicInfo.setTopicPartition(TambooConfig.KAFKA_PARTITION);
        topicInfo.setTopicReplication(TambooConfig.KAFKA_REPLICA);
        topicInfo.setTopicAclType("SASL_PLAINTEXT");
        topicInfo.setTopicAclUsername(aclUserName);
//        String adlPass = DESUtil.encrypt(aclPassWord); 加密
        topicInfo.setTopicAclPassword(aclPassWord);
        topicInfo.setTopicType(DataSourceEnum.TopicType.SOURCE.getValue());
        topicInfoDao.save(topicInfo);
        //save topicGroup，Open groups access
        long topicId = topicInfoDao.getTopicInfoByTopicName(topicName).getTopicId();
        List<String> userGroups = dataSourceListAS.getUserGroups();
        Date date = new Date();
        for (String group : userGroups) {
            TopicGroup topicGroup = new TopicGroup();
            topicGroup.setGroupName(group);
            topicGroup.setTopicId(topicId);
            topicGroup.setUpdateTime(date);
            topicGroupDao.save(topicGroup);
        }
        return ServerResponse.createBySuccess(topicName);
    }

    /**
     *delete datasource
     * @param dataSourceId
     * @return
     */
    public ServerResponse deleteDatasource(Long dataSourceId){
        //check topic
        DataSourceList dataSourceList = dataSourceListDao.getDataSourceListByDataSourceId(dataSourceId);
        Integer flag = dataSourceList.getFlag();
        if (flag != DataSourceEnum.DataSourceState.STOPPED.getValue()) {
            return ServerResponse.createByErrorMsg("failed,cause these dataSource is not stopped");
        }
        if (topicInfoDao.countTopicInfoByTopicId(dataSourceList.getTopicId()) == 0) {
            return ServerResponse.createByErrorMsg("topic is not exists");
        }
        List<DataFlowInfo> dataFlowInfoList = dataFlowInfoDao.getDataFlowInfosByDataSourceId((int) dataSourceList.getTopicId().longValue());
        if (dataFlowInfoList.size() > 0) {
            return ServerResponse.createByErrorMsg("failed,cause these dataSource' source topic is used");
        }
        long topicId = dataSourceList.getTopicId();
        //delete the topic information in zookepper,including /brokers/topics/,/kafka-acl/Topic
        String topicName = topicInfoDao.getTopicInfoByTopicId(topicId).getTopicName();
        topicHelper.deleteTopic(topicName);
        topicHelper.deleteTopicAcl(topicName);
        //delete topicInfo
        topicInfoDao.delete(topicId);
        //delete topicGroup
        List<TopicGroup> topicGroups = topicGroupDao.getTopicGroupByTopicId(topicId);
        for (TopicGroup topicGroup : topicGroups) {
            topicGroupDao.delete(topicGroup.getId());
        }
        //delete dataSource
        //connect
        String[] str = new String[]{"MYSQL","ORACLE","SQLSERVER","PG","HBASE"};
        if (ArrayUtils.contains(str,dataSourceList.getDataSourceType())){
            for (String string:str){
                if (StringUtils.equals(dataSourceList.getDataSourceType(),string)){
                    DataSourceDb dataSourceDb = dataSourceDbDao.getByDataSourceId(dataSourceId);
                    dataSourceDbDao.delete(dataSourceDb.getId());
                    break;
                }
            }
        }else {
            //flume
            List<DataSourceConf> list = dataSourceConfDao.findAllByDataSourceId(dataSourceId);
            List<Long> longs = new ArrayList<>();
            for (DataSourceConf dataSourceConf : list) {
                longs.add(dataSourceConf.getId());
            }
            if (longs.size() == 0) {
                return ServerResponse.createByErrorMsg(" dataSource:" + dataSourceId + " conf is not exists");
            }
            dataSourceConfDao.deleteByIds(longs);
        }
        //delete datasuorceList
        dataSourceListDao.delete(dataSourceId);
        return ServerResponse.createBySuccess();
    }

    /**
     * edit dataSource
     * @param dataSourceListAS
     * @return
     */
    public ServerResponse editDatasource(DataSourceListAS dataSourceListAS) {
        DataSourceList dataSourceList = dataSourceListAS.getDataSourceList();
        //check dataSource flag,if is running ,can not edit dataSource
        if (dataSourceList.getFlag()!=DataSourceEnum.DataSourceState.STOPPED.getValue()) {
            return ServerResponse.createByErrorMsg("The dataSourceName is not stopped ");
        }
        //Check if the topic is used
        List<DataFlowInfo> dataFlowInfoList = dataFlowInfoDao.getDataFlowInfosByDataSourceId((int) dataSourceList.getTopicId().longValue());
        if (dataFlowInfoList.size() > 0) {
            return ServerResponse.createByErrorMsg("failed,cause these dataSource' source topic is used");
        }
        //update dataSourceList
        if(!updateDataSource(dataSourceList)) {
            return ServerResponse.createByErrorCodeAndMsg(RestCode.NEED_LOGIN.getStatus(), RestCode.NEED_LOGIN.getMsg());
        }
        String[] str = new String[]{"MYSQL","ORACLE","SQLSERVER","PG","HBASE"};
        if (ArrayUtils.contains(str,dataSourceList.getDataSourceType())){
            //update dataSourceDb
            DataSourceDb dataSourceDb = dataSourceListAS.getDataSourceDb();
            Long dataSourceId = dataSourceListDao.getDataSourceListByDataSourceName(dataSourceList.getDataSourceName()).getDataSourceId();
            DataSourceDb dataSourceDb1 = dataSourceDbDao.getByDataSourceId(dataSourceId);
            JSONObject dbConf = getDbConf(dataSourceList.getDataSourceName(),dataSourceList.getDataSourceType(), dataSourceDb);
            dataSourceDb.setId(dataSourceDb1.getId());
            dataSourceDb.setDbConfs(dbConf.toString());
            dataSourceDbDao.save(dataSourceDb);
        }else {
            //update dataSourceConf
            List<DataSourceConf> confs = dataSourceConfDao.findAllByDataSourceId(dataSourceListAS.getDataSourceList().getDataSourceId());
            Set<String> keys = dataSourceListAS.getJsonObject().keySet();
            for (String key:keys){
                for (DataSourceConf dataSourceConf:confs) {
                    if (dataSourceConf.getDataKey().equals(key)){
                        dataSourceConf.setId(dataSourceConf.getId());
                        dataSourceConf.setDataValue(dataSourceListAS.getJsonObject().getString(key));
                        dataSourceConfDao.save(dataSourceConf);
                        break;
                    }
                }
            }
        }
        //Delete before updating userGroups
        List<TopicGroup> topicGroups = topicGroupDao.getTopicGroupByTopicId(dataSourceListAS.getDataSourceList().getTopicId());
        for (TopicGroup topicGroup : topicGroups) {
            topicGroupDao.delete(topicGroup.getId());
        }
        //save topicGroup
        List<String> userGroups = dataSourceListAS.getUserGroups();
        Date date = new Date();
        for (String group : userGroups) {
            TopicGroup topicGroup = new TopicGroup();
            topicGroup.setGroupName(group);
            topicGroup.setTopicId(dataSourceListAS.getDataSourceList().getTopicId());
            topicGroup.setUpdateTime(date);
            topicGroupDao.save(topicGroup);
        }
        return ServerResponse.createBySuccess();
    }

    /**
     * 更新数据源对象信息
     * @param dataSourceList
     */
    public boolean updateDataSource(DataSourceList dataSourceList){
        DataSourceList dataSourceListOld = dataSourceListDao.getDataSourceListByDataSourceId(dataSourceList.getDataSourceId());
        dataSourceListOld.setDataSourceId(dataSourceListOld.getDataSourceId());
        dataSourceListOld.setDataSourceDsc(dataSourceList.getDataSourceDsc());
        dataSourceListOld.setDataModel(dataSourceList.getDataModel());
        dataSourceListOld.setCreateTime(new Date());
        if(dataSourceListDao.save(dataSourceListOld)==null){
            return false;
        }
        return true;
    }

    /**
     *  query dataSource
     * @param dataSourceId
     * @return
     */
    public ServerResponse<DataSourceListAS> queryById(Long dataSourceId) {
        DataSourceListAS dataSourceListAS = new DataSourceListAS();
        DataSourceDb dataSourceDb = null;
        //get dataSource object
        DataSourceList dataSourceList = dataSourceListDao.getDataSourceListByDataSourceId(dataSourceId);
        //get dataSourceConf or dataSourceDb infomation
        String[] str = new String[]{"MYSQL","ORACLE","SQLSERVER","PG","HBASE"};
        if (ArrayUtils.contains(str,dataSourceList.getDataSourceType())){
            for (String string:str){
                if (StringUtils.equals(dataSourceList.getDataSourceType(),string)){
                    dataSourceDb = dataSourceDbDao.getByDataSourceId(dataSourceId);
                    dataSourceListAS.setDataSourceDb(dataSourceDb);
                    break;
                }
            }
        }else {
            List<DataSourceConf> listConf = dataSourceConfDao.findAllByDataSourceId(dataSourceId);
            JSONObject jsonObject = new JSONObject();
            for (DataSourceConf dataSourceConf : listConf) {
                jsonObject.put(dataSourceConf.getDataKey(),dataSourceConf.getDataValue());
            }
            dataSourceListAS.setJsonObject(jsonObject);
        }
        //get User groups infomation
        List<String> userGroups = new ArrayList<>();
        List<TopicGroup> topicGroups = topicGroupDao.getTopicGroupByTopicId(dataSourceList.getTopicId());
        for (TopicGroup topicGroup:topicGroups) {
            userGroups.add(topicGroup.getGroupName());
        }
        dataSourceListAS.setDataSourceList(dataSourceList);
        dataSourceListAS.setUserGroups(userGroups);
        return ServerResponse.createBySuccess(dataSourceListAS);
    }

    /**
     *获取list列表所有数据源的采集数据量
     * @param
     * @return
     */
    public ServerResponse getTotalSize(){
        List<DataSourceList> dataSourceLists = dataSourceListDao.findAll();
        List<Map<String ,Integer>> totalSize = new ArrayList<>();
        for (DataSourceList dataSourceList:dataSourceLists){
            MCollect mCollect = MonitorUtils.getSourceCollectRecordByTopicId(dataSourceList.getTopicId().intValue());
            Map map = new HashMap();
            map.put("dataSourceId",dataSourceList.getDataSourceId());
            if (Objects.isNull(mCollect)){
                map.put("totalColSize",0);
                map.put("colRate",0);
                String dataSourceName = dataSourceList.getDataSourceName();
                //logger.error("DataSourceName:"+dataSourceName+"数据源采集数据为空");
            }else {
                map.put("totalColSize",mCollect.getTotalColSize());
                map.put("colRate",mCollect.getColRate());
            }
            totalSize.add(map);

        }
        return ServerResponse.createBySuccess(totalSize);
    }

    /**
     * 获取所有已启动的dataSource
     * @return
     */
    public ServerResponse getRunning(){
        return ServerResponse.createBySuccess(dataSourceListDao.getDataSourceListByFlag(DataSourceEnum.DataSourceState.STARTED.getValue()));
    }

    /**
     * 统计当前登录用户组的数据源数量
     * @return
     */
    public ServerResponse countAll() {
        List<String> userNames = null;
        try {
            userNames = getLoginUsers();
        } catch (Exception e) {
            logger.error("", e);
            return ServerResponse.createByErrorCodeAndMsg(RestCode.NEED_LOGIN.getStatus(), RestCode.NEED_LOGIN.getMsg());
        }
        int count = dataSourceListDao.countDataSourceListByCreateByIn(userNames);
        return ServerResponse.createBySuccess(count);
    }

    /**
     * 统计统计当前登录用户组的runging数据源数量
     * @return
     */
    public ServerResponse countRunning() {
        List<String> userNames = null;
        try {
            userNames = getLoginUsers();
        } catch (Exception e) {
            logger.error("", e);
            return ServerResponse.createByErrorCodeAndMsg(RestCode.NEED_LOGIN.getStatus(), RestCode.NEED_LOGIN.getMsg());
        }
        int count = dataSourceListDao.countDataSourceListByFlagAndCreateByIn(DataSourceEnum.DataSourceState.STARTED.getValue(), userNames);
        return ServerResponse.createBySuccess(count);
    }

    /**
     * 统计源数据topic和开放topic数量
     * @return
     */
    public ServerResponse<Map> countDataSourceTopic() {
        int topicType = DataSourceEnum.TopicType.CLOSE.getValue();
        List<TopicInfo> list = topicInfoDao.findByTopicTypeNot(topicType);
        Map<String, Object> map = new HashMap<>();
        map.put("dataSourceCount", list.size());
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 统计源数据topic和开放topic
     * @return
     */
    public ServerResponse<List<TopicInfo>> getTopicByTopicType(int topicType) {
        if (topicType == DataSourceEnum.TopicType.OPEN.getValue() && topicType == DataSourceEnum.TopicType.SOURCE.getValue() && topicType == DataSourceEnum.TopicType.CLOSE.getValue()) {
            return ServerResponse.createBySuccess(topicInfoDao.getTopicInfosByTopicType(topicType));
        }
        return ServerResponse.createByErrorMsg("topicType input was wrong");
    }

    /**
     * 查询指定dataSourceId的dataSource详细信息
     * @return
     */
    public ServerResponse<DataSourceListVo> queryDataSource(Long dataSourceId) {
        DataSourceList dataSourceList = dataSourceListDao.getDataSourceListByDataSourceId(dataSourceId);
        DataSourceListVo dataSourceListVo = new DataSourceListVo();
        dataSourceListVo.setDataSourceType(dataSourceList.getDataSourceType());
        dataSourceListVo.setDataSourceName(dataSourceList.getDataSourceName());
        dataSourceListVo.setDataSourceDsc(dataSourceList.getDataSourceDsc());
        dataSourceListVo.setDataModel(dataSourceList.getDataModel());
        dataSourceListVo.setUseNum(dataSourceList.getUseNum());
        return ServerResponse.createBySuccess(dataSourceListVo);
    }

    /**
     * 不同类型的dataSource百分比
     * @return
     */
    public ServerResponse countByType() {
        Map<String, String> map = new HashMap<>();
        List<String> logs = new ArrayList<>();
        logs.add("EXEC");
        logs.add("SYSLOG");
        logs.add("TAILDIR");
        logs.add("NETCAT");
        List<String> dbs = new ArrayList<>();
        dbs.add("MYSQL");
        dbs.add("ORACLE");
        dbs.add("SQLSERVER");
        dbs.add("Pg");
        dbs.add("HBASE");
        int logNum = dataSourceListDao.countDataSourceListByDataSourceTypeIn(logs);
        int dbNum = dataSourceListDao.countDataSourceListByDataSourceTypeIn(dbs);
        int total = (int) dataSourceListDao.count();
        map.put("log", CharacterUtils.getPercentage(logNum, total));
        map.put("DB", CharacterUtils.getPercentage(dbNum, total));
        map.put("其他", CharacterUtils.getPercentage((total - logNum - dbNum), total));
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 获取不同类型的DcType
     * @param dcType
     * @return
     */
    public DcType getDcType(String dcType) {
        switch (dcType) {
            case "WEBSERVICE":
                return DcType.WEBSERVICE;
            case "EXEC":
                return DcType.EXEC;
            case "SYSLOG":
                return DcType.SYSLOG;
            case "NETCAT":
                return DcType.NETCAT;
            case "TAILDIR":
                return DcType.TAILDIR;
            case "HTTP":
                return DcType.HTTP;
            case "KAFKA":
                return DcType.KAFKA;
            default:
                return null;
        }
    }

    /**
     * check user inputInfo
     * @param dataSourceListAS
     * @return
     */
    public boolean checkInputInfo(DataSourceListAS dataSourceListAS){
        DataSourceList dataSourceList = dataSourceListAS.getDataSourceList();
        boolean b = true;
        //check dataSourceName
        int count1 = dataSourceListDao.countDataSourceListByDataSourceName(dataSourceList.getDataSourceName());
        if (count1 > 0) {
            b=false;
            return b;
        }
        return b;
    }

    /**
     * 获取当前登录用户的用户组下的所有用户
     * @return
     */
    public List<String> getLoginUsers() throws Exception {
        //获取当前登录用户组
        String groupName = SecUtils.findLoggedUserSession().getUserGroup();
        List<SysUser> users = userDao.getSysUserByUserGroup(groupName);
        List<String> userNames = new ArrayList<>();
        for (SysUser sysUser : users) {
            userNames.add(sysUser.getUsername());
        }
        return userNames;
    }

    /**
     * 获取多个filegroups值
     * @param lenth
     * @return
     */
    public List<String> getRandomNum(int lenth){
        int n = 0;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < lenth; i++) {
            n++;
            list.add("f"+n);
        }
        return list;
    }

    /**
     *
     */
    public void webSocket(){
        for (int i = 0; i < 50; ++i) {
            new Timer("timer - " + i).schedule(new TimerTask() {
                @Override
                public void run() {
                    JSONObject jsonObject  = new JSONObject();
                    jsonObject.put("flag","0");
                    jsonObject.put("message","数据源已停止");
                    template.convertAndSend(EWebSocketTopic.TABOO_DATASOURCE_STATE.getName(),jsonObject);
                }
            }, 1000);
        }
    }

    /**
     * @return
     */
    public Set<String> getGroups() {
        Set<String> userGroups = Sets.newHashSet();
        List<SysUserGroup> userGroupList = userGroupDao.findAll();
        userGroupList.forEach(sysUserGroup -> {
            userGroups.add(sysUserGroup.getGroupName());
        });
        return userGroups;
    }






    public static void main(String[] args) {
//        String adlPass = DESUtil.encrypt("Tp_nbrvwwp");
//        System.out.println(adlPass);

//        List<String> list = getRandomNum(5);
//        System.out.println(list);

        //代码中运行的结果尽然是"f2 ",奇葩
//        List<String> list = new ArrayList<>();
//        list.add("f1");
//        list.add("f2");
//        String str3 = "";
//        for (String string : list) {
//            str3 = str3+string + " ";
//        }
//        System.out.println(str3.trim());
        String url = getDbUrl("MYSQL","192.168.1.191:3308","sa-mgt");
        System.out.println(url);
//        JSONObject conf = getDbConf("admin","MYSQL",);
//        System.out.println(conf.toString());
    }


}
    