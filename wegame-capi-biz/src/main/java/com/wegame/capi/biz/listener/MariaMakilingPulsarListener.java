package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.mq.MQMessage;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.MariaMakilingBetRecordManager;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MariaMakilingPulsarListener extends AbstractPulsarListener<MariaMakilingGameRecordDo>{
    @Autowired
    BetRecordService betRecordService;
    @Autowired
    MariaMakilingBetRecordManager mariaMakilingBetRecordManager;
    @Autowired
    Consumer<byte[]> mariamakilingConsumer;

    @Override
    public Consumer<byte[]> getConsumer() {
        return mariamakilingConsumer;
    }

    @Override
    public GameTypeEnum getGameType() {
        return GameTypeEnum.MARIA_MAKILING;
    }

    @Override
    public TypeReference<MQMessage<BetRecordDo<MariaMakilingGameRecordDo>>> getTypeReference() {
        return new TypeReference<MQMessage<BetRecordDo<MariaMakilingGameRecordDo>>>() {};
    }

    @Override
    public AbstractBetRecordManager<MariaMakilingGameRecordDo> betRecordManager() {
        return mariaMakilingBetRecordManager;
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
