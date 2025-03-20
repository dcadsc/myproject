package com.example;

public class FilterProcessor extends MessageProcessorDecorator {
    public FilterProcessor(MessageProcessor nextProcessor) {
        super(nextProcessor);
    }

    @Override
    public void process(String message) {
        if (message.contains("filter")) {
            System.out.println("[FilterProcessor] Message filtered: " + message);
        } else {
            System.out.println("[FilterProcessor] Message passed filter: " + message);
            nextProcessor.process(message);
        }
    }
}
