package com.example;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaMessageProcessor implements MessageProcessor {
    private org.apache.kafka.clients.producer.KafkaProducer<String, String> producer;
    private String topic;

    public KafkaMessageProcessor(String bootstrapServers, String topic) {
        this.topic = topic;
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.producer = new org.apache.kafka.clients.producer.KafkaProducer<>(props);
    }

    @Override
    public void process(String message) {
        // 显示将要发送的消息内容
        System.out.println("[Kafka] Preparing to send message: " + message);

        producer.send(new ProducerRecord<>(topic, message), (metadata, exception) -> {
            if (exception == null) {
                System.out.println("[Kafka] Success: " + metadata.topic() + " offset " + metadata.offset());
            } else {
                System.err.println("[Kafka] Error: " + exception.getMessage());
            }
        });
    }

    public void close() {
        producer.close();
    }
}
