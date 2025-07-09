package com.wegame.capi.biz.module.mangkukulam.odds;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.MangkukulamConfig;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import com.wegame.capi.biz.module.base.odds.AbstractOddsCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.TreeMap;

@Slf4j
@Component("mangkukulamOddsCalculateStrategy")
public class MangkukulamOddsCalculateStrategy extends AbstractOddsCalculateStrategy<MangkukulamSymbolDo> {

    @Autowired
    MangkukulamConfig mangkukulamConfig;

    @Override
    public TreeMap<String, TreeMap<String, BigDecimal>> getOddsConfig(OddsCalculateDo oddsCalculateDo) {
        return mangkukulamConfig.getOddsTable();
    }

    @Override
    public BigDecimal calculateOdds(OddsCalculateDo<MangkukulamSymbolDo> oddsCalculate) {
        AssertUtil.assertNotNull(oddsCalculate, ErrorCode.ERR_INTER, "oddsCalculate is required");
        TreeMap<String, TreeMap<String, BigDecimal>> oddsMap = getOddsConfig(oddsCalculate);
        if (MapUtils.isEmpty(oddsMap)) {
            return BigDecimal.ZERO;
        }
        TreeMap<String, BigDecimal> map = oddsMap.get(oddsCalculate.getSymbol().getId());
        if (MapUtils.isEmpty(map)) {
            return BigDecimal.ZERO;
        }
        BigDecimal odds = map.get(oddsCalculate.getOddsType().getNo());
        return Objects.isNull(odds) ? (BigDecimal.ZERO) : odds;
    }
}
