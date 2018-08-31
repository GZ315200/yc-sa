package com.unistacks.tamboo.kafkaclient.pojo;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;


public class Throughput {
	private Map<String, ThroughputInfo> cache = new ConcurrentHashMap<>();
	private long lastMessageTimestamp;

	public Properties getAndReset() {
		Properties props = new Properties();
		props.put("throughput", JSON.toJSONString(cache));
		props.put("lastMessageTimestamp", lastMessageTimestamp);
		reset();
		return props;
	}

	public void reset() {
		for (ThroughputInfo info : cache.values()) {
			info.reset();
		}
	}

	public void update(ConsumerRecords<byte[], byte[]> records) {
		for (ConsumerRecord<byte[], byte[]> record : records) {
			String topic = record.topic();
			if (!cache.containsKey(topic)) {
				cache.put(topic, new ThroughputInfo());
			}
			cache.get(topic).update(record);
		}
		lastMessageTimestamp = System.currentTimeMillis();
	}

	public void update(ProducerRecord<byte[], byte[]> record) {
		String topic = record.topic();
		if (!cache.containsKey(topic)) {
			cache.put(topic, new ThroughputInfo());
		}
		cache.get(topic).update(record);
		lastMessageTimestamp = System.currentTimeMillis();
	}

	private class ThroughputInfo {
		private long count = 0;
		private long size = 0;
		private String lastKey = null;
		private long lastTimestamp;

		@JSONField
		public long getCount() {
			return count;
		}

		@JSONField
		public long getSize() {
			return size;
		}

		@JSONField
		public String getLastKey() {
			return lastKey;
		}

		@JSONField
		public long getLastTimestamp() {
			return lastTimestamp;
		}

		private String getKeyString(byte[] key) {
			return key == null ? "null" : new String(key);
		}

		private long size(byte[] b) {
			return b == null ? 0 : b.length;
		}

		public void update(ProducerRecord<byte[], byte[]> record) {
			count++;
			size += size(record.key()) + size(record.value());
			lastKey = getKeyString(record.key());
			lastTimestamp = System.currentTimeMillis();
		}

		public void update(ConsumerRecord<byte[], byte[]> record) {
			count++;
			size += size(record.key()) + size(record.value());
			lastKey = getKeyString(record.key());
			lastTimestamp = System.currentTimeMillis();
		}

		public void reset() {
			count = 0;
			size = 0;
		}
	}
}
