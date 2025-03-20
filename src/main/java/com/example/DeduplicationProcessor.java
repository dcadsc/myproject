package com.example;

import java.util.HashSet;

public class DeduplicationProcessor extends MessageProcessorDecorator {
    private HashSet<Integer> messageCache = new HashSet<>(); // 使用 HashSet 存储哈希值

    public DeduplicationProcessor(MessageProcessor nextProcessor) {
        super(nextProcessor); // 传入下一个处理器
    }

    @Override
    public void process(String message) {
        // 对整个消息内容进行哈希计算
        int messageHash = message.hashCode(); // 使用消息的 hashCode() 方法来生成唯一的哈希值

        // 判断是否已存在该哈希值
        if (messageCache.contains(messageHash)) {
            System.out.println("Duplicate message detected: " + message);
            return; // 去重，丢弃重复消息
        }

        // 将哈希值添加到 HashSet 中
        messageCache.add(messageHash);

        // 继续正常处理逻辑
        System.out.println("Processing message: " + message);

        // 调用下一个处理器
        if (nextProcessor != null) {
            nextProcessor.process(message);
        }
    }
}