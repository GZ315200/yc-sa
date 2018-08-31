package com.unistack.tamboo.mgt.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.unistack.tamboo.commons.utils.CommonUtils;
import com.unistack.tamboo.commons.utils.JsonUtils;
import com.unistack.tamboo.commons.utils.OkHttpResponse;
import com.unistack.tamboo.commons.utils.OkHttpUtils;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.commons.utils.enums.EDatabaseType;
import com.unistack.tamboo.mgt.dao.monitor.MCalcWorkerInfoDao;
import com.unistack.tamboo.mgt.helper.TopicHelper;
import com.unistack.tamboo.mgt.model.monitor.CalcServiceInfoVo;
import com.unistack.tamboo.mgt.model.monitor.MCaclWorkersInfo;
import com.unistack.tamboo.mgt.service.BaseService;
import com.unistack.tamboo.mgt.utils.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/7/27
 */

@Service
public class CalcServiceInfoSaving extends BaseService {

	public static Logger logger = LoggerFactory.getLogger(CalcServiceInfoSaving.class);

	private static String PORT = TambooConfig.CALC_MONITOR_PORT;

	@Autowired
	private TopicHelper topicHelper;

	@Autowired
	private MCalcWorkerInfoDao mCalcWorkerInfoDao;

	public CalcServiceInfoVo savingCalcWorkersInfo(long timestamp) {

		String url = "http://" + topicHelper.sparkServers() + ":" + PORT + "/json";
		List<MCaclWorkersInfo> workerInfos = Lists.newArrayList();

		CalcServiceInfoVo csv = new CalcServiceInfoVo();
		String json = null;
		OkHttpResponse response = OkHttpUtils.get(url);

		if (response.getCode() == 200) {
			json = response.getBody();
			JSONObject data = JSON.parseObject(json);
			JSONArray array = data.getJSONArray("workers");

			for (int i = 0; i < array.size(); i++) {
				MCaclWorkersInfo workerInfo = new MCaclWorkersInfo();
				JSONObject o = array.getJSONObject(i);
				String id = o.getString("id");
				String host = o.getString("host");
				String port = o.getString("port");
				String cores = o.getString("cores");
				String coresused = o.getString("coresused");
				String coresfree = o.getString("coresfree");
				String memory = o.getString("memory");
				String memoryuse = o.getString("memoryused");
				String memoryfree = o.getString("memoryfree");
				String state = o.getString("state");
				String lastheartbeat = o.getString("lastheartbeat");
				String heartbeat = CommonUtils.formatLong(CommonUtils.formatString(lastheartbeat) / 1000);
				workerInfo.setWorkerId(id);
				workerInfo.setHost(host);
				workerInfo.setPort(port);
				workerInfo.setCores(cores);
				workerInfo.setCoresUsed(coresused);
				workerInfo.setCoresFree(coresfree);
				workerInfo.setMemory(memory);
				workerInfo.setMemoryUse(memoryuse);
				workerInfo.setMemoryFree(memoryfree);
				workerInfo.setState(state);
				workerInfo.setLastHeartbeat(heartbeat);
				workerInfos.add(workerInfo);
			}

			mCalcWorkerInfoDao.deleteAll();
			mCalcWorkerInfoDao.save(workerInfos);

			String aliveWorkers = data.getString("aliveworkers");
			String cores = data.getString("cores");
			String coresUsed = data.getString("coresused");
			String memory = data.getString("memory");
			String memoryUsed = data.getString("memoryused");
			String activeApps = "";
			String completedApps = "";
			int activeAppsSize = data.getJSONArray("activeapps").size();

			if (activeAppsSize == 0) {
				activeApps = "0";
			}
			int completedAppsSize = data.getJSONArray("completedapps").size();
			if (completedAppsSize == 0) {
				completedApps = "0";
			}
			String status = data.getString("status");
			csv.setAliveworkers(aliveWorkers);
			csv.setCores(cores);
			csv.setCoresused(coresUsed);
			csv.setMemory(memory);
			csv.setMemoryused(memoryUsed);
			csv.setActiveapps(activeApps);
			csv.setCompletedapps(completedApps);
			csv.setActiveAppsSize(String.valueOf(activeAppsSize));
			csv.setCompletedAppsSize(String.valueOf(completedAppsSize));
//            csv.set(activeAppsSize);
			csv.setStatus(status);
			csv.setTimestamp(CommonUtils.formatLong(timestamp));
			RedisClient.set(CALC_SERVICE_INFO.getBytes(), JsonUtils.toByteArray(csv), EDatabaseType.SPARK.getCode());
			csv.setCaclWorkersInfoList(workerInfos);
			return csv;
		} else {
			return null;
		}
	}

}
