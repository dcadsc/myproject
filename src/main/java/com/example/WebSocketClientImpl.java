package com.example;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.*;

public class WebSocketClientImpl extends WebSocketClient {
    private final MessageProcessor processor;
    private final URI serverUri;
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static volatile boolean reconnecting = false;
    private static final int RECONNECT_DELAY = 5;  // 重连延迟时间，单位秒

    public WebSocketClientImpl(URI serverUri, MessageProcessor processor) {
        super(serverUri);
        this.serverUri = serverUri;
        this.processor = processor;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("[WebSocket] Connected to server");
        reconnecting = false;

        // 连接成功后发送一条消息
        String message = "Connection established successfully!";
        this.send(message);  // 发送消息
    }

    @Override
    public void onMessage(String message) {
        processor.process(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("[WebSocket] Connection closed. Reason: " + reason);
        tryReconnect();
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("[WebSocket] Error occurred: " + ex.getMessage());
        tryReconnect();
    }

    private void tryReconnect() {
        if (reconnecting) return; // 如果正在重连，避免重复重连
        reconnecting = true;
        scheduler.schedule(() -> {
            System.out.println("[WebSocket] Attempting to reconnect...");
            WebSocketClientImpl newClient = new WebSocketClientImpl(serverUri, processor);
            newClient.connect();
        }, RECONNECT_DELAY, TimeUnit.SECONDS);
    }
}
