package com.wegame.capi.biz.module.reward;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.wegame.capi.biz.builder.OddsCalculateBuilder;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.reward.RewardLineDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.multiple.MultipleCalculateStrategy;
import com.wegame.capi.biz.module.odds.OddsCalculateStrategy;
import com.wegame.capi.common.enums.RewardTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 抽象中奖策略
 */
@Slf4j
@Service
public abstract class AbstractRewardHandler2 extends CommonRewardHandler implements RewardHandler {

    public abstract OddsCalculateStrategy oddsCalculateStrategy();

    public abstract MultipleCalculateStrategy multipleCalculateStrategy();

    public abstract void validate(RewardHandleDo rewardHandle);

    public abstract List<RewardLineDo> calcRewardLineList(GameInfoDo gameInfo, WheelDo wheel);

    public RewardResultDo handle(RewardHandleDo rewardHandle) {
        long current = System.currentTimeMillis();
        // 1、业务校验
        validate(rewardHandle);
        // 2、计算中奖线
        List<RewardLineDo> rewardLineList = calcRewardLineList(rewardHandle.getGameInfo(), rewardHandle.getWheel());
        if (CollectionUtil.isEmpty(rewardLineList)) {
            // 未中奖
            return null;
        }
        // 3、计算倍率
        BigDecimal totalMultiple = multipleCalculateStrategy().calculateMultiple(rewardHandle.getGameType(), rewardHandle.getWheel());
        // 4、中奖线处理
        // 中奖总金额
        long totalRewardAmount = 0L;
        // 中奖坐标列表
        List<RewardLineDetailDo> rewardLineDetailList = Lists.newArrayList();
        for (RewardLineDo rewardLine : CollectionUtils.emptyIfNull(rewardLineList)) {
            // 计算赔率  todo  优化，相同元素的中奖线，会触发多次赔率计算
            BigDecimal odds = oddsCalculateStrategy().calculateOdds(OddsCalculateBuilder.build(rewardHandle, rewardLine.getRewardSymbol(), rewardLine.getRewardLineType()));
            // 计算（中奖线）中奖金额：中奖线中奖金额计算公式 = 投注金额 * 赔率(1 + 倍率)
            BigDecimal lineRewardAmount = calculateLineRewardAmount(rewardLine, rewardHandle.getBetAmount(), odds, totalMultiple);
            // 累加中奖总金额
            totalRewardAmount = totalRewardAmount + lineRewardAmount.longValue();
            // 中奖线明细列表
            rewardLineDetailList.add(RewardLineDetailDo.builder()
                    .betAmount(rewardHandle.getBetAmount())
                    .rewardAmount(lineRewardAmount.longValue())
                    .odds(odds)
                    .multiple(totalMultiple)
                    .rewardLineType(rewardLine.getRewardLineType())
                    .rewardLineSubType(rewardLine.getRewardLineSubType())
                    .symbol(rewardLine.getRewardSymbol())
                    .symbolList(rewardLine.getRewardSymbolList())
                    .symbolIndexList(rewardLine.getRewardSymbolIndexList())
                    .build());
        }
        // 5.构建中奖结果
        RewardResultDo rewardResult = RewardResultDo.builder()
                .rewardType(RewardTypeEnum.valueOfRewardLineCont(rewardLineList.size()))
                .rewardSubType(calculateRewardSubType(rewardHandle.getBetAmount(), totalRewardAmount))
                .betAmount(rewardHandle.getBetAmount())
                .totalRewardAmount(totalRewardAmount)
                .hasRewardFreeSpin(hasRewardFreeSpins(rewardLineList))
                .rewardLineDetailList(rewardLineDetailList)
                .totalMultiple(totalMultiple)
                .build();
        // 6.返回中奖信息
        log.info("reward cost time: {} ms", System.currentTimeMillis() - current);
        return rewardResult;
    }
}
