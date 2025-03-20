package com.example;

import java.util.HashSet;
import java.util.Set;

public class DeduplicationProcessor extends MessageProcessorDecorator {
    private Set<String> cache = new HashSet<>();

    public DeduplicationProcessor(MessageProcessor nextProcessor) {
        super(nextProcessor);
    }

    @Override
    public void process(String message) {
        if (!cache.contains(message)) {
            cache.add(message);
            System.out.println("[DeduplicationProcessor] Message passed deduplication: " + message);
            nextProcessor.process(message);
        } else {
            System.out.println("[DeduplicationProcessor] Duplicate message ignored: " + message);
        }
    }
}
