package com.example;

import javax.websocket.*;
import java.net.URI;
import java.util.List;

@ClientEndpoint
public class TyrusWebSocketClient {
    private final MessageProcessor processor;
    private Session session;
    private final List<URI> serverUris;
    private int currentServerIndex = 0; // 记录当前连接的 WebSocket 地址索引
    private static final int RECONNECT_DELAY = 5000; // 5秒后重连
    private boolean reconnecting = false; // 防止重复重连

    public TyrusWebSocketClient(List<URI> serverUris, MessageProcessor processor) {
        this.serverUris = serverUris;
        this.processor = processor;
    }

    public void connect() throws Exception {
        if (serverUris.isEmpty()) {
            throw new IllegalStateException("No WebSocket server URIs provided");
        }

        attemptConnection();
    }

    private void attemptConnection() throws Exception {
        URI serverUri = serverUris.get(currentServerIndex);
        System.out.println("[WebSocket] Connecting to: " + serverUri);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, serverUri);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("[WebSocket] Connected to: " + serverUris.get(currentServerIndex));
        reconnecting = false; // 连接成功，停止重连
        session.getAsyncRemote().sendText("Connection established successfully");
    }

    @OnMessage
    public void onMessage(String message) {
        processor.process(message);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("[WebSocket] Connection closed. Reason: " + closeReason);
        attemptReconnect();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("[WebSocket] Error occurred: " + throwable.getMessage());
        attemptReconnect();
    }

    // 断线自动切换到下一个地址
    private void attemptReconnect() {
        if (!reconnecting) {
            reconnecting = true;
            System.out.println("[WebSocket] Attempting to reconnect...");

            new Thread(() -> {
                try {
                    Thread.sleep(RECONNECT_DELAY); // 5秒后重连

                    // 切换到下一个服务器地址
                    currentServerIndex = (currentServerIndex + 1) % serverUris.size();
                    System.out.println("[WebSocket] Switching to: " + serverUris.get(currentServerIndex));

                    attemptConnection();
                } catch (Exception e) {
                    System.out.println("[WebSocket] Reconnection failed: " + e.getMessage());
                    reconnecting = false;
                    attemptReconnect(); // 继续尝试
                }
            }).start();
        }
    }
}
