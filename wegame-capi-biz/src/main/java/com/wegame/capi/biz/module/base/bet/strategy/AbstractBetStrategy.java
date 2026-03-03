package com.wegame.capi.biz.module.base.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
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
import com.wegame.capi.common.enums.NotifyRewardTypeEnum;
import com.wegame.capi.common.utils.AmountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.MessageId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private static final int RETRY_TIMES = 3;
    /** 延迟大奖通知的默认倍数阈值（子类可重写 getDelayRewardMultiplierThreshold 使用不同倍数） */
    private static final BigDecimal DEFAULT_DELAY_REWARD_MULTIPLIER_THRESHOLD = new BigDecimal(10);

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
        SingleWalletBetResultDo resultDo;
        if (Objects.equals(gameContext().gameInfo().getGameType(), GameTypeEnum.MARIA_MAKILING) ||
                Objects.equals(gameContext().gameInfo().getGameType(), GameTypeEnum.MALAKAS)
        ) {
            resultDo =  asyncSaveBetRecordAndBetWithPayout(betDo, betRecordDo, syncOpt);
        } else {
            if (syncOpt) {
                resultDo = syncSaveBetRecordAndBetWithPayout(betDo, betRecordDo);
            } else {
                resultDo = asyncSaveBetRecordAndBetWithPayout(betDo, betRecordDo, syncOpt);
            }
        }
        return resultDo;
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

    // --------------- 延迟大奖通知（通用能力） ---------------

    /**
     * 延迟大奖通知的倍数阈值，达到该倍数才发送，默认 10 倍，子类可重写
     */
    protected BigDecimal getDelayRewardMultiplierThreshold() {
        return DEFAULT_DELAY_REWARD_MULTIPLIER_THRESHOLD;
    }

    /**
     * 延迟大奖通知列表在 Redis 中的最大条数，子类可重写
     */
    protected int getDelayRewardNotifyListSize() {
        return Constants.DEFAULT_DELAY_REWARD_NOTIFY_LIST_SIZE;
    }

    /**
     * 构建延迟大奖通知内容，使用所有游戏通用模板。子类可重写以自定义或返回 null 关闭通知。
     *
     * @param multi       派彩金额/投注金额 的倍数
     * @param payOutAmount 派奖金额（pesos）
     * @param userInfo    当前用户
     * @return 通知内容，返回 null 时不发送
     */
    protected RewardNotifyInfoDo buildDelayRewardNotifyInfo(BigDecimal multi, long payOutAmount, UserInfo userInfo) {
        GameTypeEnum gameType = gameContext().gameInfo().getGameType();
        String content = String.format(Constants.DELAY_REWARD_NOTIFY_CONTENT_FORMAT,
                userInfo.getPlayerId(),
                multi.stripTrailingZeros().toPlainString(),
                AmountUtil.decimalFormat("#,##0.###", AmountUtil.toBigDecimal(payOutAmount)),
                DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        return RewardNotifyInfoDo.builder()
                .gameId(gameType.getId())
                .gameName(gameType.getName())
                .userId(Long.valueOf(userInfo.getId()))
                .userName(userInfo.getUserName())
                .notifyRewardType(NotifyRewardTypeEnum.NORMAL_FREE_WHEEL.name())
                .rewardTime(DateUtil.getCurrentMilli())
                .rewardContent(content)
                .build();
    }

    /**
     * 投注成功后调用：满足倍数阈值则延迟约 1 分钟发送大奖通知（由 buildDelayRewardNotifyInfo 返回非 null 时写入 Redis 并刷新缓存）
     */
    protected void afterBetSuccessSendDelayRewardNotify(BetRecordDo<R> betRecord) {
        try {
            Long amount = Objects.nonNull(betRecord.getPayAmount()) && betRecord.getPayAmount() > 0L ? betRecord.getPayAmount() : betRecord.getBetAmount();
            BigDecimal multi = new BigDecimal(betRecord.getPayOutAmount()).divide(new BigDecimal(amount), 2, RoundingMode.FLOOR);
            if (multi.compareTo(getDelayRewardMultiplierThreshold()) < 0) {
                return;
            }
            UserInfo userInfo = UserInfoContext.getUserInfo();
            long payOutAmount = betRecord.getPayOutAmount();
            scheduledThreadPoolExecutor.schedule(() -> {
                RewardNotifyInfoDo notifyInfo = buildDelayRewardNotifyInfo(multi, payOutAmount, userInfo);
                if (notifyInfo != null) {
                    updateDelayRewardNotifyInfo(notifyInfo, userInfo);
                }
            }, 1, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("afterBetSuccessSendDelayRewardNotify error", e);
        }
    }

    /**
     * 将延迟大奖通知写入 Redis 并刷新奖励缓存
     */
    protected void updateDelayRewardNotifyInfo(RewardNotifyInfoDo notifyInfo, UserInfo userInfo) {
        try {
            AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
            GameTypeEnum gameType = gameContext().gameInfo().getGameType();
            String key = String.format(Constants.WEGAME_REWARD_NOTIFY_KEY_FORMAT, gameType.getId(), userInfo.getPId(), userInfo.getChannelId());
            DefaultRedisScript<Long> script = new DefaultRedisScript<>(Constants.WEGAME_REWARD_NOTIFY_LUA_SCRIPT, Long.class);
            redisTemplate.execute(script, Collections.singletonList(key), notifyInfo, getDelayRewardNotifyListSize());
            rewardService.evict(gameType, userInfo.getPId(), userInfo.getChannelId());
            rewardService.getWithCache(gameType, userInfo.getPId(), userInfo.getChannelId());
        } catch (Exception e) {
            log.error("updateDelayRewardNotifyInfo error", e);
        }
    }
}
