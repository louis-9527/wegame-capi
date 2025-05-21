package com.wegame.capi.biz.module.lamang.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardNotifyInfoDo;
import com.wegame.capi.biz.service.RewardService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.NotifyRewardTypeEnum;
import com.wegame.capi.common.utils.AmountUtil;
import com.wegame.capi.dao.repository.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component("malakasNormalBetStrategy")
public class MalakasNormalBetStrategy extends AbstractMalakasBetStrategy {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    RewardService rewardService;
    @Autowired
    ThreadPoolExecutor defaultThreadPoolExecutor;

    @Override
    public void freeWheelAfterHandle(Long betRecordId) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        log.info("freeWheelAfterHandle start... betRecordId: {}", betRecordId);
        defaultThreadPoolExecutor.execute(() -> {
            try {
                BetRecordDo betRecord = malakasBetRecordManager.getBetRecord(betRecordId, GameTypeEnum.MALAKAS);
                AssertUtil.assertNotNull(betRecord, ErrorCode.ERR_INTER);
                GameRecordQuery query = GameRecordQuery.builder().betRecordId(betRecordId).index(0).freeWheelFlag(true).build();
                Long freeWheelCount = malakasBetRecordManager.countGameRecord(query, GameTypeEnum.MALAKAS);
                if (Objects.isNull(freeWheelCount)) {
                    log.info("freeWheelAfterHandle null return... betRecordId: {}", betRecordId);
                    return;
                }
                updateRewardNotifyInfo(freeWheelCount, betRecord.getPayOutAmount(), userInfo);
            } catch (Exception e) {
                log.error("freeWheelAfterHandle Exception, errMsg: {}", e.getMessage(), e);
            }
        });
    }

    private void updateRewardNotifyInfo(long freeWheelCount, long totalRewardAmount, UserInfo userInfo) {
        try {
            log.info("freeWheelAfterHandle updateRewardNotifyInfo freeWheelCount: {}, totalRewardAmount: {}, userInfo: {}", freeWheelCount, totalRewardAmount, userInfo);
            AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
            RewardNotifyInfoDo notifyInfo = RewardNotifyInfoDo.builder()
                    .gameId(GameTypeEnum.MALAKAS.getId())
                    .gameName(GameTypeEnum.MALAKAS.getName())
                    .userId(Long.valueOf(userInfo.getId()))
                    .userName(userInfo.getUserName())
                    .notifyRewardType(NotifyRewardTypeEnum.NORMAL_FREE_WHEEL.name())
                    .rewardTime(DateUtil.getCurrentMilli())
                    .rewardContent(String.format(Constants.MALAKAS_REWARD_NOTIFY_CONTENT_FORMAT,
                            DateUtil.format(new Date(), "yyyy/MM/dd HH:mm:ss"),
                            userInfo.getUserName(),
                            AmountUtil.decimalFormat("#,##0.###", AmountUtil.toBigDecimal(totalRewardAmount)),
                            freeWheelCount))
                    .build();
            String key = String.format(Constants.WEGAME_REWARD_NOTIFY_KEY_FORMAT, GameTypeEnum.MALAKAS.getId());
            DefaultRedisScript<Long> script = new DefaultRedisScript<>(Constants.WEGAME_REWARD_NOTIFY_LUA_SCRIPT, Long.class);
            log.info("freeWheelAfterHandle updateRewardNotifyInfo add cache notifyInfo: {}, key: {}", notifyInfo, key);
            redisTemplate.execute(script, Collections.singletonList(key), notifyInfo, Constants.MALAKAS_REWARD_NOTIFY_SIZE);
            rewardService.evict(GameTypeEnum.MALAKAS);
            Object o = redisTemplate.opsForValue().get(key);
            log.info("freeWheelAfterHandle updateRewardNotifyInfo redis value notifyInfo: {}, key: {}", o, key);
        } catch (Exception e) {
            log.error("notifyRewardHandle error, errMsg: {}", e.getMessage(), e);
        }
    }
}
