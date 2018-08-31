package com.unistack.tamboo.mgt.job;

import com.unistack.tamboo.mgt.dao.monitor.BrokerInfoDao;
import com.unistack.tamboo.mgt.model.monitor.BrokerInfo;
import com.unistack.tamboo.mgt.service.BaseService;
import com.unistack.tamboo.mgt.helper.TopicHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/5/25
 */

@Component(value = "clusterInfoSaving")
public class ClusterInfoSaving extends BaseService {

    public static  Logger logger = LoggerFactory.getLogger(ClusterInfoSaving.class);

    @Autowired
    private TopicHelper topicHelper;

    @Autowired
    private BrokerInfoDao brokerInfoDao;

    public void saveBrokerInfo() {
        try {
            brokerInfoDao.deleteAll();
            List<BrokerInfo> brokerInfos = topicHelper.getBrokerInfoList();
            brokerInfoDao.save(brokerInfos);
        } catch (Exception e) {
            logger.error("saveBrokerInfo: ", e);
        }
    }


}
