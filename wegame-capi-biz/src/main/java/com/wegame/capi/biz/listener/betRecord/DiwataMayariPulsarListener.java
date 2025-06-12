package com.wegame.capi.biz.listener.betRecord;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.mq.MQMessage;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.DiwataMayariGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.DiwataMayariBetRecordManager;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.wegame.capi.common.Constants.*;

@Slf4j
@Component
@ConditionalOnProperty(name = DIWATA_MAYARI_PROP, havingValue = TRUE_VAL)
public class DiwataMayariPulsarListener extends AbstractPulsarListener<DiwataMayariGameRecordDo> {
    private static final GameTypeEnum gameType = GameTypeEnum.DIWATA_MAYARI;
    private static final int corePoolSize = 1;
    private static final int maximumPoolSize = 1;
    @Autowired
    DiwataMayariBetRecordManager diwataMayariBetRecordManager;
    @Autowired
    Consumer<byte[]> diwataMayariConsumer;

    @Override
    public Consumer<byte[]> getConsumer() {
        return diwataMayariConsumer;
    }

    @Override
    public GameTypeEnum getGameType() {
        return gameType;
    }

    @Override
    public TypeReference<MQMessage<BetRecordDo<DiwataMayariGameRecordDo>>> getTypeReference() {
        return new TypeReference<MQMessage<BetRecordDo<DiwataMayariGameRecordDo>>>() {
        };
    }

    @Override
    public AbstractBetRecordManager<DiwataMayariGameRecordDo> betRecordManager() {
        return diwataMayariBetRecordManager;
    }

    @Override
    public int getCorePoolSize() {
        return corePoolSize;
    }

    @Override
    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }
}
