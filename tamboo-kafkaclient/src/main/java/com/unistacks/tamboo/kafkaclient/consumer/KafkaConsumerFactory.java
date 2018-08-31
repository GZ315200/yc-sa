package com.unistacks.tamboo.kafkaclient.consumer;

import java.util.Arrays;
import java.util.Properties;

import com.unistack.tamboo.commons.utils.CommonUtils;
import com.unistack.tamboo.commons.utils.TambooConfig;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import com.unistacks.tamboo.kafkaclient.sasl.ClientPlainLoginModule;
import com.unistacks.tamboo.kafkaclient.tools.ConfigHelper;

public class KafkaConsumerFactory {
	public static KafkaConsumer getConsumer(String bootstrapServers, String topic, String groupId) {
		return getConsumerCommon(bootstrapServers, topic, groupId, new Properties(), null, null);
	}

	public static KafkaConsumer getConsumer(String bootstrapServers, String topic, String groupId,
			String publicCredential, String privateCredential) {
		return getConsumerCommon(bootstrapServers, topic, groupId, new Properties(), publicCredential,
				privateCredential);
	}

	public static KafkaConsumer getConsumer(String bootstrapServers, String topic, String groupId,
			Properties customizedProps) {
		return getConsumerCommon(bootstrapServers, topic, groupId, customizedProps, null, null);
	}

	public static KafkaConsumer getConsumer(String bootstrapServers, String topic, String groupId,
			Properties customizedProps, String publicCredential, String privateCredential) {
		return getConsumerCommon(bootstrapServers, topic, groupId, customizedProps, publicCredential,
				privateCredential);
	}

	private static KafkaConsumer getConsumerCommon(String bootstrapServers, String topic, String groupId,
			Properties customizedProps, String publicCredential, String privateCredential) {
		nullCheck(bootstrapServers, topic, groupId);
		ClientPlainLoginModule.setCredential(publicCredential, privateCredential);
		Properties props = ConfigHelper.getConsumerProperties(bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, getClientId(groupId));
		if (customizedProps != null) {
			props.putAll(customizedProps);
		}
		KafkaConsumer consumer = new KafkaConsumer(props, ClientPlainLoginModule.getPublicCredential());
		consumer.subscribe(Arrays.asList(topic));
		return consumer;
	}

	private static void nullCheck(String bootstrapServers, String topic, String groupId) {
		CommonUtils.checkNullOrEmpty(bootstrapServers, CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG);
		CommonUtils.checkNullOrEmpty(topic, TambooConfig.TOPIC_CONFIG);
		CommonUtils.checkNullOrEmpty(groupId, ConsumerConfig.GROUP_ID_CONFIG);
	}

	private static String getClientId(String groupId) {
		String pid = CommonUtils.getProcessId();
		long tid = CommonUtils.getThreadId();
		return groupId + "_" + pid + "_" + tid;
	}
}
