package com.wegame.capi.biz.module.atlantis.multiple;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component("atlantisMultipleCalculateStrategy")
public class AtlantisMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<SymbolDo> {
    private static final BigDecimal DEFAULT_MULTI = BigDecimal.valueOf(1);

    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<SymbolDo> rewardHandle, List<RewardLineDo<SymbolDo>> rewardLineList) {
        //不使用该方法
        return null;
    }

//    private BigDecimal getTotalMultipleOfCashReward(RewardLineDo<SymbolDo> rewardLine) {
//        AssertUtil.assertNotEmpty(rewardLine.getRewardSymbolList(), ErrorCode.ERR_INTER, "rewardSymbolList is null");
//        BigDecimal totalMultiple = BigDecimal.ZERO;
//        for (RewardSymbolDo<SymbolDo> rewardSymbolDo : rewardLine.getRewardSymbolList()) {
//            SymbolDo symbolDo = rewardSymbolDo.getSymbol();
//            if (Objects.isNull(symbolDo)) {
//                continue;
//            }
//            if (symbolDo.isCashSymbol()) {
//                totalMultiple = totalMultiple.add(symbolDo.getSymbolMultiple());
//            } else if (symbolDo.isWildSymbol()) {
//                SymbolDo nestSymbol = symbolDo.getNestSymbol();
//                AssertUtil.assertNotNull(nestSymbol, ErrorCode.ERR_INTER, "nestSymbol in wildSymbol can not be null");
//                totalMultiple = totalMultiple.add(nestSymbol.getSymbolMultiple());
//            }
//        }
//        return totalMultiple;
//    }
}
