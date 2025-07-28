package com.wegame.capi.biz.module.sirena.odds;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.OddsCalculateBuilder;
import com.wegame.capi.biz.config.domain.SirenaConfig;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.SirenaSymbolDo;
import com.wegame.capi.biz.module.base.odds.AbstractOddsCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.TreeMap;

@Slf4j
@Component("sirenaOddsCalculateStrategy")
public class SireanaOddsCalculateStrategy extends AbstractOddsCalculateStrategy<SirenaSymbolDo> {

    @Autowired
    SirenaConfig sirenaConfig;

    @Override
    public TreeMap<String, TreeMap<String, BigDecimal>> getOddsConfig(OddsCalculateDo oddsCalculateDo) {
        return sirenaConfig.getOddsConfig().getCommonOddsConfig();
    }

    @Override
    public BigDecimal calculateOddsOfRewardLine(RewardHandleDo<SirenaSymbolDo> rewardHandle, RewardLineDo<SirenaSymbolDo> rewardLine) {
        AssertUtil.assertNotNull(rewardLine, ErrorCode.ERR_INTER, "rewardLine is null");
        // 配置赔率*（所有wild上面的赔率之和）
        // 配置赔率
        BigDecimal configOdd = super.calculateOdds(OddsCalculateBuilder.build(rewardHandle, rewardLine.getRewardSymbol(), rewardLine.getRewardLineType()));
        // wild倍率之和
        BigDecimal dynamicOdds = this.calculateDynamicOdds(rewardLine);
        return BigDecimal.ZERO.compareTo(dynamicOdds) <= 0 ? configOdd : dynamicOdds.multiply(configOdd);
    }

    private BigDecimal calculateDynamicOdds(RewardLineDo<SirenaSymbolDo> rewardLine) {
        if (CollectionUtil.isEmpty(rewardLine.getRewardSymbolList())) {
            return BigDecimal.ZERO;
        }

        return rewardLine.getRewardSymbolList().stream().filter(r -> Objects.nonNull(r.getSymbol().getSymbolOdd()))
                .map(r -> r.getSymbol().getSymbolOdd()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
