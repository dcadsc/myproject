需要优化
1.DeduplicationProcessor 中的 history 优化
    HashSet定期清理过时的历史消息，
2.FilterProcessor 中的 JSON 处理优化
    缓存解析后的 JSON 节点，并在需要时复用
    在遍历 dataNode 时，当前是基于索引的方式遍历，可以改为流式处理。
3.KafkaMessageProcessor 异常处理增强
    生产者 send 方法的回调中添加更多的处理逻辑
4.WhitelistImporter 的性能优化
    在导入白名单时是一次性导入，考虑做缓存读取或分页读取
5.WebSocketClientImpl 优化
    重连机制
6.消息处理流程的解耦