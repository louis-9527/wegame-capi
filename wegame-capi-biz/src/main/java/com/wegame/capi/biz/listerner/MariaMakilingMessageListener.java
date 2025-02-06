//package com.wegame.capi.biz.listerner;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.pulsar.client.api.Consumer;
//import org.apache.pulsar.client.api.Message;
//import org.apache.pulsar.client.api.MessageListener;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class MariaMakilingMessageListener implements MessageListener<String> {
//
//    @Override
//    public void received(Consumer<String> consumer, Message<String> msg) {
//        log.info("received a message: {}", msg);
//        // todo do something
//    }
//}
