//package com.wegame.capi.biz.config;
//
//=import com.wegame.capi.client.enums.GameTypeEnum;
//import org.apache.pulsar.client.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.concurrent.TimeUnit;
//
//@Configuration
//public class ConsumerConfig {
//
//    @Value("${spring.application.env}")
//    String env;
//    @Autowired
//    PulsarClient pulsarClient;
//
//    @Autowired
//    MariaMakilingMessageListener mariaMakilingMessageListener;
//
////    @Bean
////    public Consumer<String> mariamakilingConsumer() throws PulsarClientException {
////        Consumer<String> consumer = pulsarClient.newConsumer(Schema.STRING)
////                .topic(GameTypeEnum.MARIA_MAKILING.getName() + "_" + env)
////                .subscriptionName(GameTypeEnum.MARIA_MAKILING.getName())
////                // 共享订阅，允许多个消费者共享一个主题
////                .subscriptionType(SubscriptionType.Shared)
//////                // 设置接收队列大小，影响批量消费的性能
//////                .receiverQueueSize(50)
////                .batchReceivePolicy(BatchReceivePolicy.builder()
////                        .maxNumMessages(50) // 一次最多接收10条消息
////                        .timeout(10, TimeUnit.SECONDS) // 等待10秒或达到最大消息数
////                        .build())
////                // 设置批量确认的时间间隔
//////                .acknowledgmentGroupTime(1, TimeUnit.SECONDS)
////                .messageListener(mariaMakilingMessageListener)
////                .subscribe();
//////        consumer.batchReceive()
////        return consumer;
////    }
//
//}
