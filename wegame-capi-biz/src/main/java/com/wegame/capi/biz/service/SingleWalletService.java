package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.api.domain.req.singlewallet.BetStatusReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.singlewallet.BetStatusRes;
import com.wegame.capi.api.domain.res.singlewallet.GetCreditRes;
import com.wegame.capi.api.domain.res.singlewallet.SingleWalletBetWithPayoutRes;
import com.wegame.capi.biz.builder.req.GetCreditReqBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetSingleWalletResultDo;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.SingleWalletBetStatusEnum;
import com.wegame.capi.common.enums.SingleWalletResStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.wegame.capi.biz.builder.req.BetStatusReqBuilder.buildStatusReq;
import static com.wegame.capi.biz.builder.req.BetWithPayoutReqBuilder.buildReq;
import static com.wegame.capi.common.enums.SingleWalletBetStatusEnum.valueOfCode;


/**
 * 单钱包服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class SingleWalletService {

    @Value("${api.sport.invoke:true}")
    boolean invoke;
    @Autowired
    SingleWalletApi singleWalletApi;

    public BetSingleWalletResultDo betWithPayout(BetRecordDo betRecordDo) {
        if (!invoke) {
            return BetSingleWalletResultDo.builder()
                    .betStatus(BetStatusEnum.SUCCESS)
                    .betId(String.valueOf(RandomUtils.nextInt()))
                    .build();
        }
        CommonRes<SingleWalletBetWithPayoutRes> commonRes = singleWalletApi.betWithPayout(buildReq(betRecordDo));
        // 成功或者重复的refId都当作成功处理
        if (commonRes.isSuc()) {
            AssertUtil.assertNotNull(commonRes.getData(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            return BetSingleWalletResultDo.builder()
                    .betId(commonRes.getData().getBetId())
                    .refId(commonRes.getData().getRefId())
                    .credit(commonRes.getData().getCredit())
                    .betStatus(BetStatusEnum.SUCCESS)
                    .build();
        } else if (Objects.equals(commonRes.getStatus(), SingleWalletResStatusEnum.DUPLICATE_REFERENCE_ID.getCode())) {
            log.warn("betWithPayout, duplicate reference id. betRecordDo: {}, commonRes: {}", betRecordDo, commonRes);
            // 获取投注状态
            BetStatusRes betStatusRes = getBetStatus(buildStatusReq(betRecordDo));
            SingleWalletBetStatusEnum singleWalletBetStatus = valueOfCode(betStatusRes.getCode());
            AssertUtil.assertNotNull(singleWalletBetStatus, ErrorCode.ERR_PARAM, "invalid bet status code");
            // 获取余额
            GetCreditRes creditRes = singleWalletApi.getCredit(GetCreditReqBuilder.build());
            return BetSingleWalletResultDo.builder()
                    .betId(betStatusRes.getBetId())
                    .betStatus(BetStatusEnum.valueOfSingleWalletBetStatus(singleWalletBetStatus))
                    .credit(creditRes.getCredit())
                    .build();
        } else {
            // 获取余额
            // GetCreditRes creditRes = singleWalletApi.getCredit(GetCreditReqBuilder.build());
            return BetSingleWalletResultDo.builder()
                    .betStatus(BetStatusEnum.FAIL)
                    .reason(Objects.equals(commonRes.getStatus(), SingleWalletResStatusEnum.INSUFFICIENT_CREDIT.getCode())
                            ? "Insufficient credit,  please recharge"
                            : commonRes.getMessage())
                    .build();
        }
    }

    public BetStatusRes getBetStatus(BetStatusReq req) {
        return singleWalletApi.getBetStatus(req);
    }
}