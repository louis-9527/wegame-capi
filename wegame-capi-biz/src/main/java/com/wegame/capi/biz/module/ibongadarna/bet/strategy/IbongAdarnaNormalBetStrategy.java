package com.wegame.capi.biz.module.ibongadarna.bet.strategy;

import cn.hutool.core.util.NumberUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.reward.RewardNotifyInfoDo;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.NotifyRewardTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.ThreadPoolExecutor;

import static com.wegame.capi.client.enums.GameTypeEnum.IBONG_ADARNA;

@Slf4j
@Component("ibongAdarnaNormalBetStrategy")
public class IbongAdarnaNormalBetStrategy extends AbstractIbongAdarnaBetStrategy {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    IbongAdarnaConfig ibongAdarnaConfig;
    @Autowired
    ThreadPoolExecutor defaultThreadPoolExecutor;

    @Override
    public void rewardAfterHandle(BetDo betDo, Long totalRewardAmount) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        defaultThreadPoolExecutor.execute(() -> {
            Integer multiple = rewardMultiple(betDo.getBetAmount(), totalRewardAmount);
            Integer rewardNotifyMinMultiple = ibongAdarnaConfig.getRewardNotifyMinMultiple();
            if (multiple >= rewardNotifyMinMultiple) {
                updateRewardNotifyInfo(userInfo, multiple, totalRewardAmount);
            }
        });
    }

    private Integer rewardMultiple(Long betAmount, Long totalRewardAmount) {
        return Math.toIntExact(totalRewardAmount / betAmount);
    }

    private void updateRewardNotifyInfo(UserInfo userInfo, Integer multiple, long totalRewardAmount) {
        try {
            AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
            RewardNotifyInfoDo notifyInfo = RewardNotifyInfoDo.builder()
                    .gameId(IBONG_ADARNA.getId())
                    .gameName(IBONG_ADARNA.getName())
                    .userId(Long.valueOf(userInfo.getId()))
                    .userName(userInfo.getUserName())
                    .notifyRewardType(NotifyRewardTypeEnum.MULTIPLE.name())
                    .rewardTime(DateUtil.getCurrentMilli())
                    .rewardContent(String.format(Constants.IBONG_ADARNA_REWARD_NOTIFY_CONTENT_FORMAT,
                            userInfo.getUserName(),
                            multiple,
                            NumberUtil.decimalFormat("#,##0.00", NumberUtil.div(totalRewardAmount, 100))))
                    .build();
            String key = String.format(Constants.WEGAME_REWARD_NOTIFY_KEY_FORMAT, IBONG_ADARNA.getId());
            DefaultRedisScript<Long> script = new DefaultRedisScript<>(Constants.WEGAME_REWARD_NOTIFY_LUA_SCRIPT, Long.class);
            redisTemplate.execute(script, Collections.singletonList(key), notifyInfo, Constants.IBONG_ADARNA_REWARD_NOTIFY_SIZE);
        } catch (Exception e) {
            log.error("updateRewardNotifyInfo error", e);
        }
    }
}
