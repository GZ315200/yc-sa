package com.unistacks.tamboo.kafkaclient.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.unistack.tamboo.commons.utils.CommonUtils;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.commons.utils.TambooConstants;
import com.unistack.tamboo.commons.utils.errors.ConfigNotFoundException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;


public class ConfigHelper {
	private static  String EOF_TAG = "EOF";
	private static  String CONFIG_CONSUMER_GROUP_ID = "system";
	private static  long TIMEOUT = 60000;

	private static Map<String, byte[]> cache = new ConcurrentHashMap<String, byte[]>();

	private static byte[] getConfig(String bootstrapServers, String key) {
		if (cache.containsKey(bootstrapServers + key)) {
			return cache.get(bootstrapServers + key);
		}
		KafkaConsumer<byte[], byte[]> consumer = getConfigConsumer(bootstrapServers);
		try {
			long start = System.currentTimeMillis();
			while (true) {
				for (ConsumerRecord<byte[], byte[]> record : consumer
						.poll(Long.parseLong(TambooConfig.DEFAULT_POLL_TIMEOUT))) {
					String recordKey = new String(record.key());
					if (key.equalsIgnoreCase(recordKey)) {
						cache.put(bootstrapServers + recordKey, record.value());
						return record.value();
					} else if (recordKey.equalsIgnoreCase(EOF_TAG)) {
						throw new ConfigNotFoundException(key + " not found in configuration service!");
					}
				}
				if ((System.currentTimeMillis() - start) > TIMEOUT) {
					throw new ConfigNotFoundException("Get " + key + " timeout!");
				}
			}
		} finally {
			consumer.close();
		}
	}

	public static Producer<byte[], byte[]> getConfigProducer(String bootstrapServers) {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.ByteArraySerializer");
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.ByteArraySerializer");
		props.putAll(CommonUtils.getSecurityProps(bootstrapServers));
		return new KafkaProducer<byte[], byte[]>(props);
	}

	public static KafkaConsumer<byte[], byte[]> getConfigConsumer(String bootstrapServers) {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, CONFIG_CONSUMER_GROUP_ID);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.ByteArrayDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.ByteArrayDeserializer");
		props.putAll(CommonUtils.getSecurityProps(bootstrapServers));
		KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<byte[], byte[]>(props);
		List<TopicPartition> tpList = getTopicPartList(consumer.partitionsFor(TambooConstants.TOPIC_CONFIG));
		consumer.assign(tpList);
		consumer.seekToBeginning(tpList);
		return consumer;
	}

	private static List<TopicPartition> getTopicPartList(List<PartitionInfo> partInfoList) {
		List<TopicPartition> tpList = new ArrayList<TopicPartition>();
		for (PartitionInfo pi : partInfoList) {
			tpList.add(new TopicPartition(pi.topic(), pi.partition()));
		}
		return tpList;
	}

	private static Properties getProperties(String bootstrapServers, String key) {
		byte[] bytes = getConfig(bootstrapServers, key);
		return JSON.parseObject(new String(bytes), Properties.class);
	}

	public static Properties getConsumerProperties(String bootstrapServers) {
		Properties consumerProperties = ConfigHelper.getProperties(bootstrapServers,
				"consumer-" + CommonUtils.getProtocol(bootstrapServers) + ".properties");
		return consumerProperties;
	}

	public static Properties getProducerProperties(String bootstrapServers) {
		Properties producerProperties = ConfigHelper.getProperties(bootstrapServers,
				"producer-" + CommonUtils.getProtocol(bootstrapServers) + ".properties");
		return producerProperties;
	}
}
