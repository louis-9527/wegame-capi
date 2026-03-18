//package com.wegame.capi.biz.listener.operation;
//
//import com.wegame.capi.client.enums.GameTypeEnum;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.stereotype.Component;
//
//import static com.wegame.capi.common.Constants.MALAKAS_PROP;
//import static com.wegame.capi.common.Constants.TRUE_VAL;
//
//@Component
//@ConditionalOnProperty(name = MALAKAS_PROP, havingValue = TRUE_VAL)
//public class MalakasOperationEventPulsarListener extends AbstractOperationEventPulsarListener {
//    @Override
//    public GameTypeEnum gameType() {
//        return GameTypeEnum.MALAKAS;
//    }
//}
