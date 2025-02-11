package com.wegame.capi.biz.module.base.odds;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * 抽象赔率计算策略
 */
@Slf4j
public abstract class AbstractOddsCalculateStrategy<T extends SymbolDo>
        implements OddsCalculateStrategy<T> {

    /**
     * 获取赔率表 <br/>
     * key: 元素，val：{key: 赔率类型，val：赔率}
     *
     * @param oddsCalculateDo
     * @return
     */
    public abstract Map<String, Map<String, BigDecimal>> getOddsConfig(OddsCalculateDo<T> oddsCalculateDo);

    @Override
    public BigDecimal calculateOdds(OddsCalculateDo<T> oddsCalculate) {
     //   long current = System.currentTimeMillis();
        AssertUtil.assertNotNull(oddsCalculate, ErrorCode.ERR_INTER, "oddsCalculate is required");
        Map<String, Map<String, BigDecimal>> oddsMap = getOddsConfig(oddsCalculate);
        if (MapUtils.isEmpty(oddsMap)) {
            return BigDecimal.ZERO;
        }
        Map<String, BigDecimal> map = oddsMap.get(oddsCalculate.getSymbol().getId());
        if (MapUtils.isEmpty(map)) {
            return BigDecimal.ZERO;
        }
        BigDecimal odds = map.get(oddsCalculate.getOddsType().getCode());
        BigDecimal val = Objects.isNull(odds) ? (BigDecimal.ZERO) : odds;
      //  log.info("odds cost time: {} ms", System.currentTimeMillis() - current);
        return val;
    }
}
