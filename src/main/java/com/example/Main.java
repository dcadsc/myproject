package com.example;

import java.net.URI;

public class Main {
    public static void main(String[] args) throws Exception {
        // 组合多个处理步骤
        MessageProcessor kafkaSender = new KafkaMessageProcessor("localhost:9092", "my-topic");
        MessageProcessor filterProcessor = new FilterProcessor(kafkaSender);
        MessageProcessor dedupProcessor = new DeduplicationProcessor(filterProcessor); // 传入下一个处理器

        // WebSocket 地址
        String wsUrl = "ws://172.134.10.52:8090/mywebsocket";

        // WebSocketClient
        WebSocketClientImpl wsClient = new WebSocketClientImpl(new URI(wsUrl), dedupProcessor);

        // 连接 WebSocket 服务端
        wsClient.connect();

        // 阻塞主线程保持运行
        while (true) {
            Thread.sleep(10000);
        }
    }
}
