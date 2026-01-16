package com.wegame.capi.biz.module.diawatamayari.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.DiwataMayariGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardNotifyInfoDo;
import com.wegame.capi.biz.service.RewardService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.NotifyRewardTypeEnum;
import com.wegame.capi.common.utils.AmountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component("diwataMayariNormalBetStrategy")
public class DiwataMayariNormalBetStrategy extends AbstractDiwataMayariBetStrategy {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    RewardService rewardService;
    @Autowired
    ThreadPoolExecutor defaultThreadPoolExecutor;

    @Override
    public void freeWheelAfterHandle(Long betRecordId, Long payOutAmount, List<DiwataMayariGameRecordDo> list) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        log.info("freeWheelAfterHandle start... betRecordId: {}", betRecordId);
        defaultThreadPoolExecutor.execute(() -> {
            try {
                //                BetRecordDo betRecord = diwataMayariBetRecordManager.getBetRecord(betRecordId, GameTypeEnum.DIWATA_MAYARI);
                //                AssertUtil.assertNotNull(betRecord, ErrorCode.ERR_INTER);
                //                GameRecordQuery query = GameRecordQuery.builder().betRecordId(betRecordId).index(0).freeWheelFlag(true).build();
                //                Long freeWheelCount = diwataMayariBetRecordManager.countGameRecord(query, GameTypeEnum.DIWATA_MAYARI);
                long freeWheelCount = Optional.ofNullable(list).orElse(Lists.newArrayList())
                        .stream()
                        .filter(item -> item.getIndex() == 0 && item.getFreeWheelFlag())
                        .count();
                if (freeWheelCount <= 0) {
                    log.info("freeWheelAfterHandle no free count.. betRecordId: {}", betRecordId);
                    return;
                }
                updateRewardNotifyInfo(freeWheelCount, payOutAmount, userInfo);
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
                    .gameId(GameTypeEnum.DIWATA_MAYARI.getId())
                    .gameName(GameTypeEnum.DIWATA_MAYARI.getName())
                    .userId(Long.valueOf(userInfo.getId()))
                    .userName(userInfo.getUserName())
                    .notifyRewardType(NotifyRewardTypeEnum.NORMAL_FREE_WHEEL.name())
                    .rewardTime(DateUtil.getCurrentMilli())
                    .rewardContent(String.format(Constants.DIWATA_MAYARI_NOTIFY_CONTENT_FORMAT,
                            DateUtil.format(new Date(), "yyyy/MM/dd HH:mm:ss"),
                            userInfo.getUserName(),
                            AmountUtil.decimalFormat("#,##0.###", AmountUtil.toBigDecimal(totalRewardAmount)),
                            freeWheelCount))
                    .build();
            String key = String.format(Constants.WEGAME_REWARD_NOTIFY_KEY_FORMAT, GameTypeEnum.DIWATA_MAYARI.getId(), userInfo.getPId(), userInfo.getChannelId());
            DefaultRedisScript<Long> script = new DefaultRedisScript<>(Constants.WEGAME_REWARD_NOTIFY_LUA_SCRIPT, Long.class);
            log.info("freeWheelAfterHandle updateRewardNotifyInfo add cache notifyInfo: {}, key: {}", notifyInfo, key);
            redisTemplate.execute(script, Collections.singletonList(key), notifyInfo, Constants.DIWATA_MAYARI_REWARD_NOTIFY_SIZE);
            rewardService.evict(GameTypeEnum.DIWATA_MAYARI, userInfo.getPId(), userInfo.getChannelId());
            rewardService.getWithCache(GameTypeEnum.DIWATA_MAYARI, userInfo.getPId(), userInfo.getChannelId());
        } catch (Exception e) {
            log.error("notifyRewardHandle error, errMsg: {}", e.getMessage(), e);
        }
    }
}
