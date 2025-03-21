package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FilterProcessor implements MessageProcessor {
    private MessageProcessor nextProcessor;
    private List<String> whitelist;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public FilterProcessor(MessageProcessor nextProcessor, List<String> whitelist) {
        this.whitelist = whitelist.stream()
                .map(this::normalize)
                .collect(Collectors.toList());
        this.nextProcessor = nextProcessor;
    }

    @Override
    public void process(String message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);
            JsonNode dataNode = rootNode.get("data");

            if (dataNode != null && dataNode.isArray()) {
                // 分类存储
                List<JsonNode> matchedDps = new ArrayList<>();
                List<JsonNode> unmatchedDps = new ArrayList<>();

                // 第一个 dp 通常是 meta 信息，原样保留
                JsonNode metaNode = dataNode.get(0);

                for (int i = 1; i < dataNode.size(); i++) { // 从第二个 dp 开始
                    JsonNode dpNode = dataNode.get(i);
                    String dpValue = normalize(dpNode.get("dp").asText());
                    if (whitelist.contains(dpValue)) {
                        matchedDps.add(dpNode);
                    } else {
                        unmatchedDps.add(dpNode);
                    }
                }

                // 生成包含 matched dp 的消息
                if (!matchedDps.isEmpty()) {
                    ArrayNode matchedArray = objectMapper.createArrayNode();
                    matchedArray.add(metaNode);
                    matchedDps.forEach(matchedArray::add);

                    ObjectNode matchedRoot = objectMapper.createObjectNode();
                    matchedRoot.set("data", matchedArray);

                    nextProcessor.process(objectMapper.writeValueAsString(matchedRoot));
                }

                // 处理 unmatched dp
                if (!unmatchedDps.isEmpty()) {
                    ArrayNode unmatchedArray = objectMapper.createArrayNode();
                    unmatchedArray.add(metaNode);
                    unmatchedDps.forEach(unmatchedArray::add);

                    ObjectNode unmatchedRoot = objectMapper.createObjectNode();
                    unmatchedRoot.set("data", unmatchedArray);

                    System.out.println("[Filter] Unmatched dp dropped: " + objectMapper.writeValueAsString(unmatchedRoot));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String normalize(String input) {
        if (input == null) return "";
        return input.replaceAll("\\s+", "").trim().toLowerCase();
    }
}
