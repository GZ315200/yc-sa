package com.unistack.tamboo.commons.utils;

import com.google.common.collect.Lists;
import com.unistack.tamboo.commons.utils.errors.InvalidValueException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TambooConstants {
	public static  String TOPIC_CLIENT_METRICS = "tamboo_client_metrics";
	public static  String TOPIC_CONSUMER_METRICS = "tamboo_consumer_metrics";
	public static  String TOPIC_PRODUCER_METRICS = "tamboo_producer_metrics";
	public static  String TOPIC_CONFIG = "tamboo_config";
	public static  String TOPIC_SANITYCHECK = "tamboo_sanitycheck";

	public static  String METRICS_COMMON_DELIMITER = ":";

	public static  String DEFAULT_JAAS_CONF = "default_kafka_client_jaas.conf";

	public enum Protocol {
		PLAINTEXT(9092), SASL_PLAINTEXT(9093);

		public  int port;

		private static  Map<Integer, Protocol> PORT_TO_SECURITY_PROTOCOL;

		static {
			Map<Integer, Protocol> portToSecurityProtocol = new HashMap<Integer, Protocol>();
			for (Protocol protocol : values()) {
				portToSecurityProtocol.put(protocol.port, protocol);
			}
			PORT_TO_SECURITY_PROTOCOL = Collections.unmodifiableMap(portToSecurityProtocol);
		}

		private Protocol(int port) {
			this.port = port;
		}

		@Override
		public String toString() {
			return this.name();
		}

		public static List<Integer> getSupportedPorts() {
			List<Integer> ports = Lists.newArrayList();
			for (Protocol protocol : values()) {
				ports.add(protocol.port);
			}
			return ports;
		}

		public static Protocol getProtocolByPort(int port) {
			if (!PORT_TO_SECURITY_PROTOCOL.containsKey(port)) {
				throw new InvalidValueException("no protocol matches the port " + port + ", supported ports: "
						+ Protocol.getSupportedPorts());
			}
			return PORT_TO_SECURITY_PROTOCOL.get(port);
		}
	}
}
