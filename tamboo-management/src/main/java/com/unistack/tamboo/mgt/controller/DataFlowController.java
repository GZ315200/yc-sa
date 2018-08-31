package com.unistack.tamboo.mgt.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.SchemaUtils;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.mgt.action.CalcAction;
import com.unistack.tamboo.mgt.action.DataDist2Action;
import com.unistack.tamboo.mgt.action.MqAction;
import com.unistack.tamboo.mgt.common.RestCode;
import com.unistack.tamboo.mgt.common.ServerResponse;
import com.unistack.tamboo.mgt.common.enums.CalcStatusType;
import com.unistack.tamboo.mgt.common.page.Pagination;
import com.unistack.tamboo.mgt.common.page.PaginationData;
import com.unistack.tamboo.mgt.model.CalcInfo;
import com.unistack.tamboo.mgt.model.DataFlowShow;
import com.unistack.tamboo.mgt.model.DataFlowSimp;
import com.unistack.tamboo.mgt.model.collect.DataFlowInfo;
import com.unistack.tamboo.mgt.model.collect.TopicGroup;
import com.unistack.tamboo.mgt.model.collect.TopicInfo;
import com.unistack.tamboo.mgt.model.dataFlow.CalcMode;
import com.unistack.tamboo.mgt.model.dataFlow.CalcNode;
import com.unistack.tamboo.mgt.model.dataFlow.PlatInfo;
import com.unistack.tamboo.mgt.model.monitor.MCollect;
import com.unistack.tamboo.mgt.model.monitor.MOffsetS;
import com.unistack.tamboo.mgt.model.monitor.MSparkMessage;
import com.unistack.tamboo.mgt.model.sys.SysUserGroup;
import com.unistack.tamboo.mgt.model.sys.UserSession;
import com.unistack.tamboo.mgt.service.collect.ResourceServiceImpl;
import com.unistack.tamboo.mgt.service.dataFlow.CalcService;
import com.unistack.tamboo.mgt.service.dataFlow.DataFlowService;
import com.unistack.tamboo.mgt.service.dataFlow.TopicGroupService;
import com.unistack.tamboo.mgt.service.dataFlow.TopicInfoService;
import com.unistack.tamboo.mgt.utils.DataFlowUtilSimp;
import com.unistack.tamboo.mgt.utils.DataFlowUtils;
import com.unistack.tamboo.mgt.utils.MonitorUtils;
import com.unistack.tamboo.mgt.utils.SecUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hero.li 数据流程管理 1：添加数据流。2.添加验证数据流。3.删除验证数据流。4。数据流列表。5：数据流基本信息统计
 */
@RestController
@RequestMapping("/df")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DataFlowController {

	public static Logger logger = LoggerFactory.getLogger(DataFlowController.class);

	@Autowired
	private DataFlowService dataFlowService;

	@Autowired
	private CalcService calcService;

	@Autowired
	private TopicInfoService topicInfoService;

	@Autowired
	private TopicGroupService topicGroupService;

	@Autowired
	private ResourceServiceImpl resourceService;

	@Transactional
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse<Object> addService(@RequestBody String conf, HttpSession session) {
		JSONObject json = JSON.parseObject(conf);
		UserSession userSession;
		try {
			userSession = SecUtils.findLoggedUserSession();
		} catch (Exception e) {
			return ServerResponse.createByErrorCodeAndMsg(RestCode.NEED_LOGIN.getStatus(),
					RestCode.NEED_LOGIN.getMsg());
		}

		String groupName = userSession.getUserGroup();
		int userId = userSession.getUserId().intValue();
		String taskName = json.getString("taskName");
		// 初始化验证过程
		SysUserGroup sysUserGroup = SecUtils.getUserGroupByUserGroup(groupName);

		DataFlowUtils dataFlowUtils = new DataFlowUtils(json, groupName, userId, taskName,
				sysUserGroup.getAclUsername(), sysUserGroup.getAclPassword());
		TopicInfo sourceTopic = topicInfoService.getByTopicId(dataFlowUtils.getSourceId());
		dataFlowUtils.setSourceTopic(sourceTopic);

		// 判断大的节点流转定义情况
		if (!dataFlowUtils.checkFlows()) {
			return ServerResponse.createByErrorMsg("check flow error");
		}
		// 判断小的节点内的配置是否正确
		if (!dataFlowUtils.checkNode()) {
			return ServerResponse.createByErrorMsg("check flow error");
		}

		// 获取dataflow流的一些信息
		DataFlowInfo dataFlowInfo = dataFlowUtils.getDataFlowInfo();

//        boolean isMoreResource=resourceService.isMoreResource(groupName,dataFlowInfo.getCalcCpu(),dataFlowInfo.getCalcMem(),dataFlowInfo.getTopicNum());
//        if(!isMoreResource){
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return ServerResponse.createByErrorMsg("no more resource remaining");
//        }

		// 保存流
		dataFlowInfo = dataFlowService.add(dataFlowInfo);

		dataFlowUtils.setWf_id(dataFlowInfo.getWfId());
		HashMap<String, TopicInfo> topicInfos = dataFlowUtils.getTopics();

		HashMap<String, Long> topic_name_ids = new HashMap<>();
		String topicDataSource = dataFlowUtils.getSourceTopic();
		for (String key : topicInfos.keySet()) {
			if (topicDataSource.equalsIgnoreCase(key)) {
				continue;
			}
			TopicInfo topicInfo = topicInfos.get(key);
			topicInfo.setTopicType(2);
			TopicInfo topicInfoNew = topicInfoService.add(topicInfo);
			topicInfos.put(key, topicInfoNew);
			topic_name_ids.put(topicInfoNew.getTopicName(), topicInfoNew.getTopicId());
			// 执行创建命令
			// 创建对象是用的是INTEGER 暂时为了不导致冲突强制转化
			short rep = (short) ((int) topicInfo.getTopicReplication());
			boolean isCreate = MqAction.mQcreateTopic(topicInfo.getTopicName(), topicInfo.getTopicPartition(), rep,
					topicInfo.getTopicAclUsername(), topicInfo.getTopicAclPassword(), false);
			if (!isCreate) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return ServerResponse.createByErrorMsg("create mq error");
			}
		}

		TopicInfo topicInfoNew = topicInfoService.getByTopicId(dataFlowUtils.getSourceId());
		dataFlowUtils.setSourceTopic(topicInfoNew);

		HashMap<String, CalcNode> calcInfoHashMap = dataFlowUtils.getCalcInfos(topicInfos);

		HashMap<String, CalcInfo> calcInfoNews = new HashMap<>();

		for (CalcNode calcNode : calcInfoHashMap.values()) {
			CalcInfo calcInfo = new CalcInfo();
//            calcInfo.setCalcConf(calcNode.getConf());
			calcInfo.setCalcType(calcNode.getType());
			calcInfo.setCpuNum(calcNode.getCpu());
			calcInfo.setCreateTime(new Date());
			calcInfo.setFlag(0);
			calcInfo.setCalcId(getCalcId(calcNode.getTopic_from().getTopicName(), calcNode.getType()));
			calcInfo.setWfId(dataFlowInfo.getWfId());
			calcInfo.setTopicIdFrom((int) calcNode.getTopic_from().getTopicId());
			calcInfo.setTopicIdTo((int) calcNode.getTopic_to().getTopicId() == 0
					? topic_name_ids.get(calcNode.getTopic_to().getTopicName()).intValue()
					: (int) calcNode.getTopic_to().getTopicId());
			calcInfo.setTopicFromGroup(
					dataFlowUtils.getGroup(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())));

			// 执行启动命令
			CalcMode calcMode = new CalcMode();
			calcMode.setType(calcNode.getType());
//            calcMode.setTemplate(calcNode.getConf());
			calcMode.setAppName(calcInfo.getCalcId());
			JSONObject kafka = new JSONObject();
//            {"type":"clean","template":{"filter":[{"type":"Rename","fields":[{"old":"A","new":"A1"},{"old":"B","new":"B1"}]}]},"to_topic_acl_username":"admin","to_topic_acl_password"
// "kafka":{"from_topic":"t2","to_topic":"t3","group_id":"test202","batchInterval":"2500","auto_offset_reset":"earliest","acl_username":"admin","acl_password":"admin123"}}

			kafka.put("from_topic", calcNode.getTopic_from().getTopicName());
			kafka.put("to_topic", calcNode.getTopic_to().getTopicName());
			kafka.put("group_id", calcInfo.getTopicFromGroup());
			kafka.put("acl_username", sysUserGroup.getAclUsername());
			kafka.put("acl_password", sysUserGroup.getAclPassword());
			kafka.put("alias", dataFlowUtils.getDataSourceName());

			JSONObject calc = (JSONObject) JSON.toJSON(calcMode);
			calc.put("kafka", kafka);
			calc.put("queue", TambooConfig.SYSTEM_QUEUE + "." + groupName);
			JSONObject template = JSON.parseObject(calcNode.getConf());
			JSONArray streamTables = template.getJSONArray("streamTables");

			/**
			 * 优化方式:当前端只有一个数据流表的时候就不应该在streamTables对应的数组中放一个空对象
			 * 这儿应该有前端来修改，我这儿用flag标识一下看是否是空对象，这么做主要是因为我不会改前端代码
			 */
			int flag = -1;
			if (streamTables != null && !streamTables.isEmpty()) {
				for (Object stream : streamTables) {
					JSONObject fl = (JSONObject) stream;
					if (0 == fl.size()) {
						continue;
					}
					flag++;
					String table = fl.getString("streamTable");
					TopicInfo topicInfo = topicInfoService.queryTopicByDataSourceName(table);
					if (null == topicInfo) {
						return ServerResponse.createByErrorMsg("没有该[" + table + "]的信息");
					}
					fl.put("topicName", topicInfo.getTopicName());
				}
			}
			if (flag == -1) {
				template.put("streamTables", new JSONArray());
			} else {
				template.put("streamTables", streamTables);
			}

			calc.put("template", template);

			System.out.println(calc.toJSONString());
			JSONObject object = CalcAction.startCalc(calc.toJSONString());
			calcInfo.setCalcConf(calc.toJSONString());
			if (object.getString("code").equalsIgnoreCase("200")) {
				calcInfo.setFlag(1);
			} else {
				calcInfo.setFlag(-1);
				dataFlowService.changeDataFlowStatus(dataFlowInfo.getWfId(), -1);
			}
//            calcInfo.setCalcId();
<<<<<<< HEAD
			calcInfoNews.put(calcNode.getNode(), calcService.add(calcInfo));
		}
		JSONObject jsonAll = dataFlowUtils.setCalcIdsAndGetJsonAll(calcInfoNews);
		dataFlowInfo.setConf(jsonAll.toJSONString());
		dataFlowService.upDataConf(dataFlowInfo.getWfId(), jsonAll.toJSONString());
		// 添加topic 开放权限
		HashMap<Integer, Object[]> topics_open = dataFlowUtils.getOpenTopic();
		for (int topic_id : topics_open.keySet()) {
			Object[] groups = topics_open.get(topic_id);
			if (groups.length > 0) {
				for (Object g : groups) {
					TopicGroup topicGroup = new TopicGroup();
					topicGroup.setGroupName((String) g);
					topicGroup.setTopicId((long) topic_id);
					topicGroup.setUpdateTime(new Date());
					topicGroupService.save(topicGroup);
				}
			}
		}

		// 下发数据处理
		HashMap<String, JSONObject> dist = dataFlowUtils.getDist();
		for (JSONObject jsonDist : dist.values()) {
			System.out.println(jsonDist);
			JSONObject resDist = DataDist2Action.startKafkaSink(jsonDist);
			System.out.println(resDist);
		}
		return ServerResponse.createBySuccess();
	}

	@Transactional
	@RequestMapping(value = "/add2", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse<Object> add(@RequestBody String conf, HttpSession session) {

		JSONObject json = JSON.parseObject(conf);
		UserSession userSession = null;
		try {
			userSession = SecUtils.findLoggedUserSession();
		} catch (Exception e) {
			logger.error("", e);
			return ServerResponse.createByErrorCodeAndMsg(RestCode.NEED_LOGIN.getStatus(),
					RestCode.NEED_LOGIN.getMsg());
		}
		String groupName = userSession == null ? "userGroup" : userSession.getUserGroup();
		int userId = userSession == null ? 1 : userSession.getUserId().intValue();
		String name = json.getString("taskName");
		// 初始化验证过程
		SysUserGroup sysUserGroup = SecUtils.getUserGroupByUserGroup(groupName);

		DataFlowUtilSimp dataFlowUtilSimp = new DataFlowUtilSimp(json, groupName, userId, name,
				sysUserGroup.getAclUsername(), sysUserGroup.getAclPassword());
		TopicInfo sourceTopic = topicInfoService.getByTopicId(dataFlowUtilSimp.getSourceId());
		dataFlowUtilSimp.setSourceTopic(sourceTopic);

		// 判断大的节点流转定义情况
		if (!dataFlowUtilSimp.checkFlows()) {
			return ServerResponse.createByErrorMsg("check flow error");
		}
		// 判断小的节点内的配置是否正确
		if (!dataFlowUtilSimp.checkNode()) {
			return ServerResponse.createByErrorMsg("check flow error");
		}

		// 获取dataflow流的一些信息
		DataFlowInfo dataFlowInfo = dataFlowUtilSimp.getDataFlowInfo();

		// 保存流
		dataFlowInfo = dataFlowService.add(dataFlowInfo);

		dataFlowUtilSimp.setWf_id(dataFlowInfo.getWfId());
		HashMap<String, TopicInfo> topicInfos = dataFlowUtilSimp.getTopics();

		HashMap<String, Long> topic_name_ids = new HashMap<>();
		String topicDataSource = dataFlowUtilSimp.getSourceTopic();
		for (String key : topicInfos.keySet()) {
			if (topicDataSource.equalsIgnoreCase(key)) {
				continue;
			}
			TopicInfo topicInfo = topicInfos.get(key);
			topicInfo.setTopicType(2);
			TopicInfo topicInfoNew = topicInfoService.add(topicInfo);
			topicInfos.put(key, topicInfoNew);
			topic_name_ids.put(topicInfoNew.getTopicName(), topicInfoNew.getTopicId());
			// 执行创建命令
			// 创建对象是用的是INTEGER 暂时为了不导致冲突强制转化
			short rep = (short) ((int) topicInfo.getTopicReplication());
			boolean isCreate = MqAction.mQcreateTopic(topicInfo.getTopicName(), topicInfo.getTopicPartition(), rep,
					topicInfo.getTopicAclUsername(), topicInfo.getTopicAclPassword(), false);
			if (!isCreate) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return ServerResponse.createByErrorMsg("create mq error");
			}
		}
		TopicInfo topicInfoNew = topicInfoService.getByTopicId(dataFlowUtilSimp.getSourceId());
		dataFlowUtilSimp.setSourceTopic(topicInfoNew);

		HashMap<String, CalcNode> calcInfoHashMap = dataFlowUtilSimp.getCalcInfos(topicInfos);

		for (CalcNode calcNode : calcInfoHashMap.values()) {

			CalcInfo calcInfo = new CalcInfo();
=======
            calcInfoNews.put(calcNode.getNode(),calcService.add(calcInfo));
        }
        JSONObject jsonAll=dataFlowUtils.setCalcIdsAndGetJsonAll(calcInfoNews);
        dataFlowInfo.setConf(jsonAll.toJSONString());
        dataFlowService.upDataConf(dataFlowInfo.getWfId(),jsonAll.toJSONString());
        //添加topic 开放权限
        HashMap<Integer, Object[]> topics_open = dataFlowUtils.getOpenTopic();
        for (int topic_id : topics_open.keySet()) {
            Object[] groups = topics_open.get(topic_id);
            if (groups.length > 0) {
                for (Object g : groups) {
                    TopicGroup topicGroup = new TopicGroup();
                    topicGroup.setGroupName((String) g);
                    topicGroup.setTopicId((long) topic_id);
                    topicGroup.setUpdateTime(new Date());
                    topicGroupService.save(topicGroup);
                }
            }
        }


        //下发数据处理
        HashMap<String, JSONObject> dist = dataFlowUtils.getDist();
        for (JSONObject jsonDist : dist.values()){
            System.out.println(jsonDist);
            JSONObject resDist = DataDist2Action.startKafkaSink(jsonDist);
            System.out.println("下发处理结果:"+resDist);
        }
        return ServerResponse.createBySuccess();
    }



    @Transactional
    @RequestMapping(value = "/add2", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse<Object> add(@RequestBody String conf, HttpSession session) {

        JSONObject json = JSONObject.parseObject(conf);
        UserSession userSession = null;
        try {
            userSession = SecUtils.findLoggedUserSession();
        } catch (Exception e) {
            logger.error("", e);
            return ServerResponse.createByErrorCodeAndMsg(RestCode.NEED_LOGIN.getStatus(), RestCode.NEED_LOGIN.getMsg());
        }
        String groupName = userSession == null ? "userGroup" : userSession.getUserGroup();
        int userId = userSession == null ? 1 : userSession.getUserId().intValue();
        String name = json.getString("taskName");
        //初始化验证过程
        SysUserGroup sysUserGroup = SecUtils.getUserGroupByUserGroup(groupName);


        DataFlowUtilSimp dataFlowUtilSimp = new DataFlowUtilSimp(json, groupName, userId, name, sysUserGroup.getAclUsername(), sysUserGroup.getAclPassword());
        TopicInfo sourceTopic = topicInfoService.getByTopicId(dataFlowUtilSimp.getSourceId());
        dataFlowUtilSimp.setSourceTopic(sourceTopic);

        //判断大的节点流转定义情况
        if (!dataFlowUtilSimp.checkFlows()) {
            return ServerResponse.createByErrorMsg("check flow error");
        }
        //判断小的节点内的配置是否正确
        if (!dataFlowUtilSimp.checkNode()) {
            return ServerResponse.createByErrorMsg("check flow error");
        }

        //获取dataflow流的一些信息
        DataFlowInfo dataFlowInfo = dataFlowUtilSimp.getDataFlowInfo();




        //保存流
        dataFlowInfo = dataFlowService.add(dataFlowInfo);

        dataFlowUtilSimp.setWf_id(dataFlowInfo.getWfId());
        HashMap<String, TopicInfo> topicInfos = dataFlowUtilSimp.getTopics();

        HashMap<String, Long> topic_name_ids = new HashMap<>();
        String topicDataSource = dataFlowUtilSimp.getSourceTopic();
        for (String key : topicInfos.keySet()) {
            if (topicDataSource.equalsIgnoreCase(key)) {
                continue;
            }
            TopicInfo topicInfo = topicInfos.get(key);
            topicInfo.setTopicType(2);
            TopicInfo topicInfoNew = topicInfoService.add(topicInfo);
            topicInfos.put(key, topicInfoNew);
            topic_name_ids.put(topicInfoNew.getTopicName(), topicInfoNew.getTopicId());
            //执行创建命令
            //创建对象是用的是INTEGER 暂时为了不导致冲突强制转化
            short rep = (short) ((int) topicInfo.getTopicReplication());
            boolean isCreate = MqAction.mQcreateTopic(topicInfo.getTopicName(), topicInfo.getTopicPartition(), rep, topicInfo.getTopicAclUsername(), topicInfo.getTopicAclPassword(), false);
            if (!isCreate) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ServerResponse.createByErrorMsg("create mq error");
            }
        }
        TopicInfo topicInfoNew = topicInfoService.getByTopicId(dataFlowUtilSimp.getSourceId());
        dataFlowUtilSimp.setSourceTopic(topicInfoNew);

        HashMap<String, CalcNode> calcInfoHashMap = dataFlowUtilSimp.getCalcInfos(topicInfos);


        for (CalcNode calcNode : calcInfoHashMap.values()) {

            CalcInfo calcInfo = new CalcInfo();
>>>>>>> fca409511ef0ba0a271a9da485b7804a2f6bfb60
//            calcInfo.setCalcConf(calcNode.getConf());
			calcInfo.setCalcType(calcNode.getType());
			calcInfo.setCpuNum(calcNode.getCpu());
			calcInfo.setCreateTime(new Date());
			calcInfo.setFlag(0);
			calcInfo.setCalcId(getCalcId(calcNode.getTopic_from().getTopicName(), calcNode.getType()));
			calcInfo.setWfId(dataFlowInfo.getWfId());
			calcInfo.setTopicIdFrom((int) calcNode.getTopic_from().getTopicId());
			calcInfo.setTopicIdTo((int) calcNode.getTopic_to().getTopicId() == 0
					? topic_name_ids.get(calcNode.getTopic_to().getTopicName()).intValue()
					: (int) calcNode.getTopic_to().getTopicId());
			calcInfo.setTopicFromGroup(
					dataFlowUtilSimp.getTopicName(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())));

			// 执行启动命令
			CalcMode calcMode = new CalcMode();
			calcMode.setType(calcNode.getType());
//            calcMode.setTemplate(calcNode.getConf());
			calcMode.setAppName(calcInfo.getCalcId());
			JSONObject jsonObject = new JSONObject();
//            {"type":"clean","template":{"filter":[{"type":"Rename","fields":[{"old":"A","new":"A1"},{"old":"B","new":"B1"}]}]},"to_topic_acl_username":"admin","to_topic_acl_password"
// "kafka":{"from_topic":"t2","to_topic":"t3","group_id":"test202","batchInterval":"2500","auto_offset_reset":"earliest","acl_username":"admin","acl_password":"admin123"}}

			jsonObject.put("from_topic", calcNode.getTopic_from().getTopicName());
			jsonObject.put("to_topic", calcNode.getTopic_to().getTopicName());
			jsonObject.put("group_id", calcInfo.getTopicFromGroup());
			jsonObject.put("acl_username", sysUserGroup.getAclUsername());
			jsonObject.put("acl_password", sysUserGroup.getAclPassword());

//            calcMode.setKafka(jsonObject.toJSONString());

			JSONObject calc = (JSONObject) JSON.toJSON(calcMode);
			calc.put("kafka", jsonObject);
			calc.put("template", JSON.parseArray(calcNode.getConf()));

			System.out.println(calc);
			JSONObject object = CalcAction.startCalc(calc.toJSONString());
			calcInfo.setCalcConf(calc.toJSONString());
			if (object.getString("code").equalsIgnoreCase("200")) {
				calcInfo.setFlag(1);
			} else {
				calcInfo.setFlag(-1);
				dataFlowService.changeDataFlowStatus(dataFlowInfo.getWfId(), -1);
			}
			calcService.add(calcInfo);
		}
		// 添加topic 开放权限
		HashMap<Integer, Object[]> topics_open = dataFlowUtilSimp.getOpenTopic();
		for (int topic_id : topics_open.keySet()) {
			Object[] groups = topics_open.get(topic_id);
			if (groups.length > 0) {
				for (Object g : groups) {
					TopicGroup topicGroup = new TopicGroup();
					topicGroup.setGroupName((String) g);
					topicGroup.setTopicId((long) topic_id);
					topicGroup.setUpdateTime(new Date());
					topicGroupService.save(topicGroup);
				}
			}
		}

		// 下发数据处理
		HashMap<String, JSONObject> dist = dataFlowUtilSimp.getDist();
		for (JSONObject jsonDist : dist.values()) {
			System.out.println(jsonDist);
			JSONObject resDist = DataDist2Action.startKafkaSink(jsonDist);
			System.out.println(resDist);
		}
		return ServerResponse.createBySuccess();
	}

	private String getCalcId(String topicName, int type) {
		return new StringBuilder("calc").append("_").append(topicName).append("_").append(type).append("_")
				.append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())).toString();
	}

	@RequestMapping(value = "/check", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse<Object> check(@RequestParam String conf, @RequestParam String message, HttpSession session) {
		JSONObject json = JSON.parseObject(conf);
		UserSession userSession = null;
		try {
			userSession = SecUtils.findLoggedUserSession();
		} catch (Exception e) {
			logger.error("", e);
			return ServerResponse.createByErrorCodeAndMsg(RestCode.NEED_LOGIN.getStatus(),
					RestCode.NEED_LOGIN.getMsg());
		}
		SysUserGroup sysUserGroup = SecUtils.getUserGroupByUserGroup(userSession.getUserGroup());
		String name = json.getString("name");
		// 初始化验证过程
		DataFlowUtilSimp dataFlowUtilSimp = new DataFlowUtilSimp(json, userSession.getUserGroup(),
				userSession.getUserId().intValue(), name, sysUserGroup.getAclUsername(), sysUserGroup.getAclPassword());

		if (!dataFlowUtilSimp.checkFlows()) {
			return ServerResponse.createByErrorMsg("flows check false");
		}
//        if (!dataFlowUtilSimp.checkCalcs(com.unistack.tamboo.commons.json, message)) {
//            return ServerResponse.createByErrorMsg("calcs check false");
//        }
		return ServerResponse.createBySuccess();
	}

	@Transactional
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	public ServerResponse del(@RequestBody String conf, HttpSession session) {
		JSONObject json = JSON.parseObject(conf);
		int wf_id = json.getInteger("wf_id");
		DataFlowInfo dataFlowInfo = dataFlowService.queryByWfId(wf_id);
		if (dataFlowInfo == null) {
			return ServerResponse.createByErrorMsg("no dataflow id for " + wf_id);
		}

		dataFlowService.delete(wf_id);
		ArrayList<CalcInfo> calcInfos = calcService.queryByWfId(wf_id);
		calcService.delByWfId(wf_id);

		ArrayList<TopicInfo> topicInfos = new ArrayList<>();
		ArrayList<Long> topic_ids = new ArrayList<>();
		for (CalcInfo calcInfo : calcInfos) {
			TopicInfo topicInfo = topicInfoService.getByTopicId(calcInfo.getTopicIdTo());
			String topicName = topicInfo.getTopicName();
			topicInfo.setTopicName(topicName);
			topicInfos.add(topicInfo);
			topic_ids.add(topicInfo.getTopicId());
			MqAction.mQdelTopic(topicName);

		}
		topicInfoService.deleteByTopicNames(topicInfos);
//        topicInfoService.deleteByTopicIds((Long[])topic_ids.toArray());

		return ServerResponse.createBySuccess();
	}

	/**
	 * 用于计算服务的开启/停止
	 * 
	 * @param data {"wf_id":700,"action":"stop/start"}
	 * @return
	 */
	@RequestMapping(value = "/switch", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse stop(@RequestBody(required = false) String data) {
		JSONObject json = JSON.parseObject(data);
		int wf_id = json.getInteger("wf_id");
		String action = json.getString("action");

		if ("stop".equalsIgnoreCase(action)) {
			ArrayList<CalcInfo> calcInfos = calcService.updateByWfId(wf_id, CalcStatusType.STOP.getType());
			for (CalcInfo calcInfo : calcInfos) {
				CalcAction.stopCalc(calcInfo.getCalcConf());
			}
			dataFlowService.changeDataFlowStatus(wf_id, CalcStatusType.STOP.getType());
			DataDist2Action.stopKafkaSink(wf_id);
			return ServerResponse.createBySuccess();
		}

		if ("start".equalsIgnoreCase(action)) {
			ArrayList<CalcInfo> calcInfos = calcService.updateByWfId(wf_id, CalcStatusType.START.getType());
			for (CalcInfo calcInfo : calcInfos) {
				CalcAction.startCalc(calcInfo.getCalcConf());
			}
			dataFlowService.changeDataFlowStatus(wf_id, CalcStatusType.START.getType());
			DataDist2Action.startKafkaSink(wf_id);
			return ServerResponse.createBySuccess();
		}

		return ServerResponse.createByErrorMsg("ERROR ACTION");
	}

	@RequestMapping(value = "/list", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse<PaginationData<DataFlowShow>> list(@RequestBody(required = false) String conf,
			HttpSession session) {
		JSONObject json = JSON.parseObject(conf);
		UserSession userSession = null;
		try {
			userSession = SecUtils.findLoggedUserSession();
		} catch (Exception e) {
			logger.error("", e);
			return ServerResponse.createByErrorCodeAndMsg(RestCode.NEED_LOGIN.getStatus(),
					RestCode.NEED_LOGIN.getMsg());
		}
		int userId = userSession == null ? 1 : userSession.getUserId().intValue();
		int pageIndex = json.getInteger("pageIndex") == null ? 1 : json.getInteger("pageIndex");
		int pageSize = json.getInteger("pageSize") == null ? 6 : json.getInteger("pageSize");
		Pageable pageable = new PageRequest(pageIndex - 1, pageSize, null);
		Page<DataFlowInfo> dataFlows = dataFlowService.listByUser_id(userId, json.getString("query"), pageable);
		List<DataFlowShow> dataFlowShows = new ArrayList<>();
		for (DataFlowInfo dataFlowInfo : dataFlows) {
			DataFlowShow dataFlowShow = new DataFlowShow();
			int wf_id = dataFlowInfo.getWfId();
			ArrayList<CalcInfo> caclInfos = calcService.queryByWfId(wf_id);
			HashSet<String> appName = new HashSet<>();
			caclInfos.forEach(calcInfo -> appName.add(calcInfo.getCalcId()));

//            private String appId;
			Long totalCalSize = 0L;
//            private Long peakCalSize;
//            private Date peakTime;
			Long latency = 0L;
			Double calRate = 0.0;
//            private Date timestamp;

			for (Object app : appName.toArray()) {
				MSparkMessage msg = MonitorUtils.getStreamingMonitorRecord(String.valueOf(app));
				if (msg != null) {
					totalCalSize += msg.getTotalCalSize();
					latency += msg.getLatency();
					calRate += msg.getCalRate();
				}
			}
			dataFlowShow.setFlag(dataFlowInfo.getFlag());
			dataFlowShow.setLatency(appName.size() > 0 ? latency.intValue() / appName.size() : 0);
			dataFlowShow.setRate(appName.size() > 0 ? calRate.intValue() / appName.size() : 0);
			dataFlowShow.setTotalRecords(appName.size() > 0 ? totalCalSize.intValue() / appName.size() : 0);
			dataFlowShow.setWf_id(wf_id);
			dataFlowShow.setWf_name(dataFlowInfo.getWfName());
			dataFlowShow.setWorkers(calcService.countByWfId(dataFlowInfo.getWfId()));
			dataFlowShows.add(dataFlowShow);
		}
		PaginationData<DataFlowShow> pageDataFlows = new PaginationData<>();
		pageDataFlows.setPageData(dataFlowShows);
		Pagination pagination = new Pagination();
		pagination.setPageIndex(pageIndex);
		pagination.setPageSize(pageSize);
		pagination.setResultSize(dataFlows.getTotalElements());
		pagination.setTotalPage(dataFlows.getTotalPages());
		pageDataFlows.setPagination(pagination);
		return ServerResponse.createBySuccess(pageDataFlows);
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET, produces = "application/json")
	public ServerResponse<ArrayList<DataFlowInfo>> count(HttpSession session) {
		ArrayList<DataFlowInfo> dataFlows = dataFlowService
				.listByUser_id((session.getAttribute("userId") == null ? 0 : (int) session.getAttribute("userId")));

		return ServerResponse.createBySuccess(dataFlows);
	}

	@RequestMapping(value = "/platform", method = RequestMethod.GET, produces = "application/json")
	public ServerResponse<PlatInfo> platinfo(HttpSession session) {
		Object[] dataFlowMaps = dataFlowService.countServices();
		PlatInfo dataFlows = getPlatInfos(dataFlowMaps);
		return ServerResponse.createBySuccess(dataFlows);
	}

	private PlatInfo getPlatInfos(Object[] dataFlowMaps) {
		Object[] dataFlowMap = (Object[]) dataFlowMaps[0];

		PlatInfo platInfo = new PlatInfo();

		platInfo.setCountWf(Long.valueOf(String.valueOf(dataFlowMap[0])).intValue());
		platInfo.setRunning(
				Long.valueOf(String.valueOf(dataFlowMap[1]).equals("null") ? "0" : String.valueOf(dataFlowMap[1]))
						.intValue());
		platInfo.setUnrunning(
				Long.valueOf(String.valueOf(dataFlowMap[2]).equals("null") ? "0" : String.valueOf(dataFlowMap[2]))
						.intValue());
		platInfo.setWarning(
				Long.valueOf(String.valueOf(dataFlowMap[3]).equals("null") ? "0" : String.valueOf(dataFlowMap[3]))
						.intValue());
		return platInfo;
	}

	@RequestMapping(value = "/query", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse<DataFlowSimp> query(@RequestBody String conf, HttpSession session) {
		JSONObject json = JSON.parseObject(conf);
		DataFlowSimp dataFlowSimp = new DataFlowSimp();
		if (json.size() > 0) {
			int wf_id = json.getInteger("wf_id");
			DataFlowInfo dataFlowInfo = dataFlowService.queryByWfId(wf_id);
			if (dataFlowInfo == null) {
				return ServerResponse.createByErrorMsg("no dataflow id for " + wf_id);
			}
			int sourceId = dataFlowInfo.getDataSourceId();
			MCollect mCollect = MonitorUtils.getSourceCollectRecordByTopicId(dataFlowInfo.getDataSourceId());
			if (mCollect != null) {
				dataFlowSimp.setCountRecords(mCollect.getTotalColSize());
			} else {
				dataFlowSimp.setCountRecords(0l);
			}

			ArrayList<CalcInfo> calcInfos = calcService.queryByWfId(wf_id);
			int numCalcs = calcInfos.size();
			int times = 0;
			int warning = 0;
			for (CalcInfo calcInfo : calcInfos) {
//	            times+=calcInfo.getData_wf();
				warning += calcInfo.getFlag() == -1 ? 1 : 0;
			}
//	        dataFlowSimp.setCollector();
//	        dataFlowSimp.set();
			dataFlowSimp.setWf_id(wf_id);
			dataFlowSimp.setLatency(numCalcs == 0 ? 0 : times / numCalcs);
			dataFlowSimp.setWarning(warning);
			dataFlowSimp.setConf(dataFlowInfo.getConf());
			// 添加监控流信息
			dataFlowSimp.setDistCount(0);
		}

//        offsetCollectorService.getLastOffset((long)sourceId);
		return ServerResponse.createBySuccess(dataFlowSimp);
	}

	@RequestMapping(value = "/queryCharts", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse<List<MOffsetS>> queryCharts(@RequestBody String pare, HttpSession session) {
		JSONObject json = JSON.parseObject(pare);
		DataFlowInfo dataFlowInfo = null;
		if (json.size() > 0) {
			int wf_id = json.getInteger("wf_id");
			dataFlowInfo = dataFlowService.queryByWfId(wf_id);
			return ServerResponse.createBySuccess(MonitorUtils.getThirtyMinMonitors(dataFlowInfo.getDataSourceId()));
		}
		return ServerResponse.createByError();
	}

	@RequestMapping(value = "/queryNode", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse<String> queryNode(@RequestBody String pare, HttpSession session) {
		JSONObject json = JSON.parseObject(pare);
		int wf_id = json.getInteger("wf_id");
		int data_wf = json.getInteger("data_wf");
		String topicName = json.getString("topicName");
		JSONObject jsonObject = new JSONObject();
		CalcInfo calcInfo = calcService.queryByDataWf(data_wf);

		JSONObject calcConfJson = JSON.parseObject(calcInfo.getCalcConf());
		JSONObject calcInfoJson = new JSONObject();
		if (calcInfo.getCalcType() == 11) {
			calcInfoJson.put("calcNum", calcConfJson.getJSONObject("template").getJSONArray("calcInfoList").size());
		} else if (calcInfo.getCalcType() == 12) {

		} else if (calcInfo.getCalcType() == 13) {

		} else if (calcInfo.getCalcType() == 14) {
			calcInfoJson.put("calcNum", calcConfJson.getJSONObject("template").getJSONArray("datasources").size());
		}

		calcInfoJson.put("calcType", calcInfo.getCalcType());
		calcInfoJson.put("appName", calcInfo.getCalcId());
		calcInfoJson.put("topicFrom", calcConfJson.getJSONObject("kafka").getString("from_topic"));
		calcInfoJson.put("topicTo", calcConfJson.getJSONObject("kafka").getString("to_topic"));
		calcInfoJson.put("status", calcInfo.getFlag());
		jsonObject.put("calcConf", calcInfoJson);

		MSparkMessage msg = MonitorUtils.getStreamingMonitorRecord(String.valueOf(calcInfo.getCalcId()));
		JSONObject msgJson = JSON.parseObject(JSON.toJSONString(msg));
		jsonObject.put("sparkMsg", msgJson);

		JSONObject schemas = SchemaUtils.getLastVersionBySubject(topicName + "-value");
		if (schemas.getBoolean("succeed")) {
			jsonObject.put("schema", schemas.getString("msg"));
		} else {
			jsonObject.put("schema", null);
		}

		return ServerResponse.createBySuccess(jsonObject.toJSONString());

	}

	@RequestMapping(value = "/switchCalc", method = RequestMethod.POST, produces = "application/json")
	public ServerResponse<String> startCalc(@RequestBody String pare, HttpSession session) {
		JSONObject json = JSON.parseObject(pare);
		int data_wf = json.getInteger("data_wf");
		String action = json.getString("action");
		if (action.equalsIgnoreCase("start")) {
			CalcInfo calcInfo = calcService.updateByDataWf(data_wf, CalcStatusType.START.getType());

			CalcAction.startCalc(calcInfo.getCalcConf());
		} else if (action.equalsIgnoreCase("stop")) {
			CalcInfo calcInfo = calcService.updateByDataWf(data_wf, CalcStatusType.STOP.getType());

			CalcAction.stopCalc(calcInfo.getCalcConf());
		}
		return ServerResponse.createBySuccess();

	}

	public static DataFlowShow invoke(DataFlowSimp dataFlow) {

		return new DataFlowShow();
	}

}
