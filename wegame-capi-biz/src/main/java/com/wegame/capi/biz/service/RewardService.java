package com.wegame.capi.biz.service;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Sets;
import com.wegame.capi.biz.builder.RewardResultBuilder;
import com.wegame.capi.biz.domain.game.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 中奖编排服务
 */
@Slf4j
@Service
public class RewardService {
    //
//    @Autowired
//    RewardHandlerContext rewardHandlerContext;
    @Autowired
    RewardRuleService rewardRuleService;
    @Autowired
    OddsService oddsService;
    @Autowired
    MultipleService multipleService;

    /**
     * @param gameId     游戏id
     * @param betType    投注类型
     * @param betAmount  投注金额
     * @param rollerGrid 滚轮方格
     * @return 中奖结果
     */
    public RewardResultDo handleRewardRule(Long gameId, Integer betType, Long betAmount, Long[][] rollerGrid) {
        // 中奖规则处理
        RewardRuleResultDo rewardRuleResultDo = rewardRuleService.calcRewardLines(gameId, betType, rollerGrid);
        if (Objects.isNull(rewardRuleResultDo) || CollectionUtil.isEmpty(rewardRuleResultDo.getRewardLineList())) {
            // 未中奖
            return null;
        }
        // 获取 wild 倍数
        BigDecimal wildMultiple = Optional.ofNullable(rewardRuleResultDo.getWildSymbolIdList())
                .map(symbolIdList -> multipleService.getMultiple(gameId, symbolIdList)).orElse(BigDecimal.ZERO);
        // 遍历中奖线单个处理
        long rewardTotalAmount = 0L;
        Set<int[]> rewardCoordinateSets = Sets.newHashSet();
        boolean isFreeSpin = false;
        for (RewardLineDo rewardLine : rewardRuleResultDo.getRewardLineList()) {
            // 获取赔率
            BigDecimal odds = oddsService.getOdds(OddsDo.builder()
                    .gameId(gameId)
                    .symbolId(rewardLine.getSymbolId())
                    .linkCount(rewardLine.getLinkCount())
                    .betType(betType).build());
            // 计算中奖金额.投注金额*(1+wild倍数)
            BigDecimal oddsRewardAmount = BigDecimal.valueOf(betAmount).multiply(odds);
            long lineRewardAmount = oddsRewardAmount.multiply(BigDecimal.ONE.add(wildMultiple)).longValue();
            // 中奖结果处理
            rewardTotalAmount = rewardTotalAmount + lineRewardAmount;
            rewardCoordinateSets.add(rewardLine.getRewardCoordinate());
        }
        // 返回中奖信息
        return RewardResultBuilder.build(rewardTotalAmount, rewardRuleResultDo.getRewardLineList(), wildMultiple, isFreeSpin, rewardCoordinateSets);
    }
}
