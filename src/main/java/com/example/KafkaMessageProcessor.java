package com.example;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaMessageProcessor implements MessageProcessor {
    private String bootstrapServers;
    private String topic;
    private final KafkaProducer<String, String> producer;

    public KafkaMessageProcessor(String bootstrapServers, String topic) {
        this.bootstrapServers = bootstrapServers;
        this.topic = topic;
        this.producer = createProducer();
    }

    private KafkaProducer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        return new KafkaProducer<>(props);
    }

    @Override
    public void process(String message) {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    // 处理异常，防止 IDE 报错
                    System.err.println("[Kafka] Send failed: " + exception.getMessage());
                    exception.printStackTrace(); // 可选，debug用
                } else {
                    System.out.println("[Kafka] Sent to topic: " + metadata.topic() +
                            ", partition: " + metadata.partition() +
                            ", offset: " + metadata.offset());
                }
            });
        } catch (Exception e) {
            System.err.println("[Kafka] Producer encountered error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 建议加个 close()，用于 Main 中优雅关闭 producer
    public void close() {
        if (producer != null) {
            producer.close();
        }
    }
}