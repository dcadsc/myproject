package com.example;

import java.util.HashSet;

public class DeduplicationProcessor {

    private HashSet<String> messageCache = new HashSet<>();

    public boolean process(String messageId, String messageContent) {
        // 1. 判断是否已存在
        if (messageCache.contains(messageId)) {
            System.out.println("Duplicate message detected: " + messageId);
            return false; // 去重，丢弃重复消息
        }

        // 2. 添加到 HashSet
        messageCache.add(messageId);

        // 3. 继续正常处理逻辑
        System.out.println("Processing message: " + messageContent);
        return true;
    }
}
