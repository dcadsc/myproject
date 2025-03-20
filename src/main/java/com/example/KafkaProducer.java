package com.example;

public class KafkaProducer implements MessageProcessor {
    @Override
    public void process(String message) {
        // 模拟发送到 Kafka
        System.out.println("[KafkaProducer] Send to Kafka: " + message);
    }
}
