package com.unistacks.tamboo.kafkaclient;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.unistacks.tamboo.kafkaclient.consumer.KafkaConsumer;
import com.unistacks.tamboo.kafkaclient.consumer.KafkaConsumerFactory;
import com.unistacks.tamboo.kafkaclient.producer.KafkaProducer;
import com.unistacks.tamboo.kafkaclient.producer.KafkaProducerFactory;

public class RecommendedKafkaClientSample {
	public static void produce(Map<String, String> data) {
		KafkaProducer producer = KafkaProducerFactory.getProducer("localhost:9092");
		for (String key : data.keySet()) {
			producer.send(new ProducerRecord<byte[], byte[]>("topic", key.getBytes(), data.get(key).getBytes()));
		}
		producer.close();
	}

	public static void consume() {
		KafkaConsumer consumer = KafkaConsumerFactory.getConsumer("localhost:9092", "topic", "group");
		try {
			while (true) {
				ConsumerRecord<byte[], byte[]> record = consumer.receive();
				System.out.println("key = "+new String(record.key()) + new String(record.value()));
			}
		}finally {
			consumer.close();
		}
	}
}
