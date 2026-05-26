package com.wegame.capi.biz.listener.betRecord;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.mq.MQMessage;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.BayanihanGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.BayanihanBetRecordManager;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.wegame.capi.common.Constants.BAYANIHAN_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Slf4j
@Component
@ConditionalOnProperty(name = BAYANIHAN_PROP, havingValue = TRUE_VAL)
public class BayanihanPulsarListener extends AbstractPulsarListener<BayanihanGameRecordDo> {
    @Autowired
    BayanihanBetRecordManager bayanihanBetRecordManager;
    @Autowired
    Consumer<byte[]> bayanihanConsumer;

    @Override
    public Consumer<byte[]> getConsumer() {
        return bayanihanConsumer;
    }

    @Override
    public GameTypeEnum getGameType() {
        return GameTypeEnum.BAYANIHAN;
    }

    @Override
    public TypeReference<MQMessage<BetRecordDo<BayanihanGameRecordDo>>> getTypeReference() {
//        log.info("BayanihanPulsarListener getTypeReference。。。 ");
        return new TypeReference<MQMessage<BetRecordDo<BayanihanGameRecordDo>>>() {
        };
    }

    @Override
    public AbstractBetRecordManager<BayanihanGameRecordDo> betRecordManager() {
        return bayanihanBetRecordManager;
    }

    @Override
    public int getCorePoolSize() {
        return 1;
    }

    @Override
    public int getMaximumPoolSize() {
        return 1;
    }
}
