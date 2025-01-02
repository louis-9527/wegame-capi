//package com.wegame.capi.biz.module.probabilistic;
//
//import com.wegame.base.enums.ErrorCode;
//import com.wegame.base.exception.BusinessException;
//import com.wegame.capi.common.enums.BetSubTypeEnum;
//import com.wegame.capi.infrastructure.config.nacos.NacosMariaMakilingConfig;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.*;
//
///**
// * MariaMakiling概率策略
// */
//@Component
//public class MariaMakilingProbabilisticStrategy extends AbstractProbabilisticStrategy {
//
//    // todo
//    @Autowired
//    NacosMariaMakilingConfig nacosMariaMakilingConfig;
//
//    //    @Override
//    //    public Map<String, List<BigDecimal>> getProbabilisticConfigMap(BetSubTypeEnum betSubType) {
//    //
//    //        switch (betSubType) {
//    //            case NORMAL_BET:
//    //                return nacosMariaMakilingConfig.getCommonProbabilisticConfig();
//    //            case NON_BUY_FREE_SPINS_BET:
//    //            case BUY_FREE_SPINS_BET:
//    //                return nacosMariaMakilingConfig.getFreeProbabilisticConfig();
//    //            default:
//    //                throw new BusinessException(ErrorCode.ERR_PARAM, "NOT SUPPORTED");
//    //        }
//    //    }
//}
