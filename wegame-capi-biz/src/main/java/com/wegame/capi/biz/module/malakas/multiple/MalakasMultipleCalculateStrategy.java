package com.wegame.capi.biz.module.malakas.multiple;

import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component("malakasMultipleCalculateStrategy")
public class MalakasMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy {

    public BigDecimal calculateMultiple(GameTypeEnum gameType, WheelDo wheel) {
        // todo 倍率计算
        return new BigDecimal(0);
        //        long current = System.currentTimeMillis();
        //        BigDecimal multiple = new BigDecimal(0);
        //        for (SymbolDo[] row : wheel.getSymbolArr()) {
        //            for (SymbolDo element : row) {
        //                AssertUtil.assertNotNull(element, ErrorCode.ERR_INTER);
        //                if (!Objects.equals(element.getSymbolType(), SymbolTypeEnum.WILD)) {
        //                    continue;
        //                }
        //                if (Objects.isNull(element.getSymbolRule())) {
        //                    continue;
        //                }
        //                if (Objects.equals(element.getSymbolRule().getSymbolRuleType(), SymbolRuleTypeEnum.MULTIPLE)) {
        //                    multiple = multiple.add(element.getSymbolRule().getValue());
        //                }
        //            }
        //        }
        //        log.info("multiple cost time: {} ms", System.currentTimeMillis() - current);
        //        return multiple;
    }
}
