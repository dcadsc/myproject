package com.example;

public class KafkaMessageProcessor implements MessageProcessor {
    private String bootstrapServers;
    private String topic;

    public KafkaMessageProcessor(String bootstrapServers, String topic) {
        this.bootstrapServers = bootstrapServers;
        this.topic = topic;
    }

    @Override
    public void process(String message) {
        // 这里实现 Kafka 发送逻辑
        System.out.println("[Kafka] Sending message to " + topic + ": " + message);
    }
}