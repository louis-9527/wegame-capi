package com.wegame.capi.biz.listener;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.api.*;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public abstract class AbstractPulsarListener{
    @Autowired
    Map<String, Consumer<byte[]>> consumerCache;




    public void consumerStart(GameTypeEnum gameType) {
        Consumer<byte[]> consumer = this.getConSumer(gameType);
        new Thread(() -> consumeMessages(consumer)).start();
        log.info("✅ Pulsar consumer started for topic: {}", consumer.getTopic());
    }


    private void consumeMessages(Consumer<byte[]> consumer) {
        while (true) {
            try {
                MDC.put(ThreadMdcUtil.TRACE_ID, UUID.randomUUID().toString());
                Messages<byte[]> messages = consumer.batchReceive();
                if (messages == null || messages.size() == 0) {
                    continue;
                }
                List<MessageId> successMsgIds = processMessages(messages);
                if (!successMsgIds.isEmpty()) {
                    consumer.acknowledge(successMsgIds);
                }
            } catch (Exception e) {
                log.error("Error consuming messages:", e);
            } finally {
                MDC.clear();
            }
        }
    }
    /**
     * 处理接收到的消息，子类必须实现
     *
     * @param messages 批量消息
     * @return 处理成功的消息ID列表
     */
    protected abstract List<MessageId> processMessages(Messages<byte[]> messages);

    @PostConstruct
    public abstract void init();


    private Consumer<byte[]> getConSumer(GameTypeEnum gameType){
        Consumer<byte[]> consumer = consumerCache.get(StringUtils.lowerCase(gameType.getName()) + "Consumer");
        AssertUtil.assertNotNull(consumer, ErrorCode.ERR_INTER);
        return consumer;
    }
}
