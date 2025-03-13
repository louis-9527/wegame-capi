package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.mq.MQMessage;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.IbongAdarnaGameRecordDo;
import com.wegame.capi.biz.manager.AbstractRecordManager;
import com.wegame.capi.biz.manager.IbongAdarnaRecordManager;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IbongAdarnaPulsarListener extends AbstractPulsarListener<IbongAdarnaGameRecordDo> {
    @Autowired
    IbongAdarnaRecordManager ibongAdarnaRecordManager;
    @Autowired
    Consumer<byte[]> ibongadarnaConsumer;

    @Override
    public Consumer<byte[]> getConsumer() {
        return ibongadarnaConsumer;
    }

    @Override
    public GameTypeEnum getGameType() {
        return GameTypeEnum.IBONG_ADARNA;
    }

    @Override
    public TypeReference<MQMessage<BetRecordDo<IbongAdarnaGameRecordDo>>> getTypeReference() {
        log.info("IbongAdarnaPulsarListener getTypeReference。。。 ");
        return new TypeReference<MQMessage<BetRecordDo<IbongAdarnaGameRecordDo>>>() {
        };
    }

    @Override
    public AbstractRecordManager<IbongAdarnaGameRecordDo> getRecordManager() {
        return ibongAdarnaRecordManager;
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
