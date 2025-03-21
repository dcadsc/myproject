package com.example;

import java.util.HashSet;
import java.util.Set;

public class DeduplicationProcessor implements MessageProcessor {
    private MessageProcessor next;
    private Set<String> history = new HashSet<>();

    public DeduplicationProcessor(MessageProcessor next) {
        this.next = next;
    }

    @Override
    public void process(String message) {
        if (history.contains(message)) {
            System.out.println("[Dedup] Duplicate message ignored: " + message);
        } else {
            history.add(message);
            next.process(message);
        }
    }
}