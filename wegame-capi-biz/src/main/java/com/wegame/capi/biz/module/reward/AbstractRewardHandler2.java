//package com.wegame.capi.biz.module.reward;
//
//import cn.hutool.core.collection.CollectionUtil;
//import com.google.common.collect.Lists;
//import com.wegame.capi.biz.builder.OddsCalculateBuilder;
//import com.wegame.capi.biz.builder.RewardLineDetailBuilder;
//import com.wegame.capi.biz.builder.RewardResultBuilder;
//import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
//import com.wegame.capi.biz.domain.biz.reward.*;
//import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
//import com.wegame.capi.biz.module.multiple.MultipleCalculateStrategy;
//import com.wegame.capi.biz.module.odds.OddsCalculateStrategy;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 抽象中奖策略
// */
//@Slf4j
//@Service
//public abstract class AbstractRewardHandler2 implements RewardHandler {
//
//    public abstract OddsCalculateStrategy oddsCalculateStrategy();
//
//    public abstract MultipleCalculateStrategy multipleCalculateStrategy();
//
//    public abstract void validate(RewardHandleDo rewardHandle);
//
//    public abstract boolean hasRewardFreeSpin(GameInfoDo gameInfo, WheelDo wheel);
//
////    public abstract RewardRuleResultDo calcRewardLines(RewardRuleDo rewardRuleDo);
//
//    //    public abstract RewardSubTypeEnum rewardSubType(RewardHandleDo rewardHandleDo, RewardResultDo rewardResultDo);
//
//    public RewardResultDo handle(RewardHandleDo rewardHandle) {
//        long current = System.currentTimeMillis();
//        // 1、业务校验
//        validate(rewardHandle);
//        // 2、计算中奖线
//        List<RewardLineDo> rewardRuleLineList = new ArrayList<>(); //  calcRewardLines todo
//        if (CollectionUtil.isEmpty(rewardRuleLineList)) {
//            // 未中奖
//            return null;
//        }
//        // 3、计算倍率
//        BigDecimal multiple = multipleCalculateStrategy().calculateMultiple(rewardHandle.getGameType(), rewardHandle.getWheel());
//        // 4、中奖线处理
//        // 中奖总金额
//        long rewardTotalAmount = 0L;
//        // 中奖坐标列表
//        List<RewardLineDetailDo> rewardLineList = Lists.newArrayList();
//        for (RewardLineDo rewardRuleLine : rewardRuleLineList) {
//            // 计算赔率  todo  优化，相同元素的中奖线，会触发多次赔率计算
//            BigDecimal odds = oddsCalculateStrategy().calculateOdds(OddsCalculateBuilder.build(rewardHandle, rewardRuleLine.getRewardSymbol(), rewardRuleLine.getRewardLineType()));
//            // 计算（中奖线）中奖金额：中奖线中奖金额计算公式 = 投注金额 * 赔率(1 + 倍率)
//            BigDecimal lineRewardAmount = calculateLineRewardAmount(rewardHandle.getBetAmount(), rewardRuleLine, odds, multiple);
//            // 累加中奖总金额
//            rewardTotalAmount = rewardTotalAmount + lineRewardAmount.longValue();
//            // 计算后的中奖线列表
//            rewardLineList.add(RewardLineDetailBuilder.build(rewardHandle.getBetAmount(), rewardRuleLine, lineRewardAmount.longValue(), multiple, odds));
//        }
//        // 5.构建中奖结果
//        RewardResultDo rewardResult = RewardResultBuilder.build(
//                rewardHandle.getBetAmount(),
//                rewardTotalAmount,
//                rewardLineList,
//                multiple,
//                hasRewardFreeSpin(rewardHandle.getGameInfo(), rewardHandle.getWheel()) // todo 优化 ，放到中奖线规则
//                //                rewardSubType(rewardHandle, tempRewardResultDo)
//        );
//        // 6.返回中奖信息
//        log.info("reward cost time: {} ms", System.currentTimeMillis() - current);
//        return rewardResult;
//    }
//
//    private static BigDecimal calculateLineRewardAmount(Long betAmount, RewardLineDo rewardRuleLine, BigDecimal odds, BigDecimal multiple) {
//        if (rewardRuleLine.isCommonReward()) {
//            return BigDecimal.valueOf(betAmount).multiply(odds).multiply(BigDecimal.ONE.add(multiple));
//        } else {
//            // scatter 无需进行倍率计算
//            return BigDecimal.valueOf(betAmount).multiply(odds);
//        }
//    }
//}
