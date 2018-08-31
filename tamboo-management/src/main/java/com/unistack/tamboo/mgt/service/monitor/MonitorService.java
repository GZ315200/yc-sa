package com.unistack.tamboo.mgt.service.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.mgt.common.page.PageConvert;
import com.unistack.tamboo.mgt.common.page.PaginationData;
import com.unistack.tamboo.mgt.dao.monitor.MBrokerServiceDao;
import com.unistack.tamboo.mgt.dao.monitor.MCalcWorkerInfoDao;
import com.unistack.tamboo.mgt.helper.TopicHelper;
import com.unistack.tamboo.mgt.model.monitor.MBrokerService;
import com.unistack.tamboo.mgt.model.monitor.MCaclWorkersInfo;
import com.unistack.tamboo.mgt.model.monitor.MClusterServiceVo;
import com.unistack.tamboo.mgt.model.monitor.ZookeeperInfo;
import com.unistack.tamboo.mgt.service.BaseService;
import com.unistack.tamboo.mgt.service.MqService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/7/19
 */
@Service
public class MonitorService extends BaseService {

    @Autowired
    private MBrokerServiceDao mBrokerServiceDao;

    @Autowired
    private MCalcWorkerInfoDao mCalcWorkerInfoDao;

    @Autowired
    private MqService mqService;

    @Autowired
    private TopicHelper topicHelper;

    /**
     * 分页查询broker服务
     *
     * @param query
     * @param request
     * @return
     */
    public PaginationData queryBrokersServiceInfo(String query, PageRequest request) {
        Page<MBrokerService> mBrokerServices = null;
        if (StringUtils.isBlank(query)) {
            mBrokerServices = mBrokerServiceDao.findAll(request);
        } else {
            mBrokerServices = mBrokerServiceDao.getALlBrokerInfo(query, request);
        }
        return PageConvert.convertJpaPage2PaginationData(mBrokerServices, mBrokerServices.getSize());
    }


    /**
     * 查询cluster集群信息
     *
     * @return
     */
    public MClusterServiceVo getBrokerServiceInfo() {
        MClusterServiceVo mcs = new MClusterServiceVo();
        List<MBrokerService> mBrokerServices = mBrokerServiceDao.findAll();
        mBrokerServices.forEach(mBrokerService -> {
            mcs.setBrokersNumber(mBrokerService.getBrokersNumber());
            mcs.setActiveControllerCount(mBrokerService.getActiveControllerCount());
            mcs.setClusterId(mBrokerService.getClusterId());
            mcs.setTimestamp(mBrokerService.getTimestamp());
            mcs.setZkState(mBrokerService.getzKState());
            mcs.setGlobalPartitionCount(mBrokerService.getGlobalPartitionCount());
            mcs.setGlobalTopicCount(mBrokerService.getGlobalTopicCount());
        });
        return mcs;
    }


    /**
     * 查询计算节点服务的信息
     *
     * @param query
     * @param request
     * @return
     */
    public PaginationData queryCalcServiceInfo(String query, PageRequest request) {
        Page<MCaclWorkersInfo> mCalcWorkersInfos = null;
        if (StringUtils.isBlank(query)) {
            mCalcWorkersInfos = mCalcWorkerInfoDao.findAll(request);
        } else {
            mCalcWorkersInfos = mCalcWorkerInfoDao.getALlCalcInfo(query, request);
        }
        return PageConvert.convertJpaPage2PaginationData(mCalcWorkersInfos, mCalcWorkersInfos.getSize());
    }


    /**
     * 获取计算服务节点简介信息
     *
     * @return
     */
    public JSONObject getCalcServiceProfile() {
        return JSON.parseObject(mqService.getBytesValue(CALC_SERVICE_INFO.getBytes()), JSONObject.class);
    }


    public JSONObject getZookeeperInfo() {
        JSONObject data = JSON.parseObject(mqService.getBytesValue(ZOOKEEPER_METRICS_DATA.getBytes()), JSONObject.class);
        JSONObject result = new JSONObject();
        List<ZookeeperInfo> zookeeperInfos = topicHelper.zookeeperInfos();
        for (ZookeeperInfo zk : zookeeperInfos) {
            JSONObject object = data.getJSONObject(zk.getHost());
            result.putAll(object);
            String version = object.getString("Version").split(",")[0].trim();
            result.put("Version", version);
        }
        result.put("heartbeat", data.getString("timestamp"));
        return result;
    }


}
