package com.wegame.capi.biz.converter.jackpot;

import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.client.domain.vo.JackpotResultVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import com.wegame.capi.common.utils.AmountUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JackpotResultConverter {
    public static JackpotResultVo from(GameTypeEnum gameTypeEnum, List<JackpotRewardPoolDo> doList) {
        Map<JackpotRewardPoolTypeEnum, JackpotRewardPoolDo> rewardPoolMap = doList.stream()
                .collect(Collectors.toMap(
                        JackpotRewardPoolDo::getType,
                        pool -> pool,
                        (existing, replacement) -> existing // 处理 key 冲突
                ));
        JackpotRewardPoolDo grand = rewardPoolMap.get(JackpotRewardPoolTypeEnum.grand);
        BigDecimal grandPool = AmountUtil.toBigDecimal(Math.max(grand.getAmount(), grand.getInitAmount()));

        JackpotRewardPoolDo major = rewardPoolMap.get(JackpotRewardPoolTypeEnum.major);
        BigDecimal majorPool = AmountUtil.toBigDecimal(Math.max(major.getAmount(), major.getInitAmount()));

        JackpotRewardPoolDo minor = rewardPoolMap.get(JackpotRewardPoolTypeEnum.minor);
        BigDecimal minorPool = AmountUtil.toBigDecimal(Math.max(minor.getAmount(), minor.getInitAmount()));

        JackpotRewardPoolDo mini = rewardPoolMap.get(JackpotRewardPoolTypeEnum.mini);
        BigDecimal miniPool = AmountUtil.toBigDecimal(Math.max(mini.getAmount(), mini.getInitAmount()));

        return JackpotResultVo.builder()
                .miniAmount(miniPool)
                .grandAmount(grandPool)
                .majorAmount(majorPool)
                .minorAmount(minorPool)
                .currency("PHP")
                .gameId(gameTypeEnum.getId())
                .build();
    }
}
