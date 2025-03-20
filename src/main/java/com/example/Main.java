package com.example;

public class Main {
    public static void main(String[] args) {
        // 装配装饰链：Kafka <- Filter <- Dedup <- WebSocket
        MessageProcessor kafkaSender = new KafkaProducer();
        MessageProcessor filter = new FilterProcessor(kafkaSender);
        MessageProcessor dedup = new DeduplicationProcessor(filter);

        WebSocketClient wsClient = new WebSocketClient(dedup);

        // 模拟 WebSocket 消息
        wsClient.onMessage("msg1");
        wsClient.onMessage("msg1"); // 去重
        wsClient.onMessage("filter_this_msg"); // 被过滤
        wsClient.onMessage("msg2"); // 通过全部处理链，发送到 Kafka
    }
}
