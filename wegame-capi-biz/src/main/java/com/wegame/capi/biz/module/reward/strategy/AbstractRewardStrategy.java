package com.wegame.capi.biz.module.reward.strategy;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Sets;
import com.wegame.capi.biz.builder.RewardResultBuilder;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.biz.domain.game.*;
import com.wegame.capi.biz.module.multiple.MultipleCalculateContext;
import com.wegame.capi.biz.module.odds.OddsCalculateContext;
import com.wegame.capi.common.enums.BetSubTypeEnum;
import com.wegame.capi.common.enums.BetTypeEnum;
import com.wegame.capi.common.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

/**
 * 抽象中奖策略
 */
@Slf4j
@Service
public abstract class AbstractRewardStrategy {

    @Autowired
    OddsCalculateContext oddsCalculateContext;
    @Autowired
    MultipleCalculateContext multipleCalculateContext;

    public abstract RewardRuleResultDo calcRewardLines(RewardRuleDo rewardRuleDo);

    // todo 参数封装  DO
    public RewardResultDo handle(GameTypeEnum gameType,
                                 BetTypeEnum betType,
                                 BetSubTypeEnum betSubType,
                                 Long betAmount,
                                 SymbolDo[][] symbolArr) {
        // 中奖规则处理 todo
        RewardRuleResultDo rewardRuleResultDo = calcRewardLines(null); // todo
        if (Objects.isNull(rewardRuleResultDo) || CollectionUtil.isEmpty(rewardRuleResultDo.getRewardRuleLineList())) {
            // 未中奖
            return null;
        }
        // 计算倍率
        BigDecimal multiple = multipleCalculateContext.calculateMultiple(gameType, symbolArr);
        // 中奖总金额
        long rewardTotalAmount = 0L;
        Set<int[]> rewardCoordinateSets = Sets.newHashSet();
        boolean isFreeSpin = false;
        for (RewardRuleLineDo rewardRuleLine : rewardRuleResultDo.getRewardRuleLineList()) {
            // 获取赔率
            BigDecimal odds = oddsCalculateContext.calculateOdds(OddsCalculateDo.builder()
                    .gameType(gameType)
                    .betType(betType)
                    .betSubType(betSubType)
                    .oddsType(null) // todo 根据中奖线类型，获取赔率类型
                    // .symbol(rewardRuleLine.getSymbolId()) // todo 中奖元素
                    .build());
            // 中奖线中奖金额计算公式 = 投注金额 * 赔率(1 + 倍率)
            long lineRewardAmount = BigDecimal.valueOf(betAmount).multiply(odds).multiply(BigDecimal.ONE.add(multiple)).longValue();
            // 中奖结果处理
            rewardTotalAmount = rewardTotalAmount + lineRewardAmount;
            rewardCoordinateSets.add(rewardRuleLine.getRewardCoordinate());
        }
        // 返回中奖信息
        return RewardResultBuilder.build(rewardTotalAmount, null, multiple, isFreeSpin, rewardCoordinateSets);
    }
}
