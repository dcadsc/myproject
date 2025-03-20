package com.example;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebSocketClientImpl extends WebSocketClient {
    private MessageProcessor processor;

    public WebSocketClientImpl(URI serverUri, MessageProcessor processor) {
        super(serverUri);
        this.processor = processor;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("[WebSocket] Connected to " + getURI());
    }

    @Override
    public void onMessage(String message) {
        System.out.println("[WebSocket] Received: " + message);
        processor.process(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("[WebSocket] Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("[WebSocket] Error: " + ex.getMessage());
    }
}
