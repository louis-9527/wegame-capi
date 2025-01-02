package com.wegame.capi.biz.module.odds.strategy;

import com.wegame.capi.biz.domain.game.OddsCalculateDo;
import org.apache.commons.collections4.MapUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * 抽象赔率计算策略
 */
public abstract class AbstractOddsCalculateStrategy implements OddsCalculateStrategy {

    /**
     * 获取赔率表
     * @param oddsDo
     * @return
     */
    public abstract Map<String, Map<String, BigDecimal>> getOddsMap(OddsCalculateDo oddsDo);

    @Override
    public BigDecimal calculateOdds(OddsCalculateDo oddsDo) {
        Map<String, Map<String, BigDecimal>> oddsMap = getOddsMap(oddsDo);
        if (MapUtils.isEmpty(oddsMap)) {
            return BigDecimal.ZERO;
        }
        Map<String, BigDecimal> map = oddsMap.get(String.valueOf(oddsDo.getSymbol().getId()));
        if (MapUtils.isEmpty(map)) {
            return BigDecimal.ZERO;
        }
        BigDecimal odds = map.get(oddsDo.getOddsType().getCode());
        return Objects.isNull(odds) ? (BigDecimal.ZERO) : odds;
    }
}
