//package com.wegame.capi.biz.listener;
//
//import cn.hutool.core.lang.UUID;
//import com.wegame.base.executor.WsThreadPoolExecutor;
//import com.wegame.base.utils.ThreadMdcUtil;
//import com.wegame.capi.client.enums.GameTypeEnum;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.pulsar.client.api.*;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@Component
//public class MariaMakilingPulsarListener implements Runnable {
//    //    //从配置文件中获取 serviceURL 接入地址、Token 密钥、Topic 全名和 Subscription 名称（均可从控制台复制）
//    //    @Value("${tdmq.serviceUrl}")
//    //    private String serviceUrl;
//    //    @Value("${tdmq.token}")
//    //    private String token;
//    //    @Value("${tdmq.topic}")
//    //    private String topic;
//    //    @Value("${tdmq.subscription}")
//    //    private String subscription;
//    //    private PulsarClient pulsarClient;
//    //    private Consumer<String> consumer;
//
//    private static final int corePoolSize = 10;
//    private static final int maximumPoolSize = 10;
//    private volatile boolean start = false;
//
//    @Value("${spring.application.env}")
//    String env;
//    @Value("${spring.application.name}")
//    String name;
//    @Autowired
//    PulsarClient pulsarClient;
//    Consumer<byte[]> consumer;
//    ExecutorService executor;
//    GameTypeEnum gameType = GameTypeEnum.MARIA_MAKILING;
//
//    @PostConstruct
//    public void init() throws Exception {
//        String fullTopic = String.format("persistent://%s/%s/%s", name + "_" + gameType.getName(), env, gameType.getName() + "_topic");
//        String deadLetterTopic = String.format("persistent://%s/%s/%s", name + "_" + gameType.getName(), env, gameType.getName() + "_deadLetter" + "_topic");
//        consumer = pulsarClient.newConsumer()
//                .topic(fullTopic)
//                .subscriptionName(name + "_" + gameType.getName())
//                // 关键点：使用 Key_Shared 模式
//                .subscriptionType(SubscriptionType.Key_Shared)
//                .enableRetry(true)
//                // 增加内部缓存，提高吞吐量
//                .receiverQueueSize(2000)
//                .ackTimeout(60, TimeUnit.SECONDS)
//                .batchReceivePolicy(BatchReceivePolicy.builder()
//                        // 批量最多100条
//                        .maxNumMessages(200)
//                        .timeout(100, TimeUnit.MILLISECONDS)
//                        .build())
//                .deadLetterPolicy(DeadLetterPolicy.builder()
//                        // 最多重试 10 次
//                        .maxRedeliverCount(15)  // **最多重试 10 次**
//                        .deadLetterTopic(deadLetterTopic)
//                        .build())
//                .negativeAckRedeliveryDelay(1, TimeUnit.MINUTES)
//                // .keySharedPolicy(KeySharedPolicy.stickyHashRange())
//                .subscribe();
//        executor = new WsThreadPoolExecutor(corePoolSize,
//                maximumPoolSize,
//                0,
//                TimeUnit.SECONDS,
//                new ArrayBlockingQueue<>(100),
//                new ThreadPoolExecutor.AbortPolicy());
//        start = true;
//    }
//
//    @PreDestroy
//    public void destroy() throws Exception {
//        start = false;
//        if (consumer != null) {
//            consumer.close();
//        }
//        if (pulsarClient != null) {
//            pulsarClient.close();
//        }
//        if (executor != null) {
//            executor.shutdownNow();
//        }
//    }
//
//    @Override
//    public void run() {
//        log.info("MariaMakilingPulsarListener started...");
//        for (int i = 0; i < maximumPoolSize; i++) {
//            executor.submit(() -> {
//                while (start) {
//                    try {
//                        MDC.put(ThreadMdcUtil.TRACE_ID, UUID.randomUUID().toString(true));
//                        Messages<byte[]> messages = consumer.batchReceive();
//                        if (messages == null || messages.size() == 0) {
//                            continue;
//                        }
//                        onConsumer(message);
//                    } catch (Exception e) {
//                        log.warn("tdmq consumer business error", e);
//                    }
//                }
//            });
//        }
//        log.info("tdmq consumer stopped...");
//    }
//
//    private void onConsumer(Message<String> message) {
//        //        //业务逻辑,延时类操作
//        //        try {
//        //            System.out.println(Thread.currentThread().getName() + " - message receive: " + message.getValue());
//        //            Thread.sleep(1000);//模拟业务逻辑处理
//        //            consumer.acknowledge(message);
//        //            log.info(Thread.currentThread().getName() + " - message processing succeed：" + message.getValue());
//        //        } catch (Exception exception) {
//        //            consumer.negativeAcknowledge(message);
//        //            log.error(Thread.currentThread().getName() + " - message processing failed：" + message.getValue());
//        //        }
//    }
//}
