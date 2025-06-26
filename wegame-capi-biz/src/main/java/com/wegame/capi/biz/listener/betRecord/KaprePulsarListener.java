package com.wegame.capi.biz.listener.betRecord;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.mq.MQMessage;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.KapreGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.KapreBetRecordManager;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.wegame.capi.common.Constants.KAPRE_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Slf4j
@Component
@ConditionalOnProperty(name = KAPRE_PROP, havingValue = TRUE_VAL)
public class KaprePulsarListener extends AbstractPulsarListener<KapreGameRecordDo> {
    @Autowired
    KapreBetRecordManager kapreBetRecordManager;
    @Autowired
    Consumer<byte[]> kapreConsumer;

    @Override
    public Consumer<byte[]> getConsumer() {
        return kapreConsumer;
    }

    @Override
    public GameTypeEnum getGameType() {
        return GameTypeEnum.KAPRE;
    }

    @Override
    public TypeReference<MQMessage<BetRecordDo<KapreGameRecordDo>>> getTypeReference() {
        log.info("KaprePulsarListener getTypeReference。。。 ");
        return new TypeReference<MQMessage<BetRecordDo<KapreGameRecordDo>>>() {
        };
    }

    @Override
    public AbstractBetRecordManager<KapreGameRecordDo> betRecordManager() {
        return kapreBetRecordManager;
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
