package com.wegame.capi.biz.module.base.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetSingleWalletResultDo;
import com.wegame.capi.biz.listener.PulsarProducer;
import com.wegame.capi.biz.manager.AbstractRecordManager;
import com.wegame.capi.biz.module.base.game.Game;
import com.wegame.capi.biz.service.SingleWalletService;
import com.wegame.capi.client.enums.MessageTypeEnum;
import com.wegame.capi.common.utils.AmountUtil;
import org.apache.pulsar.client.api.MessageId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 抽象投注策略<br>
 * 各游戏共性抽象<br/>
 * 1、获取具体游戏
 * 2、
 */
@Component
public abstract class AbstractBetStrategy<R extends ToVoConverter, BD, RD extends ToVoConverter> implements BetStrategy<BD, RD> {

    private static final int RETRY_TIMES = 3;
    @Autowired
    public SingleWalletService singleWalletService;
    @Autowired
    public SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    private PulsarProducer pulsarProducer;

    public abstract Game<R> getGame();

    public abstract AbstractRecordManager<R> getRecordManager();

    public long generateRecordId() {
        return snowflakeIdGenerator.nextId();
    }

    /**
     * 保存投注记录并投注派彩
     */
    public Long saveBetRecordAndBetWithPayout(BetRecordDo<R> betRecordDo, Boolean syncOpt) {
        if (syncOpt) {
            return syncSaveBetRecordAndBetWithPayout(betRecordDo);
        } else {
            return asyncSaveBetRecordAndBetWithPayout(betRecordDo);
        }
    }

    private Long syncSaveBetRecordAndBetWithPayout(BetRecordDo<R> betRecordDo) {
        Long betRecordId = getRecordManager().add(betRecordDo, getGame().getGameInfo().getGameType());
        AssertUtil.assertNotNull(betRecordId, ErrorCode.ERR_INTER);
        BetSingleWalletResultDo betWithPayoutRes = singleWalletService.betWithPayout(betRecordDo);
        // 判断返回结果
        String betId = betWithPayoutRes.getBetId();
        // 更新记录
        BetRecordDo<R> updateRecord = BetRecordDo.<R>builder()
                .id(betRecordDo.getId())
                .playerId(betRecordDo.getPlayerId())
                .betId(betId)
                .status(betWithPayoutRes.getBetStatus().getCode())
                .build();
        // 异步更新
        getRecordManager().asyncUpdateBetRecord(updateRecord, getGame().getGameInfo().getGameType());
        return AmountUtil.toLong(betWithPayoutRes.getCredit());
    }

    public Long asyncSaveBetRecordAndBetWithPayout(BetRecordDo<R> betRecordDo) {
        // 发送消息(新增)
        MQMessage<BetRecordDo<R>> addMqMessage = MQMessage.buildMsg(String.valueOf(betRecordDo.getId()), MessageTypeEnum.ADD.getCode(), betRecordDo);
        MessageId addMessageId = pulsarProducer.syncSendMessage(getGame().getGameInfo().getGameType(),addMqMessage, RETRY_TIMES);
        AssertUtil.assertNotNull(addMessageId, ErrorCode.ERR_INTER);
        // 调用单钱包
        BetSingleWalletResultDo betWithPayoutRes = singleWalletService.betWithPayout(betRecordDo);
        // 判断返回结果
        String betId = betWithPayoutRes.getBetId();
        // 更新记录
        BetRecordDo<R> updateRecord = BetRecordDo.<R>builder()
                .id(betRecordDo.getId())
                .playerId(betRecordDo.getPlayerId())
                .betId(betId)
                .status(betWithPayoutRes.getBetStatus().getCode())
                .updateTime(DateUtil.getCurrentMilli())
                .build();
        // 发送消息(更新)
        MQMessage<BetRecordDo<R>> updateMqMessage = MQMessage.buildMsg(String.valueOf(betRecordDo.getId()), MessageTypeEnum.UPDATE.getCode(), updateRecord);
        pulsarProducer.syncSendMessage(getGame().getGameInfo().getGameType(),updateMqMessage, RETRY_TIMES);
        return AmountUtil.toLong(betWithPayoutRes.getCredit());
    }
}
