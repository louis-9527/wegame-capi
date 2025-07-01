package com.wegame.capi.biz.module.kapre.odds;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.OddsCalculateBuilder;
import com.wegame.capi.biz.config.domain.KapreConfig;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.KapreSymbolDo;
import com.wegame.capi.biz.module.base.odds.AbstractOddsCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.TreeMap;

@Slf4j
@Component("kapreOddsCalculateStrategy")
public class KapreOddsCalculateStrategy extends AbstractOddsCalculateStrategy<KapreSymbolDo> {

    @Autowired
    KapreConfig kapreConfig;

    @Override
    public TreeMap<String, TreeMap<String, BigDecimal>> getOddsConfig(OddsCalculateDo oddsCalculateDo) {
        return kapreConfig.getOddsConfig().getCommonOddsConfig();
    }

    @Override
    public BigDecimal calculateOddsOfRewardLine(RewardHandleDo<KapreSymbolDo> rewardHandle, RewardLineDo<KapreSymbolDo> rewardLine) {
        AssertUtil.assertNotNull(rewardLine, ErrorCode.ERR_INTER, "rewardLine is null");
        if (rewardLine.isCashReward()) {
            return getOddsOfCashReward(rewardLine);
        } else {
            return calculateOdds(OddsCalculateBuilder.build(rewardHandle, rewardLine.getRewardSymbol(), rewardLine.getRewardLineType()));
        }
    }

    private BigDecimal getOddsOfCashReward(RewardLineDo<KapreSymbolDo> rewardLine) {
        AssertUtil.assertNotEmpty(rewardLine.getRewardSymbolList(), ErrorCode.ERR_INTER, "rewardSymbolList is null");
        BigDecimal totalOdds = BigDecimal.ZERO;
        for (RewardSymbolDo<KapreSymbolDo> rewardSymbolDo : rewardLine.getRewardSymbolList()) {
            KapreSymbolDo symbolDo = rewardSymbolDo.getSymbol();
            if (Objects.isNull(symbolDo)) {
                continue;
            }
            if (symbolDo.isCashSymbol()) {
                totalOdds = totalOdds.add(symbolDo.getSymbolOdd());
            } else if (symbolDo.isWildSymbol()) {
                KapreSymbolDo nestSymbol = symbolDo.getNestSymbol();
                AssertUtil.assertNotNull(nestSymbol, ErrorCode.ERR_INTER, "nestSymbol in wildSymbol can not be null");
                totalOdds = totalOdds.add(nestSymbol.getSymbolOdd());
            }
        }
        return totalOdds;
    }
}
