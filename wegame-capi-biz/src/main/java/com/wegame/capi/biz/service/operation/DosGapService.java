package com.wegame.capi.biz.service.operation;

import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.GameOperationEventDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventDo;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.DosGapRuleEnum;
import com.wegame.capi.infrastructure.config.properties.GameApiProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * DOS 渠道：同一玩家在两次动态事件命中之间需间隔若干局（剩余局数存 Redis，5 天 TTL）。
 */
@Slf4j
@Service
public class DosGapService {

    private static final long GAP_STATE_TTL_DAYS = 5L;

    private static final String DECREMENT_GAP_REMAINING_LUA = ""
            + "local v = redis.call('GET', KEYS[1])\n"
            + "if v == false or v == nil then return -1 end\n"
            + "local n = tonumber(v)\n"
            + "if n == nil or n <= 0 then return 0 end\n"
            + "local next = n - 1\n"
            + "redis.call('SET', KEYS[1], tostring(next), 'EX', tonumber(ARGV[1]))\n"
            + "return next\n";
    private static final RedisScript<Long> DECR_SCRIPT = new DefaultRedisScript<>(DECREMENT_GAP_REMAINING_LUA, Long.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private GameApiProperty gameApiProperty;
    @Autowired
    private RandomService randomService;

    /** 是否在间隔次数中（>0 则暂不命中下一次动态事件）。 */
    public boolean isInDosGap(UserInfo currentUser) {
        if (Objects.isNull(currentUser)) {
            return false;
        }
        String channelId = currentUser.getChannelId();
        String playerName = currentUser.getPlayerName();
        if (!gameApiProperty.supportDosChannelIdSet().contains(channelId)) {
            return false;
        }
        String key = buildDosGapRemainingRoundsKey(channelId, playerName);
        Object currentValue = redisTemplate.opsForValue().get(key);
        if (Objects.isNull(currentValue)) {
            return false;
        }
        try {
            // 大于0 表示在间隔中
            return Long.parseLong(String.valueOf(currentValue)) > 0L;
        } catch (NumberFormatException e) {
            log.warn("dos gap invalid redis value key={} v={}", key, currentValue);
            return false;
        }
    }

    /**
     * 单钱包成功后的 DOS 间隔处理（由 {@link com.wegame.capi.biz.module.base.bet.strategy.AbstractBetStrategy#saveBetRecordAndBetWithPayout} 在对局完成后触发，须同步调用）。
     * <ul>
     *   <li>动态事件局（{@link BetPromotionTypeEnum#OPERATION_EVENT}）：{@link #onDosSuccess}</li>
     *   <li>其它营销类型：{@link #onBetSuccess}</li>
     * </ul>
     */
    public void onBetComplete(BetDo betDo, BetRecordDo<?> betRecordDo, SingleWalletBetResultDo result) {
        if (Objects.isNull(result) || !Objects.equals(result.getBetStatus(), BetStatusEnum.SUCCESS)) {
            return;
        }
        if (Objects.isNull(betDo) || Objects.isNull(betRecordDo)) {
            return;
        }
        UserInfo userInfo = UserInfoContext.getUserInfo();
        if (Objects.isNull(userInfo)) {
            return;
        }
        String channelId = userInfo.getChannelId();
        String playerName = userInfo.getPlayerName();
        if (!gameApiProperty.supportDosChannelIdSet().contains(channelId)) {
            return;
        }
        BetPromotionTypeEnum promo = betDo.getBetPromotionType();
        if (Objects.equals(promo, BetPromotionTypeEnum.OPERATION_EVENT)) {
            GameOperationEventDo gameOperationEvent = betDo.getGameOperationEvent();
            if (Objects.isNull(gameOperationEvent)) {
                log.warn("dos gap skip: OPERATION_EVENT bet but gameOperationEvent is null");
                return;
            }
            onDosSuccess(gameOperationEvent, channelId, playerName);
        } else {
            onBetSuccess(channelId, playerName);
        }
    }

    /** 非动态事件局单钱包成功：间隔剩余局数减 1。 */
    private void onBetSuccess(String channelId, String playerName) {
        String key = buildDosGapRemainingRoundsKey(channelId, playerName);
        try {
            Long after = redisTemplate.execute(DECR_SCRIPT, Collections.singletonList(key),
                    String.valueOf(TimeUnit.DAYS.toSeconds(GAP_STATE_TTL_DAYS)));
            if (Objects.nonNull(after) && after >= 0L) {
                log.debug("dos gap decrement key={} remainingAfter={}", key, after);
            }
        } catch (Exception e) {
            log.error("dos gap decrement error key={}", key, e);
        }
    }

    /**
     * 动态事件单钱包成功：按 {@link GameOperationEventDo#getConfigMultiple()} 随机写入下一档剩余间隔局数；
     * {@code channelId} / {@code playerName} 须与当前登录用户一致（由 {@link #onBetComplete} 传入）。
     */
    private void onDosSuccess(GameOperationEventDo gameOperationEvent, String channelId, String playerName) {
        if (Objects.isNull(gameOperationEvent)) {
            return;
        }
        OperationEventDo operationEvent = gameOperationEvent.getOperationEvent();
        if (Objects.isNull(operationEvent)) {
            return;
        }
        Double configMultiple = gameOperationEvent.getConfigMultiple();
        if (Objects.isNull(configMultiple)) {
            log.warn("dos gap skip: configMultiple is null, eventId={}", operationEvent.getEventId());
            return;
        }
        DosGapRuleEnum rule = DosGapRuleEnum.forConfigMultiple(configMultiple);
        if (Objects.isNull(rule)) {
            log.info("dos gap skip: configMultiple={}", configMultiple);
            return;
        }
        int remainingRounds = randomService.randomInt(
                rule.getRandomGapRoundsMinInclusive(),
                rule.getRandomGapRoundsMaxInclusive());
        String key = buildDosGapRemainingRoundsKey(channelId, playerName);
        redisTemplate.opsForValue().set(key, String.valueOf(remainingRounds), GAP_STATE_TTL_DAYS, TimeUnit.DAYS);
        log.info("dos gap reseed remaining={} key={} configMultiple={} rule={}", remainingRounds, key, configMultiple, rule);
    }

    private static String buildDosGapRemainingRoundsKey(String channelId, String playerName) {
        return String.format(Constants.DOS_GAP_REMAINING_ROUNDS_KEY, channelId, playerName);
    }
}
