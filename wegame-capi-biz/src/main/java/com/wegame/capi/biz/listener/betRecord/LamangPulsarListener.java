package com.wegame.capi.biz.listener.betRecord;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.mq.MQMessage;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.LamangGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.LamangBetRecordManager;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.wegame.capi.common.Constants.*;

@Slf4j
@Component
@ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
public class LamangPulsarListener extends AbstractPulsarListener<LamangGameRecordDo> {
    @Autowired
    LamangBetRecordManager lamangBetRecordManager;
    @Autowired
    Consumer<byte[]> lamangConsumer;

    @Override
    public Consumer<byte[]> getConsumer() {
        return lamangConsumer;
    }

    @Override
    public GameTypeEnum getGameType() {
        return GameTypeEnum.LAM_ANG;
    }

    @Override
    public TypeReference<MQMessage<BetRecordDo<LamangGameRecordDo>>> getTypeReference() {
        return new TypeReference<MQMessage<BetRecordDo<LamangGameRecordDo>>>() {
        };
    }

    @Override
    public AbstractBetRecordManager<LamangGameRecordDo> betRecordManager() {
        return lamangBetRecordManager;
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
