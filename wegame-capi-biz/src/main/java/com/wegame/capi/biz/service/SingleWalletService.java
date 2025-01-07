package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.api.domain.req.singlewallet.BetWithPayoutReq;
import com.wegame.capi.api.domain.req.singlewallet.BetStatusReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.singlewallet.SingleWalletBetWithPayoutRes;
import com.wegame.capi.api.domain.res.singlewallet.BetStatusRes;
import com.wegame.capi.api.domain.res.singlewallet.GetCreditRes;
import com.wegame.capi.biz.builder.req.BetWithPayoutReqBuilder;
import com.wegame.capi.biz.builder.req.GetCreditReqBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetSingleWalletResultDo;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.SingleWalletBetStatusResStatusEnum;
import com.wegame.capi.common.enums.SingleWalletResStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * 单钱包服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class SingleWalletService {


    @Autowired
    SingleWalletApi singleWalletApi;

    // todo


    public BetSingleWalletResultDo betWithPayout(BetRecordDo betRecordDo) {
        BetWithPayoutReq betWithPayoutReq = BetWithPayoutReqBuilder.buildReq(betRecordDo);
        CommonRes<SingleWalletBetWithPayoutRes> commonRes = singleWalletApi.betWithPayout(betWithPayoutReq);
        // 成功或者重复的refId都当作成功处理
        if (commonRes.isSuc()) {
            AssertUtil.assertNotNull(commonRes.getData(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            BetSingleWalletResultDo betSingleWalletResult = BetWithPayoutReqBuilder.buildRes(commonRes.getData());
            betSingleWalletResult.setBetStatus(BetStatusEnum.SUCCESS);
            return betSingleWalletResult;
        } else if (Objects.equals(commonRes.getStatus(), SingleWalletResStatusEnum.DUPLICATE_REFERENCE_ID.getCode())) {
            // 获取返回状态
            return this.queryBetDetail(betRecordDo);
        } else {
            // 获取余额
            GetCreditRes creditRes = singleWalletApi.getCredit(GetCreditReqBuilder.build());
            return BetSingleWalletResultDo.builder()
                    .betStatus(BetStatusEnum.FAIL)
                    .credit(creditRes.getCredit())
                    .build();
        }
    }

    public Pair<String, BetStatusEnum> getBetStatus(BetStatusReq req) {
        // 此时状态为成功,请求betStatus获取订单betId
        CommonRes<BetStatusRes> commonRes = singleWalletApi.getBetStatus(req);
        BetStatusRes betStatusRes = commonRes.getData();
        SingleWalletBetStatusResStatusEnum betStatus = SingleWalletBetStatusResStatusEnum.valueOfCode(betStatusRes.getCode());
        AssertUtil.assertNotNull(betStatus, ErrorCode.ERR_PARAM, "invalid bet status code");
        switch (betStatus) {
            case RECEIVED_PAYOUT_REQUEST:
                return Pair.of(betStatusRes.getBetId(), BetStatusEnum.ING);
            case COMPLETED_BET_REQUEST:
                return Pair.of(betStatusRes.getBetId(), BetStatusEnum.SUCCESS);
            case DATA_NOT_FOUND:
                return Pair.of(betStatusRes.getBetId(), BetStatusEnum.FAIL);
            default:
                throw new BusinessException(ErrorCode.ERR_PARAM, "invalid bet status code");
        }
    }

    private BetSingleWalletResultDo queryBetDetail(BetRecordDo betRecordDo) {
        // 此时状态为成功,请求betStatus获取订单betId
        BetStatusReq betStatusReq = BetWithPayoutReqBuilder.buildStatusReq(betRecordDo);
        Pair<String, BetStatusEnum> betStatusRes = this.getBetStatus(betStatusReq);
        // 获取余额
        GetCreditRes creditRes = singleWalletApi.getCredit(GetCreditReqBuilder.build());
        return BetSingleWalletResultDo.builder()
                .betId(betStatusRes.getLeft())
                .betStatus(betStatusRes.getRight())
                .credit(creditRes.getCredit())
                .build();
    }
}