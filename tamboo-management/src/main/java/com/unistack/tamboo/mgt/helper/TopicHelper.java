package com.unistack.tamboo.mgt.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.commons.utils.errors.GeneralServiceException;
import com.unistack.tamboo.commons.utils.errors.TopicAclCreateException;
import com.unistack.tamboo.commons.utils.util.DESUtil;
import com.unistack.tamboo.message.kafka.bean.TopicDescription;
import com.unistack.tamboo.message.kafka.util.OffsetOperator;
import com.unistack.tamboo.message.kafka.util.TopicAdmin;
import com.unistack.tamboo.mgt.dao.monitor.MOffsetSDao;
import com.unistack.tamboo.mgt.model.monitor.*;
import com.unistack.tamboo.mgt.service.BaseService;
import com.unistack.tamboo.mgt.utils.MonitorUtils;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.requests.DescribeLogDirsResponse;
import org.apache.kafka.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @author Gyges Zean
 * @date 2018/5/23
 */

@Service
public class TopicHelper extends BaseService {


    public static  Logger log = LoggerFactory.getLogger(TopicHelper.class);

    private static  String BOOTSTRAP_SERVER = TambooConfig.KAFKA_BROKER_LIST;

    private Map<String, Object> adminConfig = ConfigHelper.getAdminProperties(BOOTSTRAP_SERVER);

    private TopicAdmin topicAdmin = new TopicAdmin(adminConfig);

    @Autowired
    private MOffsetSDao mOffsetSDao;


    /**
     * 创建topic
     *
     * @param topic
     * @return
     */
    public boolean createTopicAndAcl(String topic, int partition, short replica, String username, String password) {
        try {
            int partitions = TambooConfig.KAFKA_PARTITION;
            short replicas = (short) TambooConfig.KAFKA_REPLICA;
            NewTopic newTopic = TopicAdmin.defineTopic(topic)
                    .partitions(partition > 1 ? partition : partitions)
                    .replicationFactor(replica > 1 ? replica : replicas)
                    .deleted()
                    .build();
            topicAdmin.createTopic(newTopic);
            if (createTopicAcl(topic, username, AclOperation.READ_WRITE)) {
                createKafkaUser(username, password);
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } catch (TopicAclCreateException e) {
            log.error("Failed to create topic for acl", e);
            return Boolean.FALSE;
        }

    }


    /**
     * 创建topic,需要指定权限，read，write，read—write
     *
     * @param topic
     * @param code  该值用于判断创建aclOperation为read and write, read ,write.
     * @return
     */
    public boolean createTopicAndAcl(String topic, int partition, short replica, String username, String password, int code) {
        try {
            int partitions = TambooConfig.KAFKA_PARTITION;
            short replicas = (short) TambooConfig.KAFKA_REPLICA;
            NewTopic newTopic = TopicAdmin.defineTopic(topic)
                    .partitions(partition > 1 ? partition : partitions)
                    .replicationFactor(replica > 1 ? replica : replicas)
                    .deleted()
                    .build();
            topicAdmin.createTopic(newTopic);

            boolean flag = false;

            switch (code) {
                case 1:
                    flag = createTopicAcl(topic, username, AclOperation.READ_WRITE);
                    break;
                case 2:
                    flag = createTopicAcl(topic, username, AclOperation.READ);
                    break;
                case 3:
                    flag = createTopicAcl(topic, username, AclOperation.WRITE);
                default:
                    break;
            }

            if (flag) {
                createKafkaUser(username, password);
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } catch (TopicAclCreateException e) {
            log.error("Failed to create topic for acl", e);
            return Boolean.FALSE;
        }

    }


    /**
     * 删除topic
     *
     * @param topic
     * @return
     */
    public boolean deleteTopic(String topic) {
        return topicAdmin.deleteTopic(topic);
    }

    /**
     * 创建acl
     *
     * @param topic
     * @param username
     * @return
     */
    public boolean createTopicAcl(String topic, String username, int code) {
        return topicAdmin.createTopicForAcl(topic, username, code);
    }


    /**
     * 删除topic
     *
     * @param topic
     * @return
     */
    public boolean deleteTopicAcl(String topic) {
        return topicAdmin.deleteTopicAcl(topic);
    }


    /**
     * 描述集群topic的offset size and offsetLag & topic & partitions
     *
     * @return
     */
    public List<LogDirInfo> describeLogDir(List<String> brokerIds) {
        Map<Integer, Map<String, DescribeLogDirsResponse.LogDirInfo>> logDirInfoMap = topicAdmin.describeLogDir(brokerIds);
        List<LogDirInfo> logDirInfos = Lists.newArrayList();

        for (Map.Entry<Integer, Map<String, DescribeLogDirsResponse.LogDirInfo>> entry : logDirInfoMap.entrySet()) {
            LogDirInfo logDirInfo = new LogDirInfo();
            logDirInfo.setBrokerId(entry.getKey());
            logDirInfo.setHost(getBrokerHost(entry.getKey()));

            Set<JSONObject> voList = Sets.newHashSet();
            Map<String, DescribeLogDirsResponse.LogDirInfo> map = entry.getValue();
            assembleTheLogDir(map, voList);
            logDirInfo.setData(voList);
            logDirInfos.add(logDirInfo);
        }
        return logDirInfos;
    }


    /**
     * 组装logDir的数据
     *
     * @param map
     * @param voList
     */
    private void assembleTheLogDir(Map<String, DescribeLogDirsResponse.LogDirInfo> map, Set<JSONObject> voList) {
        for (Map.Entry<String, DescribeLogDirsResponse.LogDirInfo> infoEntry : map.entrySet()) {
            DescribeLogDirsResponse.LogDirInfo logDirValue = map.get(infoEntry.getKey());

            Map<TopicPartition, DescribeLogDirsResponse.ReplicaInfo> replicaInfos = logDirValue.replicaInfos;

            replicaInfos.forEach((topicPartition, replicaInfo) -> {
                JSONObject data = new JSONObject();
                OffsetSizePartition osp = new OffsetSizePartition();
                String topic = topicPartition.topic();
                int partition = topicPartition.partition();
                long offsetLag = replicaInfo.offsetLag;
                long size = replicaInfo.size;
                osp.setOffsetLag(offsetLag);
                osp.setSize(size);
                osp.setPartition(partition);
                data.put(topic, osp);
                voList.add(data);
            });
        }
    }


    /**
     * 根据brokerId的获取数据
     *
     * @param brokerId
     * @return
     */
    public LogDirInfo describeLogDir(String brokerId) {
        Map<Integer, Map<String, DescribeLogDirsResponse.LogDirInfo>> logDirInfoMap = topicAdmin.describeLogDir(Arrays.asList(brokerId));
        LogDirInfo logDirInfo = new LogDirInfo();
        for (Map.Entry<Integer, Map<String, DescribeLogDirsResponse.LogDirInfo>> entry : logDirInfoMap.entrySet()) {

            logDirInfo.setBrokerId(entry.getKey());
            logDirInfo.setHost(getBrokerHost(entry.getKey()));

            Set<JSONObject> voList = Sets.newHashSet();
            Map<String, DescribeLogDirsResponse.LogDirInfo> map = entry.getValue();
            assembleTheLogDir(map, voList);
            logDirInfo.setData(voList);
        }
        return logDirInfo;
    }


    /**
     * 根据brokerId的获取数据
     *
     * @param brokerId
     * @return
     */
    public LogDirInfo describeLogDir2(String brokerId) {
        Map<Integer, Map<String, DescribeLogDirsResponse.LogDirInfo>> logDirInfoMap = topicAdmin.describeLogDir(Arrays.asList(brokerId));
        LogDirInfo logDirInfo = new LogDirInfo();
        for (Map.Entry<Integer, Map<String, DescribeLogDirsResponse.LogDirInfo>> entry : logDirInfoMap.entrySet()) {

            logDirInfo.setBrokerId(entry.getKey());
            logDirInfo.setHost(getBrokerHost(entry.getKey()));

            List<TopicOffsetVo> voList = Lists.newArrayList();
            Map<String, DescribeLogDirsResponse.LogDirInfo> map = entry.getValue();

            for (Map.Entry<String, DescribeLogDirsResponse.LogDirInfo> infoEntry : map.entrySet()) {
                DescribeLogDirsResponse.LogDirInfo logDirValue = map.get(infoEntry.getKey());

                Map<TopicPartition, DescribeLogDirsResponse.ReplicaInfo> replicaInfos = logDirValue.replicaInfos;

                replicaInfos.forEach((topicPartition, replicaInfo) -> {
                    TopicOffsetVo data = new TopicOffsetVo();
                    OffsetSizePartition osp = new OffsetSizePartition();
                    String topic = topicPartition.topic();
                    int partition = topicPartition.partition();
                    long offsetLag = replicaInfo.offsetLag;
                    long size = replicaInfo.size;
                    osp.setOffsetLag(offsetLag);
                    osp.setSize(size);
                    osp.setPartition(partition);
                    data.setTopic(topic);
                    data.setOffsetSizePartition(osp);
                    voList.add(data);
                });
            }
            logDirInfo.setTopicOffsetVos(voList);
        }
        return logDirInfo;
    }


    public String getBrokerHost(Integer id) {
        return String.valueOf(topicAdmin.getIdToHostInfo().get(id));
    }

    public static void main(String[] args) {
        TopicHelper topicHelper = new TopicHelper();
        System.out.println(topicHelper.getBrokerId("192.168.1.110:9093"));
    }


    public String getBrokerHost(String id) {
        return String.valueOf(topicAdmin.getIdToHostInfo().get(Integer.valueOf(id)));
    }


    /**
     * 根据主机ip获取brokerId
     *
     * @param host
     * @return
     */
    public String getBrokerId(String host) {
        String brokerId = String.valueOf(topicAdmin.getHostToIdInfo().get(host));
        if (StringUtils.isBlank(brokerId)) {
            return "-1";
        }
        return brokerId;
    }


    /**
     * 获取单个topic的信息
     *
     * @param topic
     * @return
     */
    public TopicDescription describeTopicInfo(String topic) {
        return topicAdmin.describeTopic(topic).get(topic);
    }


    public String zookeeperUrl() {
        return TambooConfig.ZOOKEEPER_URL;
    }


    /**
     * 查询topic的acl的信息
     *
     * @param topic
     * @return
     */
    public List<TopicAclVo> describeTopicAcl(String topic) {
        List<TopicAclVo> aclVos = Lists.newArrayList();
        List<AclBinding> aclBindings = topicAdmin.describeAcl(topic);
        aclBindings.forEach(aclBinding -> {
            TopicAclVo topicAclVo = new TopicAclVo();
            topicAclVo.setOperation(aclBinding.entry().operation().name());
            topicAclVo.setPermissionType(aclBinding.entry().permissionType().name());
            topicAclVo.setPrincipal(aclBinding.entry().principal());
            topicAclVo.setTopic(aclBinding.resource().name());
            topicAclVo.setResourceType(aclBinding.resource().resourceType().name());
            topicAclVo.setHost(aclBinding.entry().host());
            aclVos.add(topicAclVo);
        });
        return aclVos;
    }

    /**
     * 获取所有集群列表
     *
     * @return
     */
    public List<String> brokerList() {
        return topicAdmin.listBootstrapServers();
    }


    /**
     * 列出存活的brokers
     *
     * @return
     */
    public JSONObject getActiveBrokerList() {
        JSONObject data = new JSONObject();
        List<String> downBrokerList = Lists.newArrayList();
        List<String> activeBrokerList = Lists.newArrayList();

        String[] allBrokers = BOOTSTRAP_SERVER.split(",");
        String[] existBrokerList = getBrokerInfoStringList().split(",");

        for (String broker : allBrokers) {
            for (String existBroker : existBrokerList) {
                if (broker.equals(existBroker)) {
                    activeBrokerList.add(existBroker);
                } else {
                    downBrokerList.add(broker);
                }
            }
            data.put("active", activeBrokerList);
            data.put("down", downBrokerList);
        }
        return data;
    }

    public List<String> getRealBrokers() {
        List<String> brokers = Lists.newArrayList();
        String[] allBrokers = BOOTSTRAP_SERVER.split(",");
        brokers.addAll(Arrays.asList(allBrokers));
        return brokers;
    }


    /**
     * 未启动的broker
     *
     * @param broker
     * @return
     */
    public boolean isAlive(String broker) {
        return getRealBrokers().contains(broker);
    }


    /**
     * list a brokerIds;
     *
     * @return
     */
    public List<String> brokerIds() {
        return topicAdmin.listBrokerIds();
    }


    /**
     * 获取集群列表，逗号隔开
     *
     * @return
     */
    public String bootstrapServers() {
        return Utils.join(brokerList(), ",");
    }


    public String sparkServers() {
        return TambooConfig.CALC_MASTER_IP;
    }


    /**
     * 获取所有的topic信息
     *
     * @return
     */
    public JSONObject topics() {
        JSONObject data = new JSONObject();
        data.put("topics", topicAdmin.listAllTopics());
        return data;
    }


    /**
     * 从zookeeper中获取brokerList
     *
     * @return
     */
    private String getBrokerInfoStringList() {
        List<BrokerInfo> brokerInfos = getBrokerInfoList();
        List<String> brokerList = Lists.newArrayList();
        brokerInfos.forEach(brokerInfo -> {
            brokerList.add(brokerInfo.getHost() + ":" + brokerInfo.getPort());
        });
        return Utils.join(brokerList, ",");
    }


    /**
     * get broker info list from zookeeper
     *
     * @return
     */
    public List<BrokerInfo> getBrokerInfoList() {
        List<BrokerInfo> list = Lists.newArrayList();
        String zkUrl = zookeeperUrl();
        ZkClient zkClient = null;
        try {
            zkClient = ZKHelper.getZkClient(zkUrl);
            List<String> children = zkClient.getChildren("/brokers/ids");
            for (String child : children) {
                JSONObject json = JSON.parseObject(zkClient.readData("/brokers/ids/" + child).toString());

                BrokerInfo brokerInfo = new BrokerInfo();
                brokerInfo.setBrokerId(Integer.parseInt(child));
                JSONArray jsonArray = json.getJSONArray("endpoints");
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (jsonArray.getString(i).contains("SASL_PLAINTEXT")) {
                        String servers = jsonArray.getString(i).replace("SASL_PLAINTEXT://", " ");
                        String[] listeners = servers.split(":");
                        String host = listeners[0];
                        int port = Integer.parseInt(listeners[1]);
                        brokerInfo.setHost(host.trim());
                        brokerInfo.setPort(port);
                        brokerInfo.setLinuxServerInfo(servers.trim());
                    }
                }
                brokerInfo.setJmxPort(Integer.parseInt(json.get("jmx_port").toString().trim()));
                list.add(brokerInfo);
            }
        } finally {
            ZKHelper.close(zkClient);
        }

        return list;
    }


    /**
     * add user of kafka sasl plain in zookeeper
     *
     * @param username
     * @param password
     * @param password
     */
    public void createKafkaUser(String username, String password) {
        String user = username.trim();
        String psw = password.trim();
        String zkUrl = zookeeperUrl();
        ZkClient zkClient = null;
        try {
            zkClient = ZKHelper.getZkClient(zkUrl);
            if (!zkClient.exists("/unistacks/kafka-sasl")) {
                zkClient.createPersistent("/unistacks/kafka-sasl", true);
            }
            String encryptedPassword = getEncryptedPassword(user, psw);

            if (!zkClient.exists("/unistacks/kafka-sasl/" + user)) {
                zkClient.createPersistent("/unistacks/kafka-sasl/" + user, encryptedPassword);
                log.info("--------add kafka user {}", user);
            } else {
                throw new GeneralServiceException("This topic for acl username & password is exist.");
            }
        } catch (Exception e) {
            log.error("Failed tp create this topic acl for username & password", e.getMessage());
        } finally {
            try {
                ZKHelper.close(zkClient);
            } catch (Exception e) {
                log.info("-------- Failed to close zookeeper client", e);
            }
        }
    }


    private String processPassword(String userName, String originPwd) {
        return originPwd;
    }

    protected String getDecryptedPassword(String userName, String encryptedPwd) {
        return DESUtil.decrypt(encryptedPwd);
    }

    private String getEncryptedPassword(String userName, String originPwd) {
        String processedPassword = processPassword(userName, originPwd);
        return DESUtil.encrypt(processedPassword);
    }


    /**
     * 获取当前topic中的记录
     *
     * @param topicName
     * @param span
     */
    public JSONArray currentTopicRecord(String topicName, Long span) {
        Long topicId = MonitorUtils.getTopicId(topicName);
        List<Long> offsets = Lists.newArrayList();
        long curTimestamp = System.currentTimeMillis();
        List<MOffsetS> mOffsetSList = mOffsetSDao.getOffsets(topicId, new Date(curTimestamp), new Date(curTimestamp - span));
        mOffsetSList.forEach(mOffsetS -> {
            offsets.add(mOffsetS.getOffsetAdd());
        });
        List<Map<Object, byte[]>> records = OffsetOperator.getRecordValueByOffset(offsets, BOOTSTRAP_SERVER, topicName);
        JSONArray array = new JSONArray(records.size());
        for (Map<Object, byte[]> record : records) {
            for (Object key : record.keySet()) {
                String value = new String(record.get(key));
                array.add(JSON.parseObject(value));
            }
        }
        return array;
    }


    /**
     * 获取brokers内的controller信息
     *
     * @return
     */
    public String controller() {
        ZkClient zkClient = null;
        String brokerId = null;
        try {
            String zkUrl = zookeeperUrl();
            zkClient = ZKHelper.getZkClient(zkUrl);
            String result = zkClient.readData("/controller");
            JSONObject object = JSON.parseObject(result);
            brokerId = object.getString("brokerid");
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            ZKHelper.close(zkClient);
        }
        return getBrokerHost(brokerId);
    }


    /**
     * 获取zookeeper的host和jmxPort值
     *
     * @return
     */
    public List<ZookeeperInfo> zookeeperInfos() {
        String zkInfo = TambooConfig.ZOOKEEPER_HOST;
        String jmxPort = TambooConfig.ZOOKEEPER_JMXPORT;
        List<ZookeeperInfo> zookeeperInfos = Lists.newArrayList();
        if (zkInfo.contains(",")) {
            String[] zkInfos = zkInfo.split(",");
            for (String zk : zkInfos) {
                ZookeeperInfo zookeeperInfo = new ZookeeperInfo(zk, String.valueOf(jmxPort));
                zookeeperInfos.add(zookeeperInfo);
            }
            return zookeeperInfos;
        }
        zookeeperInfos.add(new ZookeeperInfo(zkInfo, jmxPort));
        return zookeeperInfos;
    }


    public List<String> connect() {
//        http://192.168.1.110:8083
        List<String> urls = Lists.newArrayList();
        String connectUrls = TambooConfig.KAFKA_CONNECTOR_URL;
        if (connectUrls.contains(",")) {
            String[] connectUrl = connectUrls.split(",");
            for (String url : connectUrl) {
                if (url.contains("http://")) {
                    url = url.replace("http://", "");
                }
                url = url.split(":")[0];
                urls.add(url);
            }
        } else {
            if (connectUrls.contains("http://")) {
                connectUrls = connectUrls.replace("http://", "");
            }
            connectUrls = connectUrls.split(":")[0];
            urls.add(connectUrls);
        }
        return urls;
    }


    /**
     * 获取topic record信息；
     *
     * @param topic
     * @return
     */
    public List<byte[]> getRecords(String topic, int size) {
        return OffsetOperator.getTop5Records(size, topic, BOOTSTRAP_SERVER);
    }


}
