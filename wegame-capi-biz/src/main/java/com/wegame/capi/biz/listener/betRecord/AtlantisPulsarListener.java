package com.wegame.capi.biz.listener.betRecord;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.mq.MQMessage;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.AtlantisGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.AtlantisBetRecordManager;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.wegame.capi.common.Constants.*;

@Slf4j
@Component
@ConditionalOnProperty(name = ATLANTIS_PROP, havingValue = TRUE_VAL)
public class AtlantisPulsarListener extends AbstractPulsarListener<AtlantisGameRecordDo> {
    @Autowired
    AtlantisBetRecordManager atlantisBetRecordManager;
    @Autowired
    Consumer<byte[]> atlantisConsumer;

    @Override
    public Consumer<byte[]> getConsumer() {
        return atlantisConsumer;
    }

    @Override
    public GameTypeEnum getGameType() {
        return GameTypeEnum.ATLANTIS;
    }

    @Override
    public TypeReference<MQMessage<BetRecordDo<AtlantisGameRecordDo>>> getTypeReference() {
        log.info("AtlantisPulsarListener getTypeReference。。。 ");
        return new TypeReference<MQMessage<BetRecordDo<AtlantisGameRecordDo>>>() {
        };
    }

    @Override
    public AbstractBetRecordManager<AtlantisGameRecordDo> betRecordManager() {
        return atlantisBetRecordManager;
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
