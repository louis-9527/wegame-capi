package com.wegame.capi.biz.builder;

import cn.hutool.core.util.RandomUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.reward.RewardNotifyInfoDo;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.utils.AmountUtil;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class RewardNotifyInfoBuilder {

    /**
     * 构建延迟大奖通知内容
     *
     * @param multi       派彩金额/投注金额 的倍数
     * @param payOutAmount 派奖金额（pesos）
     * @param userInfo    当前用户
     * @return 通知内容，返回 null 时不发送
     */
    public static RewardNotifyInfoDo buildDelayRewardNotifyInfo(GameTypeEnum gameType, BigDecimal multi, long payOutAmount, Long betTime, UserInfo userInfo) {
        long rewardTime = Objects.nonNull(betTime) ? betTime : DateUtil.getCurrentMilli();
        // 处理playerId脱敏 规则：前面2位，后面两位明文，中间固定的6位星号
        String content = String.format(Constants.DELAY_REWARD_NOTIFY_CONTENT_FORMAT,
                desensitizePlayerId(userInfo.getPlayerId()),
                multi.stripTrailingZeros().toPlainString(),
                AmountUtil.decimalFormat("#,##0.###", AmountUtil.toBigDecimal(payOutAmount)),
                DateUtil.format(new Date(rewardTime), "yyyy-MM-dd HH:mm:ss"));
        return RewardNotifyInfoDo.builder()
                .gameId(gameType.getId())
                .gameName(gameType.getName())
                .userId(Long.valueOf(userInfo.getId()))
                .userName(userInfo.getPlayerId())
                .rewardTime(rewardTime)
                .rewardContent(content)
                .build();
    }

    /**
     * 玩家ID脱敏处理规则：
     * 1. 长度不足4位时，随机生成0-9数字补齐到4位；
     * 2. 补齐后（或原长度≥4）：前2位明文+6位星号+后2位明文
     * eg: 1234 -> 12****34
     */
    private static String desensitizePlayerId(String playerId) {
        String processedPlayerId = StringUtils.defaultString(playerId, "");
        int length = processedPlayerId.length();
        if (processedPlayerId.length() < 4) {
            // 计算需要补齐的位数
            int needFill = 4 - length;
            // 生成needFill位0-9的随机数字串
            String randomFill = RandomUtil.randomNumbers(needFill);
            // 拼接补齐（补在末尾，也可根据业务需求补在开头）
            processedPlayerId += randomFill;
        }

        String prefix = processedPlayerId.substring(0, 2);
        String suffix = processedPlayerId.substring(processedPlayerId.length() - 2);
        return prefix + "******" + suffix;
    }
}
