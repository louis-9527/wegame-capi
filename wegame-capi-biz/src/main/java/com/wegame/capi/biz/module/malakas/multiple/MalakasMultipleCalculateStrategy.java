package com.wegame.capi.biz.module.malakas.multiple;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolRuleTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@Component("malakasMultipleCalculateStrategy")
public class MalakasMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy {

    public BigDecimal calculateMultiple(GameTypeEnum gameType, WheelDo wheel) {
        long current = System.currentTimeMillis();
        BigDecimal multiple = new BigDecimal(0);
        for (SymbolDo[] row : wheel.getSymbolArr()) {
            for (SymbolDo element : row) {
                AssertUtil.assertNotNull(element, ErrorCode.ERR_INTER);
                if (!Objects.equals(element.getSymbolType(), SymbolTypeEnum.WILD)) {
                    continue;
                }
                if (Objects.isNull(element.getSymbolRule())) {
                    continue;
                }
                if (Objects.equals(element.getSymbolRule().getSymbolRuleType(), SymbolRuleTypeEnum.MULTIPLE)) {
                    multiple = multiple.add(element.getSymbolRule().getValue());
                }
            }
        }
        log.info("multiple cost time: {} ms", System.currentTimeMillis() - current);
        return multiple;
    }
}
