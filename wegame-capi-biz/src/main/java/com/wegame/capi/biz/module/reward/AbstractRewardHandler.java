package com.wegame.capi.biz.module.reward;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.OddsCalculateBuilder;
import com.wegame.capi.biz.builder.RewardLineBuilder;
import com.wegame.capi.biz.builder.RewardResultBuilder;
import com.wegame.capi.biz.builder.RewardRuleBuilder;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.biz.domain.game.*;
import com.wegame.capi.biz.module.multiple.MultipleCalculateStrategy;
import com.wegame.capi.biz.module.odds.OddsCalculateStrategy;
import com.wegame.capi.common.enums.GameGrandPrizeTypeEnum;
import com.wegame.capi.common.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 抽象中奖策略
 */
@Slf4j
@Service
public abstract class AbstractRewardHandler implements RewardHandler {

    public abstract OddsCalculateStrategy oddsCalculateStrategy();

    public abstract MultipleCalculateStrategy multipleCalculateStrategy();

    public abstract RewardRuleResultDo calcRewardLines(RewardRuleDo rewardRuleDo);

    public abstract boolean hasFreeSpin(GameTypeEnum gameType, SymbolDo[][] symbolArr);

    public abstract boolean bizValidate(RewardHandleDo rewardHandle);

    public abstract GameGrandPrizeTypeEnum calcGameGrandPrize(RewardHandleDo rewardHandleDo, RewardResultDo rewardResultDo);

    public RewardResultDo handle(RewardHandleDo rewardHandle) {
        this.validate(rewardHandle);
        // 中奖规则处理
        RewardRuleDo rewardRuleDo = RewardRuleBuilder.build(rewardHandle);
        AssertUtil.assertNotNull(rewardRuleDo, ErrorCode.ERR_INTER, "rewardRuleDo is null");
        RewardRuleResultDo rewardRuleResultDo = this.calcRewardLines(rewardRuleDo);
        if (Objects.isNull(rewardRuleResultDo) || CollectionUtil.isEmpty(rewardRuleResultDo.getRewardRuleLineList())) {
            // 未中奖
            return null;
        }
        // 计算倍率
        BigDecimal multiple = multipleCalculateStrategy().calculateMultiple(rewardHandle.getGameType(), rewardHandle.getWheel());
        // 中奖总金额
        long rewardTotalAmount = 0L;
        // 中奖坐标列表
        List<RewardLineDo> rewardLineList = Lists.newArrayList();
        for (RewardRuleLineDo rewardRuleLine : rewardRuleResultDo.getRewardRuleLineList()) {
            // 获取赔率
            OddsCalculateDo oddsCalculateDo = OddsCalculateBuilder.build(rewardHandle, rewardRuleLine.getSymbol(), rewardRuleLine.getRewardLineType());
            AssertUtil.assertNotNull(oddsCalculateDo, ErrorCode.ERR_INTER, "oddsCalculateDo is null");
            BigDecimal odds = oddsCalculateStrategy().calculateOdds(oddsCalculateDo);

            // 中奖线中奖金额计算公式 = 投注金额 * 赔率(1 + 倍率)
            // scatter 无需进行倍率计算
            BigDecimal lineRewardAmount = BigDecimal.valueOf(rewardHandle.getBetAmount()).multiply(odds);
            if (rewardRuleLine.isCommonReward()) {
                lineRewardAmount = lineRewardAmount.multiply(BigDecimal.ONE.add(multiple));
            }
            // 中奖结果处理
            rewardTotalAmount = rewardTotalAmount + lineRewardAmount.longValue();
            // 计算后的中奖线列表
            rewardLineList.add(RewardLineBuilder.build(rewardRuleLine, lineRewardAmount.longValue(), multiple, odds));
        }

        RewardResultDo tempRewardResultDo = RewardResultBuilder.build(
                rewardTotalAmount,
                rewardLineList,
                multiple,
                this.hasFreeSpin(rewardHandle.getGameType(),
                        rewardHandle.getWheel().getSymbolArr()));
        // 返回中奖信息
        return RewardResultBuilder.build(tempRewardResultDo,
                this.calcGameGrandPrize(rewardHandle, tempRewardResultDo));
    }

    public boolean validate(RewardHandleDo rewardHandle) {
        // 为空
        AssertUtil.assertNotNull(rewardHandle, ErrorCode.ERR_PARAM, "rewardHandle is null");
        AssertUtil.assertNotNull(rewardHandle.getGameType(), ErrorCode.ERR_INTER, "not support gameType");
        AssertUtil.assertNotNull(rewardHandle.getBetType(), ErrorCode.ERR_PARAM, "not support betType");
        AssertUtil.assertNotNull(rewardHandle.getBetSubType(), ErrorCode.ERR_PARAM, "not support betSubType");
        AssertUtil.assertNotNull(rewardHandle.getWheel(), ErrorCode.ERR_PARAM, "wheel is null");
        AssertUtil.assertNotNull(rewardHandle.getWheel().getIndex(), ErrorCode.ERR_PARAM, "wheel index is null");
        AssertUtil.assertNotNull(rewardHandle.getWheel().getSymbolArr(), ErrorCode.ERR_PARAM, "wheel symbolArr is null");
        return this.bizValidate(rewardHandle);
    }
}
