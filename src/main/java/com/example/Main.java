package com.example;

import java.net.URI;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        // 1. 导入白名单
        WhitelistImporter importer = new WhitelistImporter();
        List<String> whitelist = importer.importWhitelist("/opt/whitelist.xlsx"); // 这里写你白名单 Excel 路径

        // 打印白名单内容，排查用
        for (String entry : whitelist) {
            System.out.println("[WHITELIST] " + entry);
        }

        // 2. 组合多个处理步骤
        MessageProcessor kafkaSender = new KafkaMessageProcessor("localhost:9092", "my-topic");
        MessageProcessor filterProcessor = new FilterProcessor(kafkaSender, whitelist);
        MessageProcessor dedupProcessor = new DeduplicationProcessor(filterProcessor);

        // 3. WebSocket 地址
        String wsUrl = "ws://172.134.10.52:8090/mywebsocket";

        // 4. WebSocketClient
        WebSocketClientImpl wsClient = new WebSocketClientImpl(new URI(wsUrl), dedupProcessor);

        // 5. 连接 WebSocket 服务端
        wsClient.connect();

        // 6. 阻塞主线程保持运行
        while (true) {
            Thread.sleep(10000);
        }
    }
}
