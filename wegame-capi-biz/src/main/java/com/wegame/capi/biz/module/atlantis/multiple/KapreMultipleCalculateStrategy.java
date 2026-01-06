package com.wegame.capi.biz.module.atlantis.multiple;//package com.wegame.capi.biz.module.kapre.multiple;
//
//import com.wegame.base.enums.ErrorCode;
//import com.wegame.base.utils.AssertUtil;
//import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
//import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
//import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
//import com.wegame.capi.biz.domain.biz.symbol.KapreSymbolDo;
//import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Objects;
//
//@Slf4j
//@Component("kapreMultipleCalculateStrategy")
//public class KapreMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<KapreSymbolDo> {
//    private static final BigDecimal DEFAULT_MULTI = BigDecimal.valueOf(1);
//
//    @Override
//    public BigDecimal calculateMultiple(RewardHandleDo<KapreSymbolDo> rewardHandle, List<RewardLineDo<KapreSymbolDo>> rewardLineList) {
//        //不使用该方法
//        return null;
//    }
//
//    @Override
//    public BigDecimal calculateMultipleOfSingleRewardLine(RewardHandleDo<KapreSymbolDo> rewardHandle, RewardLineDo<KapreSymbolDo> rewardLine) {
//        AssertUtil.assertNotNull(rewardLine, ErrorCode.ERR_INTER, "rewardLine is null");
//        if (rewardLine.isCashReward()) {
//            return getTotalMultipleOfCashReward(rewardLine);
//        } else {
//            return DEFAULT_MULTI;
//        }
//    }
//
//    private BigDecimal getTotalMultipleOfCashReward(RewardLineDo<KapreSymbolDo> rewardLine) {
//        AssertUtil.assertNotEmpty(rewardLine.getRewardSymbolList(), ErrorCode.ERR_INTER, "rewardSymbolList is null");
//        BigDecimal totalMultiple = BigDecimal.ZERO;
//        for (RewardSymbolDo<KapreSymbolDo> rewardSymbolDo : rewardLine.getRewardSymbolList()) {
//            KapreSymbolDo symbolDo = rewardSymbolDo.getSymbol();
//            if (Objects.isNull(symbolDo)) {
//                continue;
//            }
//            if (symbolDo.isCashSymbol()) {
//                totalMultiple = totalMultiple.add(symbolDo.getSymbolMultiple());
//            } else if (symbolDo.isWildSymbol()) {
//                KapreSymbolDo nestSymbol = symbolDo.getNestSymbol();
//                AssertUtil.assertNotNull(nestSymbol, ErrorCode.ERR_INTER, "nestSymbol in wildSymbol can not be null");
//                totalMultiple = totalMultiple.add(nestSymbol.getSymbolMultiple());
//            }
//        }
//        return totalMultiple;
//    }
//}
