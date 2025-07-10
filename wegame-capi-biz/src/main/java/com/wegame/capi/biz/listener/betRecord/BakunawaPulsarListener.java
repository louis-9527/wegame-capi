package com.wegame.capi.biz.listener.betRecord;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.mq.MQMessage;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.BakunawaGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.BakunawaBetRecordManager;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.wegame.capi.common.Constants.BAKUNAWA_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Slf4j
@Component
@ConditionalOnProperty(name = BAKUNAWA_PROP, havingValue = TRUE_VAL)
public class BakunawaPulsarListener extends AbstractPulsarListener<BakunawaGameRecordDo> {
    @Autowired
    BakunawaBetRecordManager bakunawaBetRecordManager;
    @Autowired
    Consumer<byte[]> bakunawaConsumer;

    @Override
    public Consumer<byte[]> getConsumer() {
        return bakunawaConsumer;
    }

    @Override
    public GameTypeEnum getGameType() {
        return GameTypeEnum.BAKUNAWA;
    }

    @Override
    public TypeReference<MQMessage<BetRecordDo<BakunawaGameRecordDo>>> getTypeReference() {
        return new TypeReference<MQMessage<BetRecordDo<BakunawaGameRecordDo>>>() {
        };
    }

    @Override
    public AbstractBetRecordManager<BakunawaGameRecordDo> betRecordManager() {
        return bakunawaBetRecordManager;
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
