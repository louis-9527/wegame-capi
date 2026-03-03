package com.wegame.capi.biz.builder;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.reward.RewardNotifyInfoDo;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.NotifyRewardTypeEnum;
import com.wegame.capi.common.utils.AmountUtil;

import java.math.BigDecimal;
import java.util.Date;

public class RewardNotifyInfoBuilder {

    /**
     * 构建延迟大奖通知内容
     *
     * @param multi       派彩金额/投注金额 的倍数
     * @param payOutAmount 派奖金额（pesos）
     * @param userInfo    当前用户
     * @return 通知内容，返回 null 时不发送
     */
    public static RewardNotifyInfoDo buildDelayRewardNotifyInfo(GameTypeEnum gameType, BigDecimal multi, long payOutAmount, UserInfo userInfo) {
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
                .rewardTime(DateUtil.getCurrentMilli())
                .rewardContent(content)
                .build();
    }
}
