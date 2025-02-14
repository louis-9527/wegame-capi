package com.wegame.capi.biz.module.base.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
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
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.utils.AmountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.MessageId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 抽象投注策略<br>
 * 各游戏共性抽象<br/>
 * 1、获取具体游戏
 * 2、
 */
@Slf4j
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

    public long generateBetRecordId() {
        long betRecordId = snowflakeIdGenerator.nextId();
        log.info("generateBetRecordId: {}", betRecordId);
        return betRecordId;
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
        BetSingleWalletResultDo betSingleWalletResultDo = singleWalletService.betWithPayout(betRecordDo);
        // 判断返回结果
        String betId = betSingleWalletResultDo.getBetId();
        if (Objects.equals(betSingleWalletResultDo.getBetStatus(), BetStatusEnum.SUCCESS)) {
            // 更新记录
            BetRecordDo<R> updateRecord = BetRecordDo.<R>builder()
                    .id(betRecordDo.getId())
                    .playerId(betRecordDo.getPlayerId())
                    .betId(betId)
                    .status(betSingleWalletResultDo.getBetStatus().getCode())
                    .build();
            // 异步更新
            getRecordManager().asyncUpdateBetRecord(updateRecord, getGame().getGameInfo().getGameType());
            return AmountUtil.toLong(betSingleWalletResultDo.getCredit());
        } else {
            throw new BusinessException(ErrorCode.ERR_INTER, betSingleWalletResultDo.getReason());
        }
    }

    public Long asyncSaveBetRecordAndBetWithPayout(BetRecordDo<R> betRecordDo) {
        long startTime = System.currentTimeMillis();
        // 发送消息(新增)
        MQMessage<BetRecordDo<R>> addMqMessage = MQMessage.buildMsg(String.valueOf(betRecordDo.getId()), MessageTypeEnum.ADD.getCode(), betRecordDo);
        //        MessageId addMessageId = pulsarProducer.syncSendMessage(getGame().getGameInfo().getGameType(),addMqMessage, RETRY_TIMES);
        //        AssertUtil.assertNotNull(addMessageId, ErrorCode.ERR_INTER);
        CompletableFuture<MessageId> future = pulsarProducer.asyncSendMessage(getGame().getGameInfo().getGameType(), addMqMessage);
        // 调用单钱包
        BetSingleWalletResultDo betSingleWalletResultDo = singleWalletService.betWithPayout(betRecordDo);
        // 判断返回结果
        String betId = betSingleWalletResultDo.getBetId();
        if (Objects.equals(betSingleWalletResultDo.getBetStatus(), BetStatusEnum.SUCCESS)) {
            MessageId messageId;
            try {
                long startTime2 = System.currentTimeMillis();
                messageId = future.get();
                long endTime2 = System.currentTimeMillis();
                log.info("asyncSendMessage get cost time: {}", endTime2 - startTime2);
            } catch (Exception e) {
                log.error("asyncSendMessage send fail, to syncSendMessage, betRecordId: {}", betRecordDo.getId());
                messageId = pulsarProducer.syncSendMessage(getGame().getGameInfo().getGameType(), addMqMessage);
            }
            if (Objects.isNull(messageId)) {
                log.error("syncSendMessage send fail, sync add data, betRecordId: {}", betRecordDo.getId());
                // 新增消息发送失败，则转同步写db
                getRecordManager().add(betRecordDo, getGame().getGameInfo().getGameType());
            }
            // 更新记录
            BetRecordDo<R> updateRecord = BetRecordDo.<R>builder()
                    .id(betRecordDo.getId())
                    .playerId(betRecordDo.getPlayerId())
                    .betId(betId)
                    .status(betSingleWalletResultDo.getBetStatus().getCode())
                    .updateTime(DateUtil.getCurrentMilli())
                    .build();
            // 异步发送更新消息
            MQMessage<BetRecordDo<R>> updateMqMessage = MQMessage.buildMsg(String.valueOf(betRecordDo.getId()), MessageTypeEnum.UPDATE.getCode(), updateRecord);
            // todo 添加失败重试机制
            pulsarProducer.asyncSendMessage(getGame().getGameInfo().getGameType(), updateMqMessage);
            long endTime = System.currentTimeMillis();
            log.info("asyncSaveBetRecordAndBetWithPayout cost time: {} ms", endTime - startTime);
            return AmountUtil.toLong(betSingleWalletResultDo.getCredit());
        } else {
            throw new BusinessException(ErrorCode.ERR_INTER, betSingleWalletResultDo.getReason());
        }
    }
}
