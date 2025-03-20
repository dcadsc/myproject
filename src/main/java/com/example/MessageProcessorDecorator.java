package com.example;

public abstract class MessageProcessorDecorator implements MessageProcessor {
    protected MessageProcessor nextProcessor;

    public MessageProcessorDecorator(MessageProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }
}
