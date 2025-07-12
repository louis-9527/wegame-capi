package com.wegame.capi.biz.listener.betRecord;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.mq.MQMessage;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.MangkukulamBetRecordManager;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.wegame.capi.common.Constants.MANGKUKULAM_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Slf4j
@Component
@ConditionalOnProperty(name = MANGKUKULAM_PROP, havingValue = TRUE_VAL)
public class MangkukulamPulsarListener extends AbstractPulsarListener<MangkukulamGameRecordDo> {
    @Autowired
    MangkukulamBetRecordManager mangkukulamBetRecordManager;
    @Autowired
    Consumer<byte[]> mangkukulamConsumer;

    @Override
    public Consumer<byte[]> getConsumer() {
        return mangkukulamConsumer;
    }

    @Override
    public GameTypeEnum getGameType() {
        return GameTypeEnum.MANGKUKULAM;
    }

    @Override
    public TypeReference<MQMessage<BetRecordDo<MangkukulamGameRecordDo>>> getTypeReference() {
        return new TypeReference<MQMessage<BetRecordDo<MangkukulamGameRecordDo>>>() {
        };
    }

    @Override
    public AbstractBetRecordManager<MangkukulamGameRecordDo> betRecordManager() {
        return mangkukulamBetRecordManager;
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
