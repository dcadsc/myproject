package com.example;

public class WebSocketClient {
    private MessageProcessor processor;

    public WebSocketClient(MessageProcessor processor) {
        this.processor = processor;
    }

    // 模拟 WebSocket onMessage
    public void onMessage(String message) {
        System.out.println("[WebSocketClient] Received: " + message);
        processor.process(message);
    }
}
