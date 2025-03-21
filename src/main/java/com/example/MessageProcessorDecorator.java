package com.example;

public abstract class MessageProcessorDecorator implements MessageProcessor {
    protected MessageProcessor next;

    public MessageProcessorDecorator(MessageProcessor next) {
        this.next = next;
    }
}