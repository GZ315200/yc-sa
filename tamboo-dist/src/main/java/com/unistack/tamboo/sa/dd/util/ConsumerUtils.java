package com.unistack.tamboo.sa.dd.util;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.ConfigHelper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author anning
 * @date 2018/6/15 下午3:04
 * @description: utils
 */
public class ConsumerUtils {
    private static  Logger logger = LoggerFactory.getLogger(ConsumerUtils.class);

    /**
     * 获取构建consumer的配置
     *
     * @param brokerList broker list
     * @param group_id   consumer group id
     * @param args       传递的参数，包含
     * @return
     */
    public static Properties getConsumerProps(String brokerList, String group_id, JSONObject args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokerList);
        props.put("group.id", group_id);
        props.put("enable.auto.commit", "false");
        props.put("auto.offset.reset", "latest");  //earliest latest
//        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3000);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 60000);
//        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,"round-robin");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        String jaas_path = JdbcConsumerRunnable.class.getResource("").getPath() + "../../jaas/" + topic + "_jaas.conf";
//        System.setProperty("java.security.auth.login.config", jaas_path); //配置文件路径
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "PLAIN");

        String kafkaAclName = args.getString("kafkaAclName");
        String kafkaAclPassword = args.getString("kafkaAclPassword");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, ConfigHelper.jaasConfigProperty("PLAIN", kafkaAclName, kafkaAclPassword).value());

        return props;
    }
}
