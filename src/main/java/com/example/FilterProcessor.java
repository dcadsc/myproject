package com.example;

import java.io.IOException;
import java.util.List;

public class FilterProcessor implements MessageProcessor {
    private MessageProcessor next;
    private List<String> whitelist;

    public FilterProcessor(MessageProcessor next) throws IOException {
        this.next = next;
        WhitelistImporter importer = new WhitelistImporter();
        this.whitelist = importer.importWhitelist("/opt/whitelist.xlsx"); // 修改成实际路径
    }

    @Override
    public void process(String message) {
        if (whitelist.contains(message)) {
            System.out.println("[Filter] Whitelisted message: " + message);
            next.process(message);
        } else {
            System.out.println("[Filter] Message filtered: " + message);
        }
    }
}