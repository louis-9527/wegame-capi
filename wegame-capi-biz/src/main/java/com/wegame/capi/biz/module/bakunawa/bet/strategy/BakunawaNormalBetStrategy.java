package com.wegame.capi.biz.module.bakunawa.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.config.domain.BakunawaConfig;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.reward.RewardNotifyInfoDo;
import com.wegame.capi.biz.service.RewardService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.NotifyRewardTypeEnum;
import com.wegame.capi.common.utils.AmountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.ThreadPoolExecutor;

import static com.wegame.capi.client.enums.GameTypeEnum.BAKUNAWA;

@Slf4j
@Component("bakunawaNormalBetStrategy")
public class BakunawaNormalBetStrategy extends AbstractBakunawaBetStrategy {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    BakunawaConfig bakunawaConfig;
    @Autowired
    ThreadPoolExecutor defaultThreadPoolExecutor;
    @Autowired
    RewardService rewardService;

    @Override
    public void rewardAfterHandle(BetDo betDo, Long totalRewardAmount) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        defaultThreadPoolExecutor.execute(() -> {
            Long multiple = rewardMultiple(betDo.getBetAmount(), totalRewardAmount);
            Integer rewardNotifyMinMultiple = bakunawaConfig.getRewardNotifyMinMultiple();
            if (multiple >= rewardNotifyMinMultiple) {
                updateRewardNotifyInfo(GameTypeEnum.BAKUNAWA, userInfo, multiple, totalRewardAmount);
            }
        });
    }

    private Long rewardMultiple(Long betAmount, Long totalRewardAmount) {
        return totalRewardAmount / betAmount;
    }

    private void updateRewardNotifyInfo(GameTypeEnum gameType, UserInfo userInfo, Long multiple, long totalRewardAmount) {
        try {
            AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
            RewardNotifyInfoDo notifyInfo = RewardNotifyInfoDo.builder()
                    .gameId(gameType.getId())
                    .gameName(gameType.getName())
                    .userId(Long.valueOf(userInfo.getId()))
                    .userName(userInfo.getUserName())
                    .notifyRewardType(NotifyRewardTypeEnum.MULTIPLE.name())
                    .rewardTime(DateUtil.getCurrentMilli())
                    .rewardContent(String.format(Constants.BAKUNAWA_REWARD_NOTIFY_CONTENT_FORMAT,
                            userInfo.getUserName(),
                            multiple,
                            AmountUtil.decimalFormat("#,##0.###", AmountUtil.toBigDecimal(totalRewardAmount))))
                    .build();
            String key = String.format(Constants.WEGAME_REWARD_NOTIFY_KEY_FORMAT, BAKUNAWA.getId(), userInfo.getPId(), userInfo.getChannelId());
            DefaultRedisScript<Long> script = new DefaultRedisScript<>(Constants.WEGAME_REWARD_NOTIFY_LUA_SCRIPT, Long.class);
            redisTemplate.execute(script, Collections.singletonList(key), notifyInfo, Constants.BAKUNAWA_ADARNA_REWARD_NOTIFY_SIZE);
            rewardService.evict(gameType, userInfo.getPId(), userInfo.getChannelId());
            rewardService.getWithCache(gameType, userInfo.getPId(), userInfo.getChannelId());
        } catch (Exception e) {
            log.error("updateRewardNotifyInfo error", e);
        }
    }
}
