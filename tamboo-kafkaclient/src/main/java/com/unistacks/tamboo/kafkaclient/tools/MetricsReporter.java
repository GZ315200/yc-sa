package com.unistacks.tamboo.kafkaclient.tools;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.PlatformManagedObject;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

import com.unistack.tamboo.commons.utils.CommonUtils;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.commons.utils.TambooConstants;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class MetricsReporter {
    private static  Logger logger = LoggerFactory.getLogger(MetricsReporter.class);

    private static MetricsReporter instance = null;
    private static KafkaProducer<byte[], byte[]> metricsProducer;

    public static MetricsReporter getInstance(String bootstrapServers) {
        if (instance == null) {
            synchronized (MetricsReporter.class) {
                if (instance == null) {
                    instance = new MetricsReporter(bootstrapServers);
                }
            }
        }
        return instance;
    }

    public void flush() {
        if (metricsProducer != null) {
            metricsProducer.flush();
        }
    }

    private MetricsReporter(String bootstrapServers) {
        Properties props = ConfigHelper.getProducerProperties(bootstrapServers);
        metricsProducer = new KafkaProducer<byte[], byte[]>(props);
    }

    private void sendRegularMetrics(String metricKey,  Properties metrics,  String topicName) {
        String fullMetricsKey = metricKey + System.currentTimeMillis();
        ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(topicName, fullMetricsKey.getBytes(),
                JSON.toJSONString(metrics).getBytes());
        metricsProducer.send(record);
    }

    private void sendClientMetrics(String metricKey, Properties kafkaClientProps) {
        Properties props = new Properties();
        props.put("Runtime", getSystemMetrics(ManagementFactory.getRuntimeMXBean(), RuntimeMXBean.class));
        props.put("OperatingSystem", getSystemMetrics(ManagementFactory.getOperatingSystemMXBean(), OperatingSystemMXBean.class));
        props.put("Memory", getSystemMetrics(ManagementFactory.getMemoryMXBean(), MemoryMXBean.class));
        props.put("ClientProperties", kafkaClientProps);

        String value = JSON.toJSONString(props);
        ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(TambooConstants.TOPIC_CLIENT_METRICS,
                metricKey.getBytes(), value.getBytes());
        metricsProducer.send(record);
    }

    private Properties getSystemMetrics(PlatformManagedObject bean, Class<?> clazz) {
        Properties props = new Properties();
        for (Method method : clazz.getMethods()) {
            if (method.getName().startsWith("get")) {
                try {
                    props.put(method.getName().replaceFirst("get", ""), method.invoke(bean));
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    logger.error("Fail to " + clazz.getName() + "." + method.getName(), e);
                }
            }
        }
        return props;
    }

    public static void sendMetrics( String metricKey,  Properties clientProperties,
                                    Boolean metricsStopped,  Callable<Properties> callable,  String topicName) {
        logger.info("send metrics, key = " + metricKey);
         String bootstrapServers = clientProperties.getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG);
        new Thread(new Runnable() {
            @Override
            public void run() {
                MetricsReporter reporter = MetricsReporter.getInstance(bootstrapServers);
                try {
                    reporter.sendClientMetrics(metricKey, clientProperties);
                    long metricsReportInterval = Long.parseLong(clientProperties.getProperty(
                            TambooConfig.METRICS_REPORT_INTERVAL_CONFIG, TambooConfig.DEFAULT_METRICS_REPORT_INTERVAL));
                    while (!metricsStopped) {
                        reporter.sendRegularMetrics(metricKey, callable.call(), topicName);
                        Thread.sleep(metricsReportInterval);
                    }
                    reporter.sendRegularMetrics(metricKey, callable.call(), topicName);
                    reporter.flush();
                } catch (Exception e) {
                    logger.error("Fail to send metrics.", e);
                }
                logger.info("Metrics reporter is stopped.");
            }
        }).start();
    }


    public static String getMetricKey() {
        String ip = "";
        try {
            ip = CommonUtils.getIP();
        } catch (SocketException e) {
            logger.warn("Fail to get ip.", e);
        }
        String pid = CommonUtils.getProcessId();
        long tid = CommonUtils.getThreadId();
        StringBuilder key = new StringBuilder().append(ip).append(TambooConstants.METRICS_COMMON_DELIMITER).append(pid)
                .append(TambooConstants.METRICS_COMMON_DELIMITER).append(tid)
                .append(TambooConstants.METRICS_COMMON_DELIMITER);
        return key.toString();
    }



    public static Properties formatMetrics(Map<MetricName, ? extends Metric> metrics) {
        Properties props = new Properties();
        if (metrics != null) {
            for (MetricName name : metrics.keySet()) {
                Metric metric = metrics.get(name);
                if (metric != null) {
                    props.put(name.group() + TambooConstants.METRICS_COMMON_DELIMITER + name.name(),
                            CommonUtils.formatDouble(metric.value()));
                }

            }
        }
        return props;
    }

    public static void close() {
        metricsProducer.flush();
        metricsProducer.close();
    }
}
