package com.unistacks.tamboo.kafkaclient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.unistacks.tamboo.kafkaclient.consumer.KafkaConsumer;
import com.unistacks.tamboo.kafkaclient.consumer.KafkaConsumerFactory;
import com.unistacks.tamboo.kafkaclient.producer.KafkaProducer;
import com.unistacks.tamboo.kafkaclient.producer.KafkaProducerFactory;

public class KafkaClientTest {
	private static  String HOST = "kfu1";
	private static  String TOPIC = "tamboo_sanitycheck";

	@Test
	public void test9092() throws InterruptedException, ExecutionException {
		int port = 9092;
		KafkaProducer producer = KafkaProducerFactory.getProducer(HOST + ":" + port);
		Map<RecordMetadata, ProducerRecord<byte[], byte[]>> recordMap = new HashMap<RecordMetadata, ProducerRecord<byte[], byte[]>>();
		for (PartitionInfo info : producer.partitionsFor(TOPIC)) {
			ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(info.topic(), info.partition(),
					("key" + info.partition()).getBytes(), ("value" + info.partition()).getBytes());
			recordMap.put(producer.send(record).get(), record);
		}
		producer.flush();
		producer.close();
		
		Assert.assertFalse(recordMap.isEmpty());

		for (RecordMetadata metadata : recordMap.keySet()) {
			KafkaConsumer consumer = KafkaConsumerFactory.getConsumer(HOST + ":" + port, TOPIC,
					"group" + System.currentTimeMillis());
			try {
				TopicPartition tp = new TopicPartition(metadata.topic(), metadata.partition());
				consumer.unsubscribe();
				consumer.assign(Lists.newArrayList(tp));
				consumer.seek(tp, metadata.offset());
				ConsumerRecord<byte[], byte[]> consumerRecord = consumer.receive();
				ProducerRecord<byte[], byte[]> producerRecord = recordMap.get(metadata);
				Assert.assertEquals(new String(consumerRecord.key()), new String(producerRecord.key()));
			} finally {
				consumer.close();
			}
		}

	}

	@Test
	public void test9093() throws InterruptedException, ExecutionException {
		int port = 9093;
		KafkaProducer producer = KafkaProducerFactory.getProducer(HOST + ":" + port);
		Map<RecordMetadata, ProducerRecord<byte[], byte[]>> recordMap = new HashMap<RecordMetadata, ProducerRecord<byte[], byte[]>>();
		for (PartitionInfo info : producer.partitionsFor(TOPIC)) {
			ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(info.topic(), info.partition(),
					("key" + info.partition()).getBytes(), ("value" + info.partition()).getBytes());
			recordMap.put(producer.send(record).get(), record);
		}
		producer.flush();
		producer.close();
		
		Assert.assertFalse(recordMap.isEmpty());

		for (RecordMetadata metadata : recordMap.keySet()) {
			KafkaConsumer consumer = KafkaConsumerFactory.getConsumer(HOST + ":" + port, TOPIC,
					"group" + System.currentTimeMillis());
			try {
				TopicPartition tp = new TopicPartition(metadata.topic(), metadata.partition());
				consumer.unsubscribe();
				consumer.assign(Lists.newArrayList(tp));
				consumer.seek(tp, metadata.offset());
				ConsumerRecord<byte[], byte[]> consumerRecord = consumer.receive();
				ProducerRecord<byte[], byte[]> producerRecord = recordMap.get(metadata);
				Assert.assertEquals(new String(consumerRecord.key()), new String(producerRecord.key()));
			} finally {
				consumer.close();
			}
		}
	}

}
