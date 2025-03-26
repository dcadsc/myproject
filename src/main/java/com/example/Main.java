package com.example;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        // 1. 导入白名单
        WhitelistImporter importer = new WhitelistImporter();
        List<String> whitelist = importer.importWhitelist("/opt/whitelist9.xlsx");

        // 2. 组合多个处理步骤
        MessageProcessor kafkaSender = new KafkaMessageProcessor("localhost:9092", "my-topic");
        MessageProcessor filterProcessor = new FilterProcessor(kafkaSender, whitelist);
        MessageProcessor dedupProcessor = new DeduplicationProcessor(filterProcessor);

        // 3. WebSocket 地址列表（按优先级顺序）
        List<URI> wsUris = Arrays.asList(
            new URI("ws://172.134.10.118:8090/mywebsocket"), // 主服务器
            new URI("ws://172.134.10.52:8090/mywebsocket")  // 备用服务器
        );

        // 4. 创建 WebSocket 客户端
        TyrusWebSocketClient wsClient = new TyrusWebSocketClient(wsUris, dedupProcessor);
        wsClient.connect();

        // 5. 阻塞主线程（防止主线程退出）
        Thread.currentThread().join();
    }
}
