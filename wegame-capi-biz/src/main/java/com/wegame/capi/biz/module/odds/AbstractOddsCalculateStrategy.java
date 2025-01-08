package com.wegame.capi.biz.module.odds;

import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * 抽象赔率计算策略
 */
@Slf4j
public abstract class AbstractOddsCalculateStrategy implements OddsCalculateStrategy {

    /**
     * 获取赔率表 <br/>
     * key: 元素id，val：{key: 赔率类型，val：赔率}
     * @param oddsCalculateDo
     * @return
     */
    public abstract Map<String, Map<String, BigDecimal>> getOddsMap(OddsCalculateDo oddsCalculateDo);

    @Override
    public BigDecimal calculateOdds(OddsCalculateDo oddsCalculateDo) {
        long current = System.currentTimeMillis();
        Map<String, Map<String, BigDecimal>> oddsMap = getOddsMap(oddsCalculateDo);
        if (MapUtils.isEmpty(oddsMap)) {
            return BigDecimal.ZERO;
        }
        Map<String, BigDecimal> map = oddsMap.get(String.valueOf(oddsCalculateDo.getSymbol().getId()));
        if (MapUtils.isEmpty(map)) {
            return BigDecimal.ZERO;
        }
        BigDecimal odds = map.get(oddsCalculateDo.getOddsType().getCode());
        BigDecimal val = Objects.isNull(odds) ? (BigDecimal.ZERO) : odds;
        log.info("odds cost time: {} ms", System.currentTimeMillis() - current);
        return val;
    }
}
