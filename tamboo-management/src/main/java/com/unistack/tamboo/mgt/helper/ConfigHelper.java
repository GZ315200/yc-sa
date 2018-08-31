package com.unistack.tamboo.mgt.helper;

import com.google.common.collect.Maps;

import com.unistack.tamboo.commons.utils.Protocol;
import com.unistack.tamboo.message.kafka.runtime.RunnerConfig;
import org.apache.kafka.clients.ClientUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.types.Password;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.security.plain.PlainLoginModule;
import org.apache.kafka.common.security.scram.ScramLoginModule;
import org.apache.kafka.common.security.scram.ScramMechanism;
import org.apache.kafka.common.utils.Utils;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Gyges Zean
 * @date 2018/5/23
 */
public class ConfigHelper {


    public static  String MECHANISM = "PLAIN";
    public static  String USERNAME = "admin";
    public static  String PASSWORD = "admin123";


    private static  AtomicInteger counter = new AtomicInteger(new Random().nextInt());

    public static Map<String, Object> getAdminProperties(String bootstrapServers) {
        Properties properties = new Properties();
        properties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        properties.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfigProperty(MECHANISM, USERNAME, PASSWORD).value());
        properties.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_PLAINTEXT.name);
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return toMap(properties);
    }


    /**
     * 获取通信协议
     *
     * @param bootstrapServers
     * @return
     */
    public static Protocol getProtocol(String bootstrapServers) {
        InetSocketAddress addr = ClientUtils.parseAndValidateAddresses(Collections.singletonList(bootstrapServers))
                .get(0);
        return Protocol.getProtocolByPort(addr.getPort());
    }


    public static Map<String, Object> setRunnerConfig() {
        Map<String, Object> config = Maps.newHashMap();
        config.put(RunnerConfig.RUNNER_ID, (Thread.currentThread().getName() + "@" + Utils.abs(counter.get())) + "@" + System.currentTimeMillis() / 1000);
        config.put(RunnerConfig.STATUS_STORAGE_PARTITIONS_CONFIG, 1);
        config.put(RunnerConfig.STATUS_STORAGE_REPLICATION_FACTOR_CONFIG, 1);
        return config;
    }


    /**
     * 获取认证的配置
     *
     * @param bootstrapServers
     * @return
     */
    public static Properties getSecurityProps(String bootstrapServers) {
        Protocol protocol = getProtocol(bootstrapServers);
        Properties props = new Properties();
        if (protocol.name().equals(SecurityProtocol.SASL_PLAINTEXT.name)) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_PLAINTEXT.name);
            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        }
        return props;
    }


    public static Password jaasConfigProperty(String mechanism, String username, String password) {
        return new Password(loginModule(mechanism) + " required username=" + username + " password=" + password + ";");
    }


    private static String loginModule(String mechanism) {
        String loginModule;
        switch (mechanism) {
            case "PLAIN":
                loginModule = PlainLoginModule.class.getName();
                break;
            default:
                if (ScramMechanism.isScram(mechanism))
                    loginModule = ScramLoginModule.class.getName();
                else
                    throw new IllegalArgumentException("Unsupported mechanism " + mechanism);
        }
        return loginModule;
    }

    public static Map<String, Object> toMap(Properties properties) {
        Map<String, Object> result = Maps.newHashMap();
        properties.forEach((o, o2) -> {
            result.put((String) o, o2);
        });
        return result;
    }


}
