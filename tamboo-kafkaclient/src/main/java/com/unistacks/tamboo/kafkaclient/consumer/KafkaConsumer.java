package com.unistacks.tamboo.kafkaclient.consumer;

import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.Callable;

import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.commons.utils.TambooConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unistacks.tamboo.kafkaclient.pojo.Throughput;
import com.unistacks.tamboo.kafkaclient.tools.MetricsReporter;

public class KafkaConsumer extends org.apache.kafka.clients.consumer.KafkaConsumer<byte[], byte[]> {
    private static  Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private String publicCredential;
    private Boolean metricsStopped = Boolean.FALSE;
    private Throughput throughput;
    private static long POLL_TIMEOUT;
    private Iterator<ConsumerRecord<byte[], byte[]>> cache;

    KafkaConsumer(Properties props, String publicCredential) {
        super(props);
        this.publicCredential = publicCredential;
        this.throughput = new Throughput();
        POLL_TIMEOUT = Long
                .parseLong(props.getProperty(TambooConfig.POLL_TIMEOUT_CONFIG, TambooConfig.DEFAULT_POLL_TIMEOUT));
        sendMetrics(props);
    }

    private void sendMetrics(Properties props) {
        String metricKey = MetricsReporter.getMetricKey();
        MetricsReporter.sendMetrics(metricKey, props, metricsStopped, new Callable<Properties>() {
            @Override
            public Properties call() throws Exception {
                Properties metrics = MetricsReporter.formatMetrics(metrics());
                metrics.putAll(throughput.getAndReset());
                if (publicCredential != null) {
                    metrics.put(TambooConfig.PUBLIC_CREDENTIAL_CONFIG, publicCredential);
                }
                return metrics;
            }
        }, TambooConstants.TOPIC_CONSUMER_METRICS);
    }

    public ConsumerRecord<byte[], byte[]> receive() {
        while (cache == null || !cache.hasNext()) {
            cache = poll(POLL_TIMEOUT).iterator();
        }
        return cache.next();
    }

    @Override
    public ConsumerRecords<byte[], byte[]> poll(long timeout) {
        ConsumerRecords<byte[], byte[]> records = super.poll(timeout);
        throughput.update(records);
        logger.debug("Consume: records count = {}", records.count());
        return records;
    }

    @Override
    public void close() {
        super.close();
        metricsStopped = Boolean.TRUE;
    }
}
