package com.unistacks.tamboo.client;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

public interface TambooConsumer extends Closeable {

	public ConsumerRecords<byte[], byte[]> poll(long timeout);

	public ConsumerRecords<byte[], byte[]> poll();

	public void commitSync();

	public void commitAsync();

	public long position(TopicPartition partition);

	public List<PartitionInfo> partitionsFor(String topic);

	public Map<String, List<PartitionInfo>> listTopics();

	public void close();
}
