package com.wegame.capi.biz.module.multiple.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.common.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolRuleTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
public abstract class AbstractMultipleCalculateStrategy implements MultipleCalculateStrategy {

    public BigDecimal calculateMultiple(GameTypeEnum gameType, SymbolDo[][] symbolArr) {
        BigDecimal multiple = new BigDecimal(0);
        for(SymbolDo[] row : symbolArr) {
            for(SymbolDo element : row) {
                AssertUtil.assertNotNull(element, ErrorCode.ERR_INTER);
                // todo
                if (!Objects.equals(element.getSymbolType(), SymbolTypeEnum.COMMON)) {
                    continue;
                }
                if (Objects.isNull(element.getSymbolRule())) {
                    continue;
                }
                if (Objects.equals(element.getSymbolRule(), SymbolRuleTypeEnum.MULTIPLE)) {
                    multiple = multiple.add(element.getSymbolRule().getValue());
                }
            }
        }
        return multiple;
    }

}
