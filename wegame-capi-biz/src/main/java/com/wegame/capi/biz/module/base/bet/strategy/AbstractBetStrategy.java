package com.wegame.capi.biz.module.base.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardNotifyInfoBuilder;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardNotifyInfoDo;
import com.wegame.capi.biz.listener.PulsarProducer;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import com.wegame.capi.biz.service.RewardService;
import com.wegame.capi.biz.service.SingleWalletService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.MessageTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.BetStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.MessageId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.wegame.capi.common.Constants.*;
import static com.wegame.capi.common.enums.BetPromotionTypeEnum.FREE_SPIN;

/**
 * 抽象投注策略<br>
 * 各游戏共性抽象<br/>
 * 1、获取具体游戏
 * 2、
 */
@Slf4j
@Component
public abstract class AbstractBetStrategy<R extends ToVoConverter, RD extends ToVoConverter> implements BetStrategy<RD> {
    private static final int RETRY_TIMES = 3;

    @Autowired
    public SingleWalletService singleWalletService;
    @Autowired
    public SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    public PulsarProducer pulsarProducer;
    @Autowired
    ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    RewardService rewardService;

    public abstract AbstractGameContext<R> gameContext();

    public abstract AbstractBetRecordManager<R> betRecordManager();

    public long generateId() {
        return snowflakeIdGenerator.nextId();
    }

    public long generateBetRecordId() {
        return snowflakeIdGenerator.nextId();
    }

    /**
     * 保存投注记录并投注派彩
     */
    public SingleWalletBetResultDo saveBetRecordAndBetWithPayout(BetDo betDo, BetRecordDo<R> betRecordDo, Boolean syncOpt) {
        if (Objects.equals(gameContext().gameInfo().getGameType(), GameTypeEnum.MARIA_MAKILING) ||
                Objects.equals(gameContext().gameInfo().getGameType(), GameTypeEnum.MALAKAS)
        ) {
            return asyncSaveBetRecordAndBetWithPayout(betDo, betRecordDo, syncOpt);
        } else {
            if (syncOpt) {
                return syncSaveBetRecordAndBetWithPayout(betDo, betRecordDo);
            } else {
                return asyncSaveBetRecordAndBetWithPayout(betDo, betRecordDo, syncOpt);
            }
        }
    }

    private SingleWalletBetResultDo syncSaveBetRecordAndBetWithPayout(BetDo betDo, BetRecordDo<R> betRecordDo) {
        Long betRecordId = betRecordManager().add(betRecordDo, gameContext().gameInfo().getGameType());
        log.info("syncSaveBetRecordAndBetWithPayout betRecordId:{}", betRecordId);
        AssertUtil.assertNotNull(betRecordId, ErrorCode.ERR_INTER);
        SingleWalletBetResultDo betSingleWalletResult = singleWalletService.betWithPayout(betRecordDo);
        if (Objects.equals(betSingleWalletResult.getBetStatus(), BetStatusEnum.SUCCESS)) {
            pulsarProducer.asyncSendMessage(gameContext().gameInfo().getGameType(),
                    buildUpdateMessage(betRecordDo, BetStatusEnum.SUCCESS, betSingleWalletResult));
            //            // 异步更新：更新为->投注成功
            //            betRecordManager().asyncUpdateBetRecord(buildUpdateRecord(betRecordDo, BetStatusEnum.SUCCESS, betSingleWalletResult),
            //                    gameContext().gameInfo().getGameType());
        } else if (Objects.equals(betSingleWalletResult.getBetStatus(), BetStatusEnum.FAIL)) {
            log.warn("syncSaveBetRecordAndBetWithPayout bet fail, betRecordDo: {}, betSingleWalletResult: {}", betRecordDo, betSingleWalletResult);
            // 异步发送更新消息: 更新为投注失败
            pulsarProducer.asyncSendMessage(gameContext().gameInfo().getGameType(),
                    buildUpdateMessage(betRecordDo, BetStatusEnum.FAIL, betSingleWalletResult));
            //            // 异步更新：更新为->投注失败
            //            betRecordManager().asyncUpdateBetRecord(buildUpdateRecord(betRecordDo, BetStatusEnum.FAIL, betSingleWalletResult),
            //                    gameContext().gameInfo().getGameType());
        }
        return betSingleWalletResult;
    }

    public SingleWalletBetResultDo asyncSaveBetRecordAndBetWithPayout(BetDo betDo, BetRecordDo<R> betRecordDo, boolean syncOpt) {
        // 发送消息(新增)
        MQMessage<BetRecordDo<R>> addMqMessage = MQMessage.buildMsg(String.valueOf(betRecordDo.getId()), MessageTypeEnum.ADD.getCode(), betRecordDo);
        MessageId addMessageId = pulsarProducer.syncSendMessage(gameContext().gameInfo().getGameType(), addMqMessage, RETRY_TIMES);
        AssertUtil.assertNotNull(addMessageId, ErrorCode.ERR_INTER);
        SingleWalletBetResultDo betSingleWalletResult = singleWalletService.betWithPayout(betRecordDo);
        if (Objects.equals(betSingleWalletResult.getBetStatus(), BetStatusEnum.SUCCESS)) {
            if (syncOpt && Objects.equals(gameContext().gameInfo().getGameType(), GameTypeEnum.MARIA_MAKILING) ||
                    Objects.equals(gameContext().gameInfo().getGameType(), GameTypeEnum.MALAKAS)
            ) {
                // 同步设置游戏记录缓存
                String cacheKey = String.format("%s_%s", betRecordDo.getId(), betRecordDo.getPlayerId());
                betRecordManager().putGameRecordCache(betRecordDo.getGameRecordList(), gameContext().gameInfo().getGameType(), cacheKey);
            }
            // 异步发送更新消息: 更新为投注成功
            pulsarProducer.asyncSendMessage(gameContext().gameInfo().getGameType(),
                    buildUpdateMessage(betRecordDo, BetStatusEnum.SUCCESS, betSingleWalletResult));
            return betSingleWalletResult;
        } else if (Objects.equals(betSingleWalletResult.getBetStatus(), BetStatusEnum.FAIL)) {
            log.warn("asyncSaveBetRecordAndBetWithPayout bet fail, betRecordDo: {}, betSingleWalletResult: {}", betRecordDo, betSingleWalletResult);
            // 异步发送更新消息: 更新为投注失败
            pulsarProducer.asyncSendMessage(gameContext().gameInfo().getGameType(),
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
                        .jackpot(betRecordDo.getJackpot())
                        .status(status.getCode())
                        .build());
    }

    /**
     * 延迟发送大奖通知
     * <p>
     * 条件：派彩金额>2000 || 倍数>30x
     * </p>
     * @param resultDo resultDo
     * @param betRecord betRecord
     */
    public void sendDelayRewardNotifyAfterBet(SingleWalletBetResultDo resultDo, BetRecordDo<R> betRecord) {
        try {
            //  1、投注成功
            if (!Objects.equals(resultDo.getBetStatus(), BetStatusEnum.SUCCESS)) {
                return;
            }
            // 2、获取金额： 默认取支付金额并且支付金额大于0，否则取投注金额。 另特殊场景freeSpin，强制取投注金额
            Long amount = Objects.nonNull(betRecord.getPayAmount()) && betRecord.getPayAmount() > 0L ? betRecord.getPayAmount() : betRecord.getBetAmount();
            if (Objects.equals(betRecord.getBetPromotionType(), FREE_SPIN.getCode())) {
                amount = betRecord.getBetAmount();
            }
            if (Objects.isNull(amount) || amount <= 0L) {
                log.error("sendDelayRewardNotifyAfterBet amount is null, betRecordId: {}", betRecord.getId());
                return;
            }
            // 3、派彩金额 < 金额(支付金额|投注)
            if (betRecord.getPayOutAmount() < amount) {
                return;
            }
            // 4、计算倍数
            BigDecimal multi = new BigDecimal(betRecord.getPayOutAmount()).divide(new BigDecimal(amount), 2, RoundingMode.FLOOR);
            // 5、发送判断：发送大奖通知
            if (Objects.equals(gameContext().gameInfo().getGameType(), GameTypeEnum.LAM_ANG)) {
                // 倍数 < 100x 则不发，反之则发送大奖通知
                if (multi.compareTo(DELAY_REWARD_100X_MULTIPLIER_THRESHOLD) < 0) {
                    return;
                }
            } else {
                // 倍数 < 30x 则不发，反之则发送大奖通知
                if (multi.compareTo(DELAY_REWARD_30X_MULTIPLIER_THRESHOLD) < 0) {
                    return;
                }
            }
            // 6、发送延迟消息
            GameTypeEnum gameType = gameContext().gameInfo().getGameType();
            UserInfo userInfo = UserInfoContext.getUserInfo();
            long payOutAmount = betRecord.getPayOutAmount();
            scheduledThreadPoolExecutor.schedule(() -> {
                updateRewardNotifyInfo(RewardNotifyInfoBuilder.buildDelayRewardNotifyInfo(gameType, multi, payOutAmount, betRecord.getBetTime(), userInfo), userInfo);
            }, 1, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("sendDelayRewardNotifyAfterBet error", e);
        }
    }

    private void updateRewardNotifyInfo(RewardNotifyInfoDo notifyInfo, UserInfo userInfo) {
        try {
            AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
            GameTypeEnum gameType = gameContext().gameInfo().getGameType();
            String key = String.format(Constants.WEGAME_REWARD_NOTIFY_KEY_FORMAT, gameType.getId(), userInfo.getPId(), userInfo.getChannelId());
            DefaultRedisScript<Long> script = new DefaultRedisScript<>(Constants.WEGAME_REWARD_NOTIFY_LUA_SCRIPT, Long.class);
            redisTemplate.execute(script, Collections.singletonList(key), notifyInfo, DEFAULT_DELAY_REWARD_NOTIFY_LIST_SIZE);
            rewardService.evict(gameType, userInfo.getPId(), userInfo.getChannelId());
            rewardService.getWithCache(gameType, userInfo.getPId(), userInfo.getChannelId());
        } catch (Exception e) {
            log.error("updateDelayRewardNotifyInfo error", e);
        }
    }
}
