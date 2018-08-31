package com.unistacks.tamboo.client;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;

public interface TambooProducer extends Closeable {

	public Future<RecordMetadata> send(ProducerRecord<byte[], byte[]> record);

	public Future<RecordMetadata> send(ProducerRecord<byte[], byte[]> record, Callback callback);

	public void flush();

	public List<PartitionInfo> partitionsFor(String topic);

	public void close();

	public void close(long timeout, TimeUnit unit);

}
