package com.unistacks.tamboo.kafkaclient.producer;

import java.util.Properties;

import com.unistack.tamboo.commons.utils.CommonUtils;
import org.apache.kafka.clients.CommonClientConfigs;

import com.unistacks.tamboo.kafkaclient.sasl.ClientPlainLoginModule;
import com.unistacks.tamboo.kafkaclient.tools.ConfigHelper;

public class KafkaProducerFactory {
	public static KafkaProducer getProducer(String bootstrapServers) {
		return getProducerCommon(bootstrapServers, new Properties(), null, null);
	}

	public static KafkaProducer getProducer(String bootstrapServers, Properties customizedProps) {
		return getProducerCommon(bootstrapServers, customizedProps, null, null);
	}

	public static KafkaProducer getProducer(String bootstrapServers, String publicCredential,
			String privateCredential) {
		return getProducerCommon(bootstrapServers, new Properties(), publicCredential, privateCredential);
	}

	public static KafkaProducer getProducer(String bootstrapServers, Properties customizedProps,
			String publicCredential, String privateCredential) {
		return getProducerCommon(bootstrapServers, customizedProps, publicCredential, privateCredential);
	}

	private static KafkaProducer getProducerCommon(String bootstrapServers, Properties customizedProps,
			String publicCredential, String privateCredential) {
		nullCheck(bootstrapServers);
		ClientPlainLoginModule.setCredential(publicCredential, privateCredential);
		Properties props = ConfigHelper.getProducerProperties(bootstrapServers);
		if (customizedProps != null) {
			props.putAll(customizedProps);
		}
		return new KafkaProducer(props, ClientPlainLoginModule.getPublicCredential());
	}

	private static void nullCheck(String bootstrapServers) {
		CommonUtils.checkNullOrEmpty(bootstrapServers, CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG);
	}
}
