//package com.wegame.capi.biz.config;
//
//import com.wegame.capi.biz.domain.biz.BetRecordDo;
//import com.wegame.capi.client.enums.GameTypeEnum;
//import org.apache.pulsar.client.api.Producer;
//import org.apache.pulsar.client.api.PulsarClient;
//import org.apache.pulsar.client.api.PulsarClientException;
//import org.apache.pulsar.client.api.Schema;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class ProducerConfig {
//    //
////    @Value("${spring.application.env}")
////    private String env;
//    @Value("${pulsar.tenant}")
//    private String tenant;
//
//    @Value("${pulsar.namespaces}")
//    private String namespaces;
//
//    @Value("${pulsar.mariamakiling-topic}")
//    private String topic;
//    @Autowired
//    PulsarClient pulsarClient;
////
////    @Bean
////    public Producer<String> mariamakilingProducer() throws PulsarClientException {
////        return pulsarClient.newProducer(Schema.STRING)
////                .topic(GameTypeEnum.MARIA_MAKILING.getName() + "_" + env)
////                .producerName(GameTypeEnum.MARIA_MAKILING.getName())
////                .create();
////    }
//
//
//    // 创建 Producer Bean
//    @Bean
//    public Producer<byte[]> pulsarProducer(PulsarClient pulsarClient) throws PulsarClientException {
//        String fullTopic = String.format("persistent://%s/%s/%s", tenant, namespaces, topic);
//        return pulsarClient.newProducer(Schema.BYTES)
//                .topic(fullTopic)
//                .producerName(GameTypeEnum.MARIA_MAKILING.getName())
//                .create();
//    }
//
//}
