package com.unistack.tamboo.commons.utils;

import com.google.common.collect.Lists;
import com.unistack.tamboo.commons.utils.errors.InvalidValueException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gyges Zean
 * @date 2018/5/2
 */
public class Const {

    public static long OFFSET_COLLECT_TIME = 15 * 60 * 1000;

    public Const() {

    }

    public enum State {
        FAILED,
        SUCCESS;
    }

    public enum ErrorMessage {
        INTERNAL_SERVER_UNKNOWN_ERROR("服务器异常,请联系管理员或稍后重试。"),
        DATABASE_ERROR("数据库异常,请联系管理员或稍后重试。"),
        NOT_LOGIN_ERROR("您还没有登录,请登录"),
        FORMAT_DATA_ERROR("格式化数据错误"),
        NO_AVAILABLE_URL("没有可用的URL"),
        NO_DATA_RETURN("没有可返回的数据"),
        NO_FOUND_DATA("找不到该信息"),
        OPERATION_ERROR("操作失败"),
        NULL_PARAM_INPUT("无输入参数或参数值为空"),
        EMPTY_VALUES("目标数据为空"),
        UPLOAD_ERROR("上传失败"),
        PASSWORD_IS_NOT_CORRECT("Bad credentials, please input the correct password."),
        UPLOAD_SIZE_ERROR("Upload file is too large, please upload files less than 20M."),
        RESOURCE_MANAGER_ERROR("资源管理器交互错误"),
        ACCESS_DENIED("不允许访问"),
        START_DATASOURCE_ERROR("启动数据源失败");

        private String message;

        private ErrorMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return this.message;
        }
    }


    public static  String TOPIC_CLIENT_METRICS = "tamboo_client_metrics";
    public static  String TOPIC_CONSUMER_METRICS = "tamboo_consumer_metrics";
    public static  String TOPIC_PRODUCER_METRICS = "tamboo_producer_metrics";
    public static  String TOPIC_CONFIG = "tamboo_config";
    public static  String TOPIC_SANITYCHECK = "tamboo_sanitycheck";

    public static  String METRICS_COMMON_DELIMITER = ":";

    public static  String DEFAULT_JAAS_CONF = "default_kafka_client_jaas.conf";

    public enum Protocol {
        PLAINTEXT(9092), SASL_PLAINTEXT(9093);

        public  int port;

        private static  Map<Integer, Protocol> PORT_TO_SECURITY_PROTOCOL;

        static {
            Map<Integer, Protocol> portToSecurityProtocol = new HashMap<Integer, Protocol>();
            for (Protocol protocol : values()) {
                portToSecurityProtocol.put(protocol.port, protocol);
            }
            PORT_TO_SECURITY_PROTOCOL = Collections.unmodifiableMap(portToSecurityProtocol);
        }

        private Protocol(int port) {
            this.port = port;
        }

        @Override
        public String toString() {
            return this.name();
        }

        public static List<Integer> getSupportedPorts() {
            List<Integer> ports = Lists.newArrayList();
            for (Protocol protocol : values()) {
                ports.add(protocol.port);
            }
            return ports;
        }


        public static Protocol getProtocolByPort(int port) {
            if (!PORT_TO_SECURITY_PROTOCOL.containsKey(port)) {
                throw new InvalidValueException("no protocol matches the port " + port + ", supported ports: "
                        + Protocol.getSupportedPorts());
            }
            return PORT_TO_SECURITY_PROTOCOL.get(port);
        }
    }


}



