package com.example;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebSocketClientImpl extends WebSocketClient {
    private MessageProcessor processor;
    private URI serverUri;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public WebSocketClientImpl(URI serverUri, MessageProcessor processor) {
        super(serverUri);
        this.serverUri = serverUri;
        this.processor = processor;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("[WebSocket] Connected to server");

        // 连接成功后，发送确认消息
        //String confirmMessage = "Connection established with server";
        //this.send(confirmMessage);
    }

    @Override
    public void onMessage(String message) {
        processor.process(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("[WebSocket] Connection closed. Reason: " + reason);
        scheduleReconnect();
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("[WebSocket] Error occurred: " + ex.getMessage());
        scheduleReconnect();
    }

    private void scheduleReconnect() {
        scheduler.schedule(() -> {
            System.out.println("[WebSocket] Attempting to reconnect...");
            try {
                WebSocketClientImpl newClient = new WebSocketClientImpl(serverUri, processor);
                newClient.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 5, TimeUnit.SECONDS);
    }
}
