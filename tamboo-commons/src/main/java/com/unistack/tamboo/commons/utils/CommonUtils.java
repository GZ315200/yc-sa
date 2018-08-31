package com.unistack.tamboo.commons.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unistack.tamboo.commons.utils.errors.InvalidValueException;
import org.apache.kafka.clients.ClientUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.types.Password;
import org.apache.kafka.common.security.JaasUtils;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.Boolean;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.net.*;
import java.util.*;

import static com.unistack.tamboo.commons.utils.ConfigHelper.jaasConfigProperty;


/**
 * 工具类:抽取公共的方法
 */
public class CommonUtils {


    private static  Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    /**
     * acl admin username
     */
    private static  String USERNAME = "admin";
    /**
     * acl admin password
     */
    private static  String PASSWORD = "admin123";


    /**
     * 获取通信协议
     *
     * @param bootstrapServers
     * @return
     */
    public static Protocol getProtocol(String bootstrapServers) {
        InetSocketAddress addr = ClientUtils.parseAndValidateAddresses(Arrays.asList(new String[]{bootstrapServers}))
                .get(0);
        return Protocol.getProtocolByPort(addr.getPort());
    }

    /**
     * 获取认证的配置
     *
     * @param bootstrapServers
     * @return
     */
    public static Properties getSecurityProps(String bootstrapServers, Password password) {
        Protocol protocol = getProtocol(bootstrapServers);
        Properties props = new Properties();
        if (protocol.name().equals(SecurityProtocol.SASL_PLAINTEXT.name)) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_PLAINTEXT.name);
            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            props.put(SaslConfigs.SASL_JAAS_CONFIG, password.value());
        }
        return props;
    }

    /**
     * 获取认证的配置
     *
     * @param bootstrapServers
     * @return
     */
    public static Properties getSecurityProps(String bootstrapServers, String password) {
        Protocol protocol = getProtocol(bootstrapServers);
        Properties props = new Properties();
        if (protocol.name().equals(SecurityProtocol.SASL_PLAINTEXT.name)) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_PLAINTEXT.name);
            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            props.put(SaslConfigs.SASL_JAAS_CONFIG, password);
        }
        return props;
    }

    public static Properties getSecurityProps(String bootstrapServers) {
        Protocol protocol = getProtocol(bootstrapServers);
        Properties props = new Properties();
        if (protocol.name().equals(SecurityProtocol.SASL_PLAINTEXT.name)) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_PLAINTEXT.name);
            props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfigProperty("PLAIN", USERNAME, PASSWORD).value());
            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        }
        return props;
    }


    public static Map<String, String> toMap(String bootstrapServers) {
        Properties prop = getSecurityProps(bootstrapServers);
        Map<String, String> data = Maps.newHashMap();
        for (Object key : prop.keySet()) {
            Object value = prop.get(key);
            if (key instanceof String) {
                if (value instanceof String) {
                    data.put((String) key, (String) value);
                }
            }
        }
        return data;
    }


    /**
     * 在JVM中设置认证配置
     */
    public static void setJavaSecurityConfig() {
        if (System.getProperty(JaasUtils.JAVA_LOGIN_CONFIG_PARAM) == null) {
            URL url = CommonUtils.class.getClassLoader().getResource(TambooConstants.DEFAULT_JAAS_CONF);
            System.setProperty(JaasUtils.JAVA_LOGIN_CONFIG_PARAM, url.toExternalForm());
        }
        logger.info(JaasUtils.JAVA_LOGIN_CONFIG_PARAM + "=" + System.getProperty(JaasUtils.JAVA_LOGIN_CONFIG_PARAM));
    }

    /**
     * 获取IP地址
     *
     * @return
     * @throws SocketException
     */
    public static String getIP() throws SocketException {
        String ip = "UnknownHost";
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface intf = en.nextElement();
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                InetAddress inetAddress = enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
                        && inetAddress.isSiteLocalAddress()) {
                    ip = inetAddress.getHostAddress();
                }
            }
        }
        return ip;
    }

    /**
     * 获取进程ID
     *
     * @return
     */
    public static String getProcessId() {
        return ManagementFactory.getRuntimeMXBean().getName();
    }

    /**
     * 获取线程ID
     *
     * @return
     */
    public static long getThreadId() {
        return Thread.currentThread().getId();
    }


    public static long formatTimestamp(long source) {
        // source : Thu Apr 07 21:53:13 CST 2016
        // return : Thu Apr 07 21:53:10 CST 2016
        // 返回整10秒
        return source / (1000 * 10) * (1000 * 10);
    }


    /**
     * 格式化double
     *
     * @param d
     * @return
     */
    public static String formatDouble(double d) {
        int i = (int) d;
        if (i == d) {
            return String.valueOf(i);
        } else {
            return String.format("%.2f", d);
        }
    }

    public static Long formatDoubleToLong(double d) {
        BigDecimal bigDecimal = new BigDecimal(d);
        return bigDecimal.longValue();
    }


    public static String formatLong(long l) {
        BigDecimal bigDecimal = new BigDecimal(l);
        return bigDecimal.toString();
    }


    public static Long formatString(String v) {
        BigDecimal bigDecimal = new BigDecimal(v);
        return bigDecimal.longValue();
    }


    public static Long formatString(long v) {
        BigDecimal bigDecimal = new BigDecimal(v);
        return bigDecimal.longValue();
    }


    /**
     * 格式化size:Bytes,KB,MB,GB
     *
     * @param d
     * @return
     */
    public static String formatSize(double d) {
        List<String> units = Lists.newArrayList("Bytes", "KB", "MB", "GB", "TB");
        for (int i = 0; i < units.size(); i++) {
            double carryD = d / 1024;
            if (carryD < 1) {
                return formatDouble(d) + units.get(i);
            } else {
                if (i == units.size() - 1) {
                    return formatDouble(d) + units.get(i);
                } else {
                    d = carryD;
                }
            }
        }
        return null;
    }

    /**
     * add shutdown hook. the hook runs in cases ^C is received
     *
     * @param closeable
     * @param isClosed
     * @return
     */
    public static Thread addShutdownHook( Closeable closeable,  Boolean isClosed) {
        // add shutdown hook. the hook runs in cases ^C is received
        Thread shutdownHook = new Thread() {
            @Override
            public void run() {
                if (!isClosed.booleanValue()) {
                    logger.info("Received kill signal, stopping producer/consumer.");
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        // close() method of kafka producer/consumer won't throw
                        // IOException
                        e.printStackTrace();
                    }
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        return shutdownHook;
    }


    /**
     * 检查是否为空
     *
     * @param obj
     * @param name
     */
    public static void checkNullOrEmpty(Object obj, String name) {
        if (obj == null)
            throw new InvalidValueException(name + " should not be null!");
        if (obj.toString().trim().isEmpty()) {
            throw new InvalidValueException(name + " should not be empty!");
        }
    }


    public static void sendPost(String strURL, String params) throws IOException {
        OutputStreamWriter out = null;
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Tamboo-Resource-Authorization", "unistacks-license");
            connection.connect();
            out = new OutputStreamWriter(connection.getOutputStream());
            out.write(params);
            out.flush();
            int status = connection.getResponseCode();
            if (status < 200 || status >= 300) {
                throw new IOException("Unexpected response status: " + status);
            }
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    public static List<String> sendGet(String getURL) throws IOException {
        URL getUrl = new URL(getURL);
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
        connection.setRequestProperty("Tamboo-Resource-Authorization", "unistacks-license");
        connection.connect();

        int status = connection.getResponseCode();
        if (status < 200 || status >= 300) {
            throw new IOException("Unexpected response status: " + status);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        List<String> lines = Lists.newArrayList();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        connection.disconnect();
        return lines;
    }


    public static Properties extractSensitiveInfo(Properties props) {
        // kafka client will log the non-kafka config as a warnning which
        // contains some sensitive information such as private credential
        Properties kafkaProps = new Properties();
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            if (!TambooConfig.SENSITIVE_CONFIGS.contains(entry.getKey())) {
                kafkaProps.put(entry.getKey(), entry.getValue());
            }
        }
        return kafkaProps;
    }


    public static void main(String[] args) {
        //acl
        String bootstrapServer = "192.168.1.193:9093";
        String username = "";
        String aclPassword = "";
        String value = jaasConfigProperty("PLAIN", username, aclPassword).value();
        CommonUtils.getSecurityProps(bootstrapServer, value);

    }
}
