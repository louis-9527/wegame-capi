package com.wegame.capi.biz.listener.betRecord;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.mq.MQMessage;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.KapreGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.SirenaGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.KapreBetRecordManager;
import com.wegame.capi.biz.manager.SirenaBetRecordManager;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.wegame.capi.common.Constants.*;

@Slf4j
@Component
@ConditionalOnProperty(name = SIRENA_PROP, havingValue = TRUE_VAL)
public class SirenaPulsarListener extends AbstractPulsarListener<SirenaGameRecordDo> {
    @Autowired
    SirenaBetRecordManager sirenaBetRecordManager;
    @Autowired
    Consumer<byte[]> sirenaConsumer;

    @Override
    public Consumer<byte[]> getConsumer() {
        return sirenaConsumer;
    }

    @Override
    public GameTypeEnum getGameType() {
        return GameTypeEnum.SIRENA;
    }

    @Override
    public TypeReference<MQMessage<BetRecordDo<SirenaGameRecordDo>>> getTypeReference() {
        log.info("KaprePulsarListener getTypeReference。。。 ");
        return new TypeReference<MQMessage<BetRecordDo<SirenaGameRecordDo>>>() {
        };
    }

    @Override
    public AbstractBetRecordManager<SirenaGameRecordDo> betRecordManager() {
        return sirenaBetRecordManager;
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
