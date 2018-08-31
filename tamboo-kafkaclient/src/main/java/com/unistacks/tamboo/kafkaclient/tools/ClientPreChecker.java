package com.unistacks.tamboo.kafkaclient.tools;

import com.unistack.tamboo.commons.utils.TambooConstants;
import com.unistacks.tamboo.kafkaclient.consumer.KafkaConsumer;
import com.unistacks.tamboo.kafkaclient.consumer.KafkaConsumerFactory;
import com.unistacks.tamboo.kafkaclient.producer.KafkaProducer;
import com.unistacks.tamboo.kafkaclient.producer.KafkaProducerFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.errors.TopicAuthorizationException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class ClientPreChecker {
	private static  List<String> results = new ArrayList<String>();

	private static  int PING_THRESHOLD = 1000;
	private static  int TELNET_THRESHOLD = 1000;
	private static  int LATENCY_HRESHOLD = 1000;
	
	private static enum LEVEL {
		SUCCESS, WARN, FAILURE
	}

	@Option(name = "-check", required = false, usage = "1/network 2/topic 3/latency")
	private String check = "1";

	@Option(name = "-bootstrap.servers", required = true, usage = "k1:9092,k2:9092,k3:9092")
	private String bootstrapServers;

	@Option(name = "-topics", required = false, usage = "required while check=2, splitted by comma(,)")
	private String topics;

	@Option(name = "-username", required = false)
	private String username;

	@Option(name = "-password", required = false)
	private String password;

	@Option(name = "-cycle.times", required = false)
	private Integer cycle = 1;

	public static void main(String[] args) {
		try {
			new ClientPreChecker().doMain(args);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	private void doMain(String[] args) throws CmdLineException {
		parseArgument(args);
		if (check.equals("1") || check.equals("network")) {
			checkNetwork();
		} else if (check.equals("2") || check.equals("topic")) {
			checkTopics();
		} else if (check.equals("3") || check.equals("latency")) {
			checkLatency();
		}

		for (String result : results) {
			System.out.println(result);
		}
	}

	private void parseArgument(String[] args) throws CmdLineException {
		CmdLineParser parser = new CmdLineParser(this);
		parser.printUsage(System.out);
		parser.parseArgument(args);
	}

	private void checkNetwork() {
		List<String> hostsAndports = Arrays.asList(bootstrapServers.split(","));
		for (String hostAndPort : hostsAndports) {
			String host = hostAndPort.split(":")[0];
			int port = Integer.parseInt(hostAndPort.split(":")[1]);
			printPing(host);
			printTelnet(host, port);
		}
		List<String> fullBootstrapServers = new ArrayList<String>(getFullBootstrapServers());
		fullBootstrapServers.removeAll(hostsAndports);
		for (String hostAndPort : fullBootstrapServers) {
			String host = hostAndPort.split(":")[0];
			int port = Integer.parseInt(hostAndPort.split(":")[1]);
			printPing(host);
			printTelnet(host, port);
		}
	}

	private Set<String> getFullBootstrapServers() {
		Set<String> fullBootstrapServers = new HashSet<String>();
		Properties consumerProps = ConfigHelper.getConsumerProperties(bootstrapServers);
		fullBootstrapServers.addAll(
				Arrays.asList(consumerProps.getProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "").split(",")));

		Properties producerProps = ConfigHelper.getProducerProperties(bootstrapServers);
		fullBootstrapServers.addAll(
				Arrays.asList(producerProps.getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "").split(",")));
		return fullBootstrapServers;
	}

	private void printPing(String host) {
		long cost = tryPing(host);
		if (cost == Long.MAX_VALUE) {
			results.add(LEVEL.FAILURE + " - ping " + host);
		} else if (cost > PING_THRESHOLD) {
			results.add(LEVEL.WARN + " - ping " + host + ", cost " + cost + "ms");
		} else {
			results.add(LEVEL.SUCCESS + " - ping " + host + ", cost " + cost + "ms");
		}
	}

	private long tryPing(String host) {
		long start = System.currentTimeMillis();
		try {
			InetAddress address = InetAddress.getByName(host);// ping this IP
			if (address.isReachable(5000)) {
				return System.currentTimeMillis() - start;
			} else {
				return Long.MAX_VALUE;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Long.MAX_VALUE;
		}
	}

	private void printTelnet(String host, int port) {
		long cost = tryTelnet(host, port);
		if (cost == Long.MAX_VALUE) {
			results.add(LEVEL.FAILURE + " - telnet " + host + " " + port);
		} else if (cost > TELNET_THRESHOLD) {
			results.add(LEVEL.WARN + " - telnet " + host + " " + port + ", cost " + cost + "ms");
		} else {
			results.add(LEVEL.SUCCESS + " - telnet " + host + " " + port + ", cost " + cost + "ms");
		}
	}

	private long tryTelnet(String host, int port) {
		Socket client = null;
		long start = System.currentTimeMillis();
		try {
			client = new Socket(host, port);
		} catch (Exception e) {
			e.printStackTrace();
			return Long.MAX_VALUE;
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return System.currentTimeMillis() - start;
	}

	private void checkTopics() {
		if (topics == null) {
			return;
		}
		KafkaConsumer consumer = KafkaConsumerFactory.getConsumer(bootstrapServers, TambooConstants.TOPIC_SANITYCHECK,
				"ClientPreChecker", username, password);
		try {
			for (String topic : topics.split(",")) {
				try {
					if (consumer.partitionsFor(topic) == null) {
						results.add(LEVEL.FAILURE + " - topic " + topic + " doesn't exist.");
					} else {
						results.add(LEVEL.SUCCESS + " - topic " + topic + " exists.");
					}
				} catch (TopicAuthorizationException e) {
					results.add("Not authorized to access topic: [" + topic
							+ "], please add arguments: -username <username> -password <password>");
					continue;
				}
			}
		} finally {
			consumer.close();
		}
	}
	
	private void checkLatency() {
		if (topics == null) {
			return;
		}
		Map<String, Long> cache = new TreeMap<>();
		KafkaProducer producer = KafkaProducerFactory.getProducer(bootstrapServers);
		List<PartitionInfo> partitions = producer.partitionsFor(topics);
		try {
			for (int i = 1; i <= (cycle + 1); i++) {
				
				StringBuilder sb = new StringBuilder();
				for (PartitionInfo pi : partitions) {
					long start = System.currentTimeMillis();
					String key = start + "";
					ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(topics, pi.partition(),
									key.getBytes(), key.getBytes());
					try {
						producer.send(record).get();
						if(i != 1){
							long cost = System.currentTimeMillis() - start;
							String partitionName = "partition-"+pi.partition();
							if(!cache.containsKey(partitionName)){
								cache.put(partitionName, cost);
							}else{
								cache.put(partitionName, cache.get(partitionName)+cost);
							}
							sb.append(partitionName).append(":").append(cost+"ms").append(" , ");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(i==1){
					continue;
				}
				System.out.println(sb.toString().substring(0,sb.toString().lastIndexOf(",")));
				
				Thread.sleep(500);
			}
			printResult(cache);
		} catch(Exception e){
			e.printStackTrace();
		}finally {
			producer.close();
			
		}
	}

	private void printResult(Map<String, Long> cache) {
		System.out.println("\nCheck the "+ topics +" send latency, the results are as follows:\n");
		for (String key : cache.keySet()) {
			long cost = cache.get(key) / cycle;
			if (cost > LATENCY_HRESHOLD) {
				results.add(LEVEL.WARN + " - latency " + key + ", cost " + cost + "ms.");
			} else {
				results.add(LEVEL.SUCCESS + " - latency " + key + ", cost " + cost + "ms.");
			}
		}
	}
}
