package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.mq.MQMessage;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.MalakasBetRecordManager;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MalakasPulsarListener extends AbstractPulsarListener<MalakasGameRecordDo> {
    private static final GameTypeEnum gameType = GameTypeEnum.MALAKAS;
    private static final int corePoolSize = 1;
    private static final int maximumPoolSize = 1;
    @Autowired
    BetRecordService betRecordService;
    @Autowired
    MalakasBetRecordManager malakasBetRecordManager;
    @Autowired
    Consumer<byte[]> malakasConsumer;

    @Override
    public Consumer<byte[]> getConsumer() {
        return malakasConsumer;
    }

    @Override
    public GameTypeEnum getGameType() {
        return gameType;
    }

    @Override
    public TypeReference<MQMessage<BetRecordDo<MalakasGameRecordDo>>> getTypeReference() {
        return new TypeReference<MQMessage<BetRecordDo<MalakasGameRecordDo>>>() {
        };
    }

    @Override
    public AbstractBetRecordManager<MalakasGameRecordDo> betRecordManager() {
        return malakasBetRecordManager;
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
