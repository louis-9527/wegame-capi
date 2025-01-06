package com.wegame.capi.biz.service;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.api.domain.req.singlewallet.BetWithPayoutReq;
import com.wegame.capi.api.domain.req.singlewallet.BoxingBetStatusReq;
import com.wegame.capi.api.domain.req.singlewallet.GetCreditReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.singlewallet.BetWithPayoutRes;
import com.wegame.capi.api.domain.res.singlewallet.BoxingBetStatusRes;
import com.wegame.capi.api.domain.res.singlewallet.GetCreditRes;
import com.wegame.capi.biz.builder.req.GetCreditReqBuilder;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.enums.SingleWalletResStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;


/**
 * 单钱包服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class SingleWalletService {

    private static final int BET_TYPE = 2;

    @Autowired
    SingleWalletApi singleWalletApi;

    // todo


    public BetWithPayoutRes betWithPayout(BetWithPayoutReq req) {
        CommonRes<BetWithPayoutRes> commonRes = singleWalletApi.betWithPayout(req);
        // 成功或者重复的refId都当作成功处理
        if (commonRes.isSuc()) {
            AssertUtil.assertNotNull(commonRes.getData(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            return commonRes.getData();
        } else if (Objects.equals(commonRes.getStatus(), SingleWalletResStatusEnum.DUPLICATE_REFERENCE_ID.getCode())) {
            //获取返回状态
            return this.queryBetDetail(req);
        }
        throw new BusinessException(ErrorCode.ERR_INTER, commonRes.getMessage());
    }

    private BetWithPayoutRes queryBetDetail(BetWithPayoutReq req) {
        // 此时状态为成功,请求betStatus获取订单betId
        BoxingBetStatusReq betStatusReq = BoxingBetStatusReq.builder()
                .betType(BET_TYPE)
                .wagersDate(req.getBetDetails().getWagersDate())
                .refId(req.getRefId())
                .build();
        BoxingBetStatusRes betStatusRes = singleWalletApi.betStatus(betStatusReq);
        // 获取余额
        GetCreditRes creditRes = singleWalletApi.getCredit(GetCreditReqBuilder.build());
        return BetWithPayoutRes.builder()
                .betId(betStatusRes.getBetId())
                .playerName(req.getPlayerName())
                .credit(creditRes.getCredit())
                .build();
    }
}