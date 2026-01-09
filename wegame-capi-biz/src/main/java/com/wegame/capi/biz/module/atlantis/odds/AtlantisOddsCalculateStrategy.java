package com.wegame.capi.biz.module.atlantis.odds;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.OddsCalculateBuilder;
import com.wegame.capi.biz.config.domain.AtlantisConfig;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.module.base.odds.AbstractOddsCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.TreeMap;

@Slf4j
@Component("atlantisOddsCalculateStrategy")
public class AtlantisOddsCalculateStrategy extends AbstractOddsCalculateStrategy<SymbolDo> {

    @Autowired
    AtlantisConfig atlantisConfig;

    @Override
    public TreeMap<String, TreeMap<String, BigDecimal>> getOddsConfig(OddsCalculateDo oddsCalculateDo) {
        return null;
    }

    @Override
    public BigDecimal calculateOddsOfRewardLine(RewardHandleDo<SymbolDo> rewardHandle, RewardLineDo<SymbolDo> rewardLine) {
        AssertUtil.assertNotNull(rewardLine, ErrorCode.ERR_INTER, "rewardLine is null");
        if (rewardLine.isCashReward()) {
            return getOddsOfCashReward(rewardLine);
        } else {
            return calculateOdds(OddsCalculateBuilder.build(rewardHandle, rewardLine.getRewardSymbol(), rewardLine.getRewardLineType()));
        }
    }

    private BigDecimal getOddsOfCashReward(RewardLineDo<SymbolDo> rewardLine) {
        AssertUtil.assertNotEmpty(rewardLine.getRewardSymbolList(), ErrorCode.ERR_INTER, "rewardSymbolList is null");
        BigDecimal totalOdds = BigDecimal.ZERO;
        for (RewardSymbolDo<SymbolDo> rewardSymbolDo : rewardLine.getRewardSymbolList()) {
            SymbolDo symbolDo = rewardSymbolDo.getSymbol();
            if (Objects.isNull(symbolDo)) {
                continue;
            }
            if (symbolDo.isCashSymbol()) {
                if (!Objects.isNull(symbolDo.getSymbolOdd())) {
                    totalOdds = totalOdds.add(symbolDo.getSymbolOdd());
                }
            } else if (symbolDo.isWildSymbol()) {
                SymbolDo nestSymbol = symbolDo.getNestSymbol();
                AssertUtil.assertNotNull(nestSymbol, ErrorCode.ERR_INTER, "nestSymbol in wildSymbol can not be null");
                if (!Objects.isNull(nestSymbol.getSymbolOdd())) {
                    totalOdds = totalOdds.add(nestSymbol.getSymbolOdd());
                }
            }
        }
        return totalOdds;
    }
}
