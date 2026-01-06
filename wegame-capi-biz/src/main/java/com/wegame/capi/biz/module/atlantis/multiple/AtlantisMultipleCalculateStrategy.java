package com.wegame.capi.biz.module.atlantis.multiple;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.AtlantisSymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component("atlantisMultipleCalculateStrategy")
public class AtlantisMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<AtlantisSymbolDo> {
    private static final BigDecimal DEFAULT_MULTI = BigDecimal.valueOf(1);

    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<AtlantisSymbolDo> rewardHandle, List<RewardLineDo<AtlantisSymbolDo>> rewardLineList) {
        //不使用该方法
        return null;
    }

//    private BigDecimal getTotalMultipleOfCashReward(RewardLineDo<AtlantisSymbolDo> rewardLine) {
//        AssertUtil.assertNotEmpty(rewardLine.getRewardSymbolList(), ErrorCode.ERR_INTER, "rewardSymbolList is null");
//        BigDecimal totalMultiple = BigDecimal.ZERO;
//        for (RewardSymbolDo<AtlantisSymbolDo> rewardSymbolDo : rewardLine.getRewardSymbolList()) {
//            AtlantisSymbolDo symbolDo = rewardSymbolDo.getSymbol();
//            if (Objects.isNull(symbolDo)) {
//                continue;
//            }
//            if (symbolDo.isCashSymbol()) {
//                totalMultiple = totalMultiple.add(symbolDo.getSymbolMultiple());
//            } else if (symbolDo.isWildSymbol()) {
//                AtlantisSymbolDo nestSymbol = symbolDo.getNestSymbol();
//                AssertUtil.assertNotNull(nestSymbol, ErrorCode.ERR_INTER, "nestSymbol in wildSymbol can not be null");
//                totalMultiple = totalMultiple.add(nestSymbol.getSymbolMultiple());
//            }
//        }
//        return totalMultiple;
//    }
}
