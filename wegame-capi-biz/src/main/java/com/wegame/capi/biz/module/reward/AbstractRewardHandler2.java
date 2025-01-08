package com.wegame.capi.biz.module.reward;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.OddsCalculateBuilder;
import com.wegame.capi.biz.builder.RewardLineBuilder;
import com.wegame.capi.biz.builder.RewardResultBuilder;
import com.wegame.capi.biz.builder.RewardRuleBuilder;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.domain.biz.reward.*;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.module.multiple.MultipleCalculateStrategy;
import com.wegame.capi.biz.module.odds.OddsCalculateStrategy;
import com.wegame.capi.common.enums.RewardSubTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 抽象中奖策略
 */
@Slf4j
@Service
public abstract class AbstractRewardHandler2 implements RewardHandler {

    public abstract OddsCalculateStrategy oddsCalculateStrategy();

    public abstract MultipleCalculateStrategy multipleCalculateStrategy();

    //    public abstract RewardRuleResultDo calcRewardLines(RewardRuleDo rewardRuleDo);
    //
    //    public abstract boolean hasRewardFreeSpin(GameInfoDo gameInfo, SymbolDo[][] symbolArr);
    //
    public abstract void validate(RewardHandleDo rewardHandle);

    //    public abstract RewardSubTypeEnum rewardSubType(RewardHandleDo rewardHandleDo, RewardResultDo rewardResultDo);

    public RewardResultDo handle(RewardHandleDo rewardHandle) {
        long current = System.currentTimeMillis();
        // 1、业务校验
        validate(rewardHandle);
        // 2、计算中奖线
        List<RewardRuleLineDo> rewardRuleLineList = new ArrayList<>(); //  calcRewardLines todo
        if (CollectionUtil.isEmpty(rewardRuleLineList)) {
            // 未中奖
            return null;
        }
        // 3、计算倍率
        BigDecimal multiple = multipleCalculateStrategy().calculateMultiple(rewardHandle.getGameType(), rewardHandle.getWheel());
//        // 中奖总金额
//        long rewardTotalAmount = 0L;
//        // 中奖坐标列表
//        List<RewardLineDo> rewardLineList = Lists.newArrayList();
//        for (RewardRuleLineDo rewardRuleLine : rewardRuleLineList) {
//            // 计算赔率
//            BigDecimal odds = oddsCalculateStrategy().calculateOdds(OddsCalculateBuilder.build(rewardHandle, rewardRuleLine.getSymbol(), rewardRuleLine.getRewardLineType()));
//            // 计算（中奖线）中奖金额：中奖线中奖金额计算公式 = 投注金额 * 赔率(1 + 倍率)
//            // scatter 无需进行倍率计算
//            BigDecimal lineRewardAmount = BigDecimal.valueOf(rewardHandle.getBetAmount()).multiply(odds);
//            if (rewardRuleLine.isCommonReward()) {
//                lineRewardAmount = lineRewardAmount.multiply(BigDecimal.ONE.add(multiple));
//            }
//            // 中奖结果处理
//            rewardTotalAmount = rewardTotalAmount + lineRewardAmount.longValue();
//            // 计算后的中奖线列表
//            rewardLineList.add(RewardLineBuilder.build(rewardHandle.getBetAmount(), rewardRuleLine, lineRewardAmount.longValue(), multiple, odds));
//        }
//
//        RewardResultDo tempRewardResultDo = RewardResultBuilder.build(
//                rewardHandle.getBetAmount(),
//                rewardTotalAmount,
//                rewardLineList,
//                multiple,
//                hasRewardFreeSpin(rewardHandle.getGameInfo(), rewardHandle.getWheel().getSymbolArr()));
//        // 返回中奖信息
//        RewardResultDo rewardResultDo = RewardResultBuilder.build(tempRewardResultDo, rewardSubType(rewardHandle, tempRewardResultDo));
//        log.info("reward cost time: {} ms", System.currentTimeMillis() - current);
//        return rewardResultDo;
        return null;
    }
}
