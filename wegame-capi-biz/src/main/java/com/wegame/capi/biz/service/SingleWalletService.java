package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.api.domain.req.singlewallet.BetStatusReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.singlewallet.AuthLoginTokenRes;
import com.wegame.capi.api.domain.res.singlewallet.BetStatusRes;
import com.wegame.capi.api.domain.res.singlewallet.GetCreditRes;
import com.wegame.capi.api.domain.res.singlewallet.SingleWalletBetWithPayoutRes;
import com.wegame.capi.biz.builder.req.GetAuthLoginTokenReqBuilder;
import com.wegame.capi.biz.builder.req.GetCreditReqBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetSingleWalletResultDo;
import com.wegame.capi.biz.domain.user.UserLoginDo;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.SingleWalletBetStatusEnum;
import com.wegame.capi.common.enums.SingleWalletResStatusEnum;
import com.wegame.capi.common.utils.AmountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
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

    @Autowired
    SingleWalletApi singleWalletApi;

    public boolean authLoginToken(UserLoginDo userLoginDo) {
        AuthLoginTokenRes res = singleWalletApi.authLoginToken(GetAuthLoginTokenReqBuilder.build(userLoginDo));
        AssertUtil.assertNotNull(res, ErrorCode.ERR_NET);
        return true;
    }

    public BigDecimal getCredit() {
        GetCreditRes res = singleWalletApi.getCredit(GetCreditReqBuilder.build());
        AssertUtil.assertNotNull(res, ErrorCode.ERR_NET);
        AssertUtil.assertNotNull(res.getCredit(), ErrorCode.ERR_NET);
        return res.getCredit();
    }

    public BetSingleWalletResultDo betWithPayout(BetRecordDo betRecordDo) {
        CommonRes<SingleWalletBetWithPayoutRes> commonRes = null;
        try {
            commonRes = singleWalletApi.betWithPayout(buildReq(betRecordDo));
            AssertUtil.assertNotNull(commonRes, ErrorCode.ERR_NET);
            AssertUtil.assertNotNull(commonRes.getData(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
        } catch (Exception e) {
            // 接口异常， 则异步线程处理（通过查询投注状态，去扭转投注状态）
            // todo
        }
        if (Objects.equals(commonRes.getStatus(), SingleWalletResStatusEnum.SUCCESS.getCode())) {
            return BetSingleWalletResultDo.builder()
                    .betId(commonRes.getData().getBetId())
                    .refId(commonRes.getData().getRefId())
                    .credit(AmountUtil.toLong(commonRes.getData().getCredit()))
                    .betStatus(BetStatusEnum.SUCCESS)
                    .build();
        } else if (Objects.equals(commonRes.getStatus(), SingleWalletResStatusEnum.DUPLICATE_REFERENCE_ID.getCode())) {
            // 重复的refId当作成功处理
            log.warn("betWithPayout, duplicate reference id. commonRes: {}", commonRes);
            // 获取投注状态 todo
            BetStatusRes betStatusRes = getBetStatus(buildStatusReq(betRecordDo));
            SingleWalletBetStatusEnum singleWalletBetStatus = valueOfCode(betStatusRes.getCode());
            AssertUtil.assertNotNull(singleWalletBetStatus, ErrorCode.ERR_PARAM, "invalid bet status code");
            // 获取余额
            BigDecimal credit = null;
            try {
                credit = getCredit();
            } catch (Exception e) {
                log.error("getCredit error", e);
            }
            return BetSingleWalletResultDo.builder()
                    .betId(betStatusRes.getBetId())
                    .betStatus(BetStatusEnum.valueOfSingleWalletBetStatus(singleWalletBetStatus))
                    .credit(Objects.nonNull(credit) ? AmountUtil.toLong(credit) : null)
                    .build();
        } else if (Objects.equals(commonRes.getStatus(), SingleWalletResStatusEnum.INSUFFICIENT_CREDIT.getCode())) {
            return BetSingleWalletResultDo.builder()
                    .betStatus(BetStatusEnum.FAIL)
                    .errorCode(ErrorCode.INSUFFICIENT_CREDIT)
                    .errorMsg(commonRes.getErrorMessage())
                    .build();
        } else {
            return BetSingleWalletResultDo.builder()
                    .betStatus(BetStatusEnum.FAIL)
                    .errorCode(ErrorCode.ERR_NET)
                    .errorMsg(commonRes.getErrorMessage())
                    .build();
        }
    }

    public BetStatusRes getBetStatus(BetStatusReq req) {
        return singleWalletApi.getBetStatus(req);
    }

    //    public boolean verifyHash(String channelId, String playerName, Date createTime, String refId, String hash) {
    //        Pair<String, String> privateKeyAndIv = singleWalletApi.getPrivateKeyAndIvByChannelId(channelId);
    //        String verifyHash = singleWalletApi.hash(channelId, playerName, DateUtil.toUTC(createTime), refId, privateKeyAndIv.getLeft(), privateKeyAndIv.getRight());
    //        AssertUtil.assertTrue(StringUtils.equals(hash, verifyHash), ErrorCode.ERR_PARAM);
    //        return true;
    //    }
}