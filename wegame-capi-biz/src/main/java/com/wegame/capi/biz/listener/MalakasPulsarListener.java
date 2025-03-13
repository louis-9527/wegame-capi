package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.mq.MQMessage;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.manager.AbstractRecordManager;
import com.wegame.capi.biz.manager.MalakasRecordManager;
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
    MalakasRecordManager malakasRecordManager;
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
    public AbstractRecordManager<MalakasGameRecordDo> getRecordManager() {
        return malakasRecordManager;
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
