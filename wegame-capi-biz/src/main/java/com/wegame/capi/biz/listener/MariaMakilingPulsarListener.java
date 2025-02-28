package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.mq.MQMessage;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.manager.AbstractRecordManager;
import com.wegame.capi.biz.manager.MariaMakilingRecordManager;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MariaMakilingPulsarListener extends AbstractPulsarListener<MariaMakilingGameRecordDo>{

    private static final String LISTENER_NAME = "MariaMakilingPulsarListener";
    private static final String POOL_NAME = "mariamakiling-consumber-pool-";
    @Autowired
    BetRecordService betRecordService;
    @Autowired
    MariaMakilingRecordManager mariaMakilingRecordManager;
    @Autowired
    Consumer<byte[]> mariamakilingConsumer;

    @Override
    public String getListenerName() {
        return LISTENER_NAME;
    }

    @Override
    public String getPoolName() {
        return POOL_NAME;
    }

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
    public AbstractRecordManager<MariaMakilingGameRecordDo> getRecordManager() {
        return mariaMakilingRecordManager;
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
