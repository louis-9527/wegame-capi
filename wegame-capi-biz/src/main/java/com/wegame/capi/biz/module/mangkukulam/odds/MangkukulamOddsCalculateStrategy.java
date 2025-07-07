package com.wegame.capi.biz.module.mangkukulam.odds;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import com.wegame.capi.biz.module.base.odds.AbstractOddsCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.TreeMap;

@Slf4j
@Component("mangkukulamOddsCalculateStrategy")
public class MangkukulamOddsCalculateStrategy extends AbstractOddsCalculateStrategy<MangkukulamSymbolDo> {

    @Override
    public TreeMap<String, TreeMap<String, BigDecimal>> getOddsConfig(OddsCalculateDo oddsCalculateDo) {
        throw new BusinessException(ErrorCode.ERR_INTER, "todo");
    }
}
