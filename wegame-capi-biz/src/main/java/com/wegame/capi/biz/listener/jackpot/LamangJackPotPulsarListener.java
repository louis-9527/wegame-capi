package com.wegame.capi.biz.listener.jackpot;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.mq.MQMessage;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.IbongAdarnaGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.LamangGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.jackpot.AbstractJackpotManager;
import com.wegame.capi.biz.manager.jackpot.LamangJackpotManager;
import com.wegame.capi.client.enums.GameTypeEnum;
import org.apache.pulsar.client.api.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import static com.wegame.capi.common.Constants.*;

@ConditionalOnProperty(name = LAMANG_ADARNA_PROP, havingValue = TRUE_VAL)
@Service
public class LamangJackPotPulsarListener extends AbstractJackPotPulsarListener<LamangGameRecordDo>{
    @Autowired
    private Consumer<byte[]> lamangJackPotPulsarConsumer;
    @Autowired
    private LamangJackpotManager lamangJackpotManager;

    private static final int corePoolSize = 1;
    private static final int maximumPoolSize = 1;
    @Override
    public Consumer<byte[]> getConsumer() {
        return lamangJackPotPulsarConsumer;
    }

    @Override
    public GameTypeEnum getGameType() {
        return GameTypeEnum.LAM_ANG;
    }

    @Override
    public AbstractJackpotManager jackpotManager() {
        return lamangJackpotManager;
    }

    @Override
    public TypeReference<MQMessage<BetRecordDo<LamangGameRecordDo>>> getTypeReference() {
        return new TypeReference<MQMessage<BetRecordDo<LamangGameRecordDo>>>() {
        };
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
