package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class FilterProcessor implements MessageProcessor {
    private MessageProcessor nextProcessor;
    private List<String> whitelist;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public FilterProcessor(MessageProcessor nextProcessor, List<String> whitelist) {
        // 预处理白名单：去空格，忽略大小写（如果需要）
        this.whitelist = whitelist.stream()
                .map(item -> normalize(item))
                .collect(Collectors.toList());
        this.nextProcessor = nextProcessor;
    }

    @Override
    public void process(String message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);
            JsonNode dataNode = rootNode.get("data");

            if (dataNode != null && dataNode.isArray()) {
                boolean matched = false;
                for (int i = 1; i < dataNode.size(); i++) { // 从第二个 dp 开始
                    JsonNode dpNode = dataNode.get(i).get("dp");
                    if (dpNode != null) {
                        String dpValue = normalize(dpNode.asText());
                        System.out.println("[DEBUG] dpNode: '" + dpValue + "'");
                        if (whitelist.contains(dpValue)) {
                            matched = true;
                            break;
                        }
                    }
                }
                if (matched) {
                    nextProcessor.process(message);
                } else {
                    System.out.println("[Filter] All dp filtered out for message: " + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 统一处理方法，去空格、不可见字符、转小写（可选）
    private String normalize(String input) {
        if (input == null) return "";
        return input.replaceAll("\\s+", "") // 去除所有空白字符
                    .trim()
                    .toLowerCase(); // 如果需要忽略大小写就加上
    }
}
