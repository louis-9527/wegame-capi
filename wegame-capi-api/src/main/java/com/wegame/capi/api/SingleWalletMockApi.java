package com.wegame.capi.api;

import cn.hutool.core.lang.UUID;
import com.google.common.collect.Lists;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.capi.api.domain.req.singlewallet.*;
import com.wegame.capi.api.domain.res.singlewallet.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

/**
 * todo 在对接单钱包之前自行mock,对接单钱包之后删除@jayni
 */
@Slf4j
//@Component
public class SingleWalletMockApi extends SingleWalletApi {
//
//    @Retryable(value = RetryException.class, exclude = BusinessException.class)
//    public BoxingBetRes boxingBet(BoxingBetReq req) {
//        return BoxingBetRes.builder()
//                .refId(req.getRefId())
//                .playerName(req.getPlayerName())
//                .betId(UUID.fastUUID().toString())
//                .credit(BigDecimal.valueOf(1000000.55))
//                .build();
//    }
//
//
//    @Retryable(value = RetryException.class, exclude = BusinessException.class)
//    public SingleWalletCommonRes<BoxingPayoutRes> boxingPayout(BoxingPayoutReq req) {
//        return SingleWalletCommonRes.<BoxingPayoutRes>builder()
//                .status(200)
//                .data(BoxingPayoutRes.builder()
//                        .refId(req.getRefId())
//                        .playerName(req.getPlayerName())
//                        .betId(Lists.newArrayList(UUID.fastUUID().toString()))
//                        .credit(BigDecimal.valueOf(100000.55))
//                        .build())
//                .build();
//    }
//
//    @Override
//    @Retryable(value = RetryException.class, exclude = BusinessException.class)
//    public AwardRes award(AwardReq req) {
//        return AwardRes.builder()
//                .refId(req.getRefId())
//                .playerName(req.getPlayerName())
//                .betId(UUID.fastUUID().toString())
//                .credit(BigDecimal.valueOf(100000.55))
//                .build();
//    }
}
