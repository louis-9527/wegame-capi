package com.wegame.capi.biz.module.base.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.listener.PulsarProducer;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import com.wegame.capi.biz.service.SingleWalletService;
import com.wegame.capi.biz.service.promotion.FreeSpinService;
import com.wegame.capi.client.enums.MessageTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
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
public abstract class AbstractBetStrategy<R extends ToVoConverter, RD extends ToVoConverter>
        implements BetStrategy<RD> {
    @Autowired
    public SingleWalletService singleWalletService;
    @Autowired
    public SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    public PulsarProducer pulsarProducer;
    @Autowired
    public FreeSpinService freeSpinService;

    public abstract AbstractGameContext<R> gameContext();

    public abstract AbstractBetRecordManager<R> betRecordManager();

    public long generateId() {
        return snowflakeIdGenerator.nextId();
    }

    public long generateBetRecordId() {
        long betRecordId = snowflakeIdGenerator.nextId();
        return betRecordId;
    }

    /**
     * 保存投注记录并投注派彩
     */
    public SingleWalletBetResultDo saveBetRecordAndBetWithPayout(BetRecordDo<R> betRecordDo, Boolean syncOpt) {
        //        if (syncOpt) {
        //            return syncSaveBetRecordAndBetWithPayout(betRecordDo);
        //        } else {
        //            return asyncSaveBetRecordAndBetWithPayout(betRecordDo);
        //        }
        return asyncSaveBetRecordAndBetWithPayout(betRecordDo, syncOpt);
    }

    private SingleWalletBetResultDo syncSaveBetRecordAndBetWithPayout(BetRecordDo<R> betRecordDo) {
        Long betRecordId = betRecordManager().add(betRecordDo, gameContext().gameInfo().getGameType());
        AssertUtil.assertNotNull(betRecordId, ErrorCode.ERR_INTER);
        SingleWalletBetResultDo betSingleWalletResult = singleWalletService.betWithPayout(betRecordDo);
        if (Objects.equals(betSingleWalletResult.getBetStatus(), BetStatusEnum.SUCCESS)) {
            // 异步更新：更新为->投注成功
            betRecordManager().asyncUpdateBetRecord(buildUpdateRecord(betRecordDo, BetStatusEnum.SUCCESS, betSingleWalletResult),
                    gameContext().gameInfo().getGameType());
        } else if (Objects.equals(betSingleWalletResult.getBetStatus(), BetStatusEnum.FAIL)) {
            log.warn("syncSaveBetRecordAndBetWithPayout bet fail, betRecordDo: {}, betSingleWalletResult: {}", betRecordDo, betSingleWalletResult);
            // 异步更新：更新为->投注失败
            betRecordManager().asyncUpdateBetRecord(buildUpdateRecord(betRecordDo, BetStatusEnum.FAIL, betSingleWalletResult),
                    gameContext().gameInfo().getGameType());
        }
        return betSingleWalletResult;
    }

    public SingleWalletBetResultDo asyncSaveBetRecordAndBetWithPayout(BetRecordDo<R> betRecordDo, boolean syncOpt) {
        // 发送消息(新增)
        MQMessage<BetRecordDo<R>> addMqMessage = MQMessage.buildMsg(String.valueOf(betRecordDo.getId()), MessageTypeEnum.ADD.getCode(), betRecordDo);
        //        MessageId addMessageId = pulsarProducer.syncSendMessage(getGameContext().getGameInfo().getGameType(),addMqMessage, RETRY_TIMES);
        //        AssertUtil.assertNotNull(addMessageId, ErrorCode.ERR_INTER);
        CompletableFuture<MessageId> future = pulsarProducer.asyncSendMessage(gameContext().gameInfo().getGameType(), addMqMessage);
        SingleWalletBetResultDo betSingleWalletResult = singleWalletService.betWithPayout(betRecordDo);
        if (Objects.equals(betSingleWalletResult.getBetStatus(), BetStatusEnum.SUCCESS)) {
            MessageId messageId;
            try {
                messageId = future.get();
            } catch (Exception e) {
                log.error("asyncSendMessage send fail, to syncSendMessage, betRecordId: {}", betRecordDo.getId());
                messageId = pulsarProducer.syncSendMessage(gameContext().gameInfo().getGameType(), addMqMessage);
            }
            if (Objects.isNull(messageId)) {
                log.error("syncSendMessage send fail, sync add data, betRecordId: {}", betRecordDo.getId());
                // 新增消息发送失败，则转同步写db
                betRecordManager().add(betRecordDo, gameContext().gameInfo().getGameType());
            }
            if (syncOpt) {
                // 同步设置游戏记录缓存
                String cacheKey = String.format("%s_%s", betRecordDo.getId(), betRecordDo.getPlayerId());
                betRecordManager().putGameRecordCache(betRecordDo.getGameRecordList(), gameContext().gameInfo().getGameType(), cacheKey);
            }
            // 异步发送更新消息: 更新为投注成功
            pulsarProducer.asyncSendMessageThenRetry(gameContext().gameInfo().getGameType(),
                    buildUpdateMessage(betRecordDo, BetStatusEnum.SUCCESS, betSingleWalletResult));
            return betSingleWalletResult;
        } else if (Objects.equals(betSingleWalletResult.getBetStatus(), BetStatusEnum.FAIL)) {
            log.warn("asyncSaveBetRecordAndBetWithPayout bet fail, betRecordDo: {}, betSingleWalletResult: {}", betRecordDo, betSingleWalletResult);
            // 异步发送更新消息: 更新为投注成功
            pulsarProducer.asyncSendMessageThenRetry(gameContext().gameInfo().getGameType(),
                    buildUpdateMessage(betRecordDo, BetStatusEnum.FAIL, betSingleWalletResult));
        }
        return betSingleWalletResult;
    }

    private MQMessage<BetRecordDo> buildUpdateMessage(BetRecordDo<R> betRecordDo, BetStatusEnum status, SingleWalletBetResultDo betSingleWalletResult) {
        return MQMessage.buildMsg(String.valueOf(betRecordDo.getId()),
                MessageTypeEnum.UPDATE.getCode(),
                BetRecordDo.<R>builder()
                        .id(betRecordDo.getId())
                        .betId(Objects.equals(BetStatusEnum.SUCCESS, status) ? betSingleWalletResult.getBetId() : null)
                        .status(status.getCode())
                        .build());
    }

    private BetRecordDo<R> buildUpdateRecord(BetRecordDo<R> betRecordDo, BetStatusEnum status, SingleWalletBetResultDo betSingleWalletResult) {
        return BetRecordDo.<R>builder()
                .id(betRecordDo.getId())
                .betId(Objects.equals(BetStatusEnum.SUCCESS, status) ? betSingleWalletResult.getBetId() : null)
                .status(status.getCode())
                .build();
    }
}
