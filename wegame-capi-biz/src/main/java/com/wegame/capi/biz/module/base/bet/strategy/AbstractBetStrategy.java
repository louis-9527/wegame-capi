package com.wegame.capi.biz.module.base.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetSingleWalletResultDo;
import com.wegame.capi.biz.listener.PulsarProducer;
import com.wegame.capi.biz.manager.AbstractRecordManager;
import com.wegame.capi.biz.module.base.game.Game;
import com.wegame.capi.biz.service.SingleWalletService;
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
public abstract class AbstractBetStrategy<R extends ToVoConverter, BD, RD extends ToVoConverter>
        implements BetStrategy<BD, RD> {
    @Autowired
    public SingleWalletService singleWalletService;
    @Autowired
    public SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    public PulsarProducer pulsarProducer;

    public abstract Game<R> getGame();

    public abstract AbstractRecordManager<R> getRecordManager();

    public long generateBetRecordId() {
        long betRecordId = snowflakeIdGenerator.nextId();
        // todo delete @maidong
        log.info("generateBetRecordId: {}", betRecordId);
        return betRecordId;
    }

    /**
     * 保存投注记录并投注派彩
     * @param betRecordDo 投注记录
     * @param syncOpt 是否同步写库
     * @return 玩家余额
     */
    public BetSingleWalletResultDo saveBetRecordAndBetWithPayout(BetRecordDo<R> betRecordDo, Boolean syncOpt) {
        // todo delete @maidong
        log.info("saveBetRecordAndBetWithPayout betRecordId:{}; syncOpt:{}", betRecordDo.getId(), syncOpt);
        if (syncOpt) {
            return syncSaveBetRecordAndBetWithPayout(betRecordDo);
        } else {
            return asyncSaveBetRecordAndBetWithPayout(betRecordDo);
        }
    }

    private BetSingleWalletResultDo syncSaveBetRecordAndBetWithPayout(BetRecordDo<R> betRecordDo) {
        Long betRecordId = getRecordManager().add(betRecordDo, getGame().getGameInfo().getGameType());
        log.info("syncSaveBetRecordAndBetWithPayout betRecordId:{}", betRecordId);
        AssertUtil.assertNotNull(betRecordId, ErrorCode.ERR_INTER);
        BetSingleWalletResultDo betSingleWalletResult = singleWalletService.betWithPayout(betRecordDo);
        if (Objects.equals(betSingleWalletResult.getBetStatus(), BetStatusEnum.SUCCESS)) {
            // 异步更新：更新为->投注成功
            getRecordManager().asyncUpdateBetRecord(buildUpdateRecord(betRecordDo, BetStatusEnum.SUCCESS, betSingleWalletResult),
                    getGame().getGameInfo().getGameType());
        } else if (Objects.equals(betSingleWalletResult.getBetStatus(), BetStatusEnum.FAIL)) {
            log.warn("syncSaveBetRecordAndBetWithPayout bet fail, betRecordDo: {}, betSingleWalletResult: {}", betRecordDo, betSingleWalletResult);
            // 异步更新：更新为->投注失败
            getRecordManager().asyncUpdateBetRecord(buildUpdateRecord(betRecordDo, BetStatusEnum.FAIL, betSingleWalletResult),
                    getGame().getGameInfo().getGameType());
        }
        return betSingleWalletResult;
    }

    public BetSingleWalletResultDo asyncSaveBetRecordAndBetWithPayout(BetRecordDo<R> betRecordDo) {
        // 发送消息(新增)
        MQMessage<BetRecordDo<R>> addMqMessage = MQMessage.buildMsg(String.valueOf(betRecordDo.getId()), MessageTypeEnum.ADD.getCode(), betRecordDo);
        //        MessageId addMessageId = pulsarProducer.syncSendMessage(getGame().getGameInfo().getGameType(),addMqMessage, RETRY_TIMES);
        //        AssertUtil.assertNotNull(addMessageId, ErrorCode.ERR_INTER);
        CompletableFuture<MessageId> future = pulsarProducer.asyncSendMessage(getGame().getGameInfo().getGameType(), addMqMessage);
        BetSingleWalletResultDo betSingleWalletResult = singleWalletService.betWithPayout(betRecordDo);
        if (Objects.equals(betSingleWalletResult.getBetStatus(), BetStatusEnum.SUCCESS)) {
            MessageId messageId;
            try {
                messageId = future.get();
            } catch (Exception e) {
                log.error("asyncSendMessage send fail, to syncSendMessage, betRecordId: {}", betRecordDo.getId());
                messageId = pulsarProducer.syncSendMessage(getGame().getGameInfo().getGameType(), addMqMessage);
            }
            if (Objects.isNull(messageId)) {
                log.error("syncSendMessage send fail, sync add data, betRecordId: {}", betRecordDo.getId());
                // 新增消息发送失败，则转同步写db
                getRecordManager().add(betRecordDo, getGame().getGameInfo().getGameType());
            }
            // 异步发送更新消息: 更新为投注成功
            pulsarProducer.asyncSendMessageThenRetry(getGame().getGameInfo().getGameType(),
                    buildUpdateMessage(betRecordDo, BetStatusEnum.SUCCESS, betSingleWalletResult));
            return betSingleWalletResult;
        } else if (Objects.equals(betSingleWalletResult.getBetStatus(), BetStatusEnum.FAIL)) {
            log.warn("asyncSaveBetRecordAndBetWithPayout bet fail, betRecordDo: {}, betSingleWalletResult: {}", betRecordDo, betSingleWalletResult);
            // 异步发送更新消息: 更新为投注成功
            pulsarProducer.asyncSendMessageThenRetry(getGame().getGameInfo().getGameType(),
                    buildUpdateMessage(betRecordDo, BetStatusEnum.FAIL, betSingleWalletResult));
        }
        return betSingleWalletResult;
    }

    private MQMessage<BetRecordDo> buildUpdateMessage(BetRecordDo<R> betRecordDo, BetStatusEnum status, BetSingleWalletResultDo betSingleWalletResult) {
        return MQMessage.buildMsg(String.valueOf(betRecordDo.getId()),
                MessageTypeEnum.UPDATE.getCode(),
                BetRecordDo.<R>builder()
                        .id(betRecordDo.getId())
                        .betId(Objects.equals(BetStatusEnum.SUCCESS, status) ? betSingleWalletResult.getBetId() : null)
                        .status(status.getCode())
                        .build());
    }

    private BetRecordDo<R> buildUpdateRecord(BetRecordDo<R> betRecordDo, BetStatusEnum status, BetSingleWalletResultDo betSingleWalletResult) {
        return BetRecordDo.<R>builder()
                .id(betRecordDo.getId())
                .betId(Objects.equals(BetStatusEnum.SUCCESS, status) ? betSingleWalletResult.getBetId() : null)
                .status(status.getCode())
                .build();
    }
}
