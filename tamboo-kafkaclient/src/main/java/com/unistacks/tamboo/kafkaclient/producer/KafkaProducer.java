package com.unistacks.tamboo.kafkaclient.producer;

import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.commons.utils.TambooConstants;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unistacks.tamboo.kafkaclient.pojo.Throughput;
import com.unistacks.tamboo.kafkaclient.tools.MetricsReporter;

public class KafkaProducer extends org.apache.kafka.clients.producer.KafkaProducer<byte[], byte[]> {
    private static  Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    private String publicCredential;
    private Boolean metricsStopped = Boolean.FALSE;
    private Throughput throughput;

    KafkaProducer(Properties props, String publicCredential) {
        super(props);
        this.publicCredential = publicCredential;
        this.throughput = new Throughput();
        sendMetrics(props);
    }

    @Override
    public Future<RecordMetadata> send(ProducerRecord<byte[], byte[]> record, Callback callback) {
        Future<RecordMetadata> future = super.send(record, callback);
        logger.debug("Produce: topic = {}, key = {}", record.topic(),
                record.key() == null ? "" : new String(record.key()));
        throughput.update(record);
        return future;
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
        }, TambooConstants.TOPIC_PRODUCER_METRICS);
    }

    @Override
    public void close() {
        close(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    @Override
    public void close(long timeout, TimeUnit timeUnit) {
        super.flush();
        super.close(timeout, timeUnit);
        metricsStopped = Boolean.TRUE;
    }
}
