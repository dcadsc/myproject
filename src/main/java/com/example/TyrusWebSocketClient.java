package com.example;

import javax.websocket.*;
import java.net.URI;


@ClientEndpoint
public class TyrusWebSocketClient {
    private final MessageProcessor processor;
    private Session session;
    private final URI serverUri;
    private static final int RECONNECT_DELAY = 5000; // 5秒后重连
    private boolean reconnecting = false; // 防止重复重连

    public TyrusWebSocketClient(URI serverUri, MessageProcessor processor) {
        this.serverUri = serverUri;
        this.processor = processor;
    }

    public void connect() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, serverUri);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("[WebSocket] Connected to server");
        // 连接成功后发送一条消息
        session.getAsyncRemote().sendText("Connection established successfully");
    }

    @OnMessage
    public void onMessage(String message) {
        processor.process(message);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("[WebSocket] Connection closed. Reason: " + closeReason);
        // 如果连接关闭，尝试重连
        attemptReconnect();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("[WebSocket] Error occurred: " + throwable.getMessage());
        // 如果发生错误，尝试重连
        attemptReconnect();
    }

    // 尝试重连的方法
    private void attemptReconnect() {
        if (!reconnecting) {
            reconnecting = true;
            System.out.println("[WebSocket] Attempting to reconnect...");

            // 创建一个新的线程进行重连操作
            new Thread(() -> {
                try {
                    Thread.sleep(RECONNECT_DELAY); // 延迟重连，避免频繁重试
                    connect(); // 尝试重新连接
                    reconnecting = false; // 重连成功，标记为不再重连
                    System.out.println("[WebSocket] Reconnected to server");
                } catch (Exception e) {
                    System.out.println("[WebSocket] Reconnection failed: " + e.getMessage());
                    reconnecting = false;
                    attemptReconnect(); // 如果重连失败，继续尝试
                }
            }).start();
        }
    }
}
