package com.wegame.capi.biz.service;

import cn.hutool.core.util.RandomUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.api.domain.req.singlewallet.BetStatusReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.singlewallet.AuthLoginTokenRes;
import com.wegame.capi.api.domain.res.singlewallet.BetStatusRes;
import com.wegame.capi.api.domain.res.singlewallet.GetCreditRes;
import com.wegame.capi.api.domain.res.singlewallet.SingleWalletBetWithPayoutRes;
import com.wegame.capi.biz.builder.req.BetStatusReqBuilder;
import com.wegame.capi.biz.builder.req.GetAuthLoginTokenReqBuilder;
import com.wegame.capi.biz.builder.req.GetCreditReqBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.user.UserLoginDo;
import com.wegame.capi.biz.service.freespin.FreeSpinService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.SingleWalletBetStatusEnum;
import com.wegame.capi.common.enums.SingleWalletResStatusEnum;
import com.wegame.capi.common.utils.AmountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.locks.LockSupport;

import static com.wegame.capi.biz.builder.req.BetWithPayoutReqBuilder.*;
import static com.wegame.capi.common.enums.SingleWalletBetStatusEnum.valueOfCode;


/**
 * 单钱包服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class SingleWalletService {

    @Value("${api.game.invokeSingle}")
    boolean invokeSingle;
    @Autowired
    SingleWalletApi singleWalletApi;
    @Autowired
    BetStatusService betStatusService;
    @Autowired
    FreeSpinService freeSpinService;

    public boolean authLoginToken(UserLoginDo userLoginDo) {
        AuthLoginTokenRes res = singleWalletApi.authLoginToken(GetAuthLoginTokenReqBuilder.buildReq(userLoginDo));
        AssertUtil.assertNotNull(res, ErrorCode.ERR_NET);
        return true;
    }

    public BigDecimal getCredit() {
        GetCreditRes res = singleWalletApi.getCredit(GetCreditReqBuilder.buildReq());
        AssertUtil.assertNotNull(res, ErrorCode.ERR_NET);
        AssertUtil.assertNotNull(res.getCredit(), ErrorCode.ERR_NET);
        return res.getCredit();
    }

    public SingleWalletBetResultDo betWithPayout(BetRecordDo betRecordDo) {
        if (!invokeSingle) {
            return SingleWalletBetResultDo.builder()
                    .betId(RandomUtil.randomNumbers(8))
                    .refId(RandomUtil.randomString(10))
                    .credit(RandomUtil.randomLong())
                    .betStatus(BetStatusEnum.SUCCESS)
                    .build();
        }
        CommonRes<SingleWalletBetWithPayoutRes> commonRes = null;
        try {
            if (Objects.equals(BetPromotionTypeEnum.FREE_SPIN.getCode(), betRecordDo.getBetPromotionType())) {
                commonRes = freeSpinService.freeSpin(buildFreeSpinReq(betRecordDo));
            } else if (Objects.equals(BetPromotionTypeEnum.STAR_CARD.getCode(), betRecordDo.getBetPromotionType())) {
                commonRes = singleWalletApi.starSpin(buildStarSpinReq(betRecordDo));
            } else {
                commonRes = singleWalletApi.betWithPayout(buildReq(betRecordDo));
            }
            AssertUtil.assertNotNull(commonRes, ErrorCode.ERR_NET);
            AssertUtil.assertNotNull(commonRes.getData(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
        } catch (Exception e) {
            log.error("betWithPayout error, betRecordId: {}, res: {}", betRecordDo.getId(), commonRes, e);
            betStatusService.asyncConvertBetStatus(betRecordDo, GameTypeEnum.valueOfId(betRecordDo.getGameId()));
            return SingleWalletBetResultDo.builder()
                    .betStatus(BetStatusEnum.FAIL)
                    .errorCode(ErrorCode.ERR_NET)
                    .errorMsg(ErrorCode.ERR_NET.getMsg())
                    .build();
        }
        if (Objects.equals(commonRes.getStatus(), SingleWalletResStatusEnum.SUCCESS.getCode())) {
            return SingleWalletBetResultDo.builder()
                    .betId(commonRes.getData().getBetId())
                    .refId(commonRes.getData().getRefId())
                    .credit(AmountUtil.toLong(commonRes.getData().getCredit()))
                    .betStatus(BetStatusEnum.SUCCESS)
                    .build();
        } else if (Objects.equals(commonRes.getStatus(), SingleWalletResStatusEnum.DUPLICATE_REFERENCE_ID.getCode())) {
            // 重复的refId当作成功处理(返回用户成功)
            log.warn("betWithPayout result unknown, duplicate reference id. betRecordId：{} commonRes: {}", betRecordDo.getId(), commonRes);
            if (Objects.equals(BetPromotionTypeEnum.FREE_SPIN.getCode(), betRecordDo.getBetPromotionType())) {
                log.warn("betWithPayout result unknown, duplicate reference id in free spin scene. betRecordId：{} commonRes: {}", betRecordDo.getId(), commonRes);
                // 获取余额
                BigDecimal userCredit = getUserCredit();
                return SingleWalletBetResultDo.builder()
                        .betId("")
                        .betStatus(BetStatusEnum.SUCCESS)
                        .credit(Objects.nonNull(userCredit) ? AmountUtil.toLong(userCredit) : null)
                        .build();
            } else {
                // 查询投注状态
                BetStatusRes betStatusRes = getBetStatus(BetStatusReqBuilder.buildReq(betRecordDo));
                SingleWalletBetStatusEnum singleWalletBetStatus = valueOfCode(betStatusRes.getCode());
                AssertUtil.assertNotNull(singleWalletBetStatus, ErrorCode.ERR_PARAM, "invalid bet status code");
                // 查询投注状态结果为3， 则多一次重试-------start
                if (Objects.equals(SingleWalletBetStatusEnum.RECEIVED_PAYOUT_REQUEST, singleWalletBetStatus)) {
                    LockSupport.parkNanos(2000000000L);
                    betStatusRes = getBetStatus(BetStatusReqBuilder.buildReq(betRecordDo));
                    singleWalletBetStatus = valueOfCode(betStatusRes.getCode());
                    log.info("more one time getBetStatus, code: {}", betStatusRes.getCode());
                }
                // 查询投注状态结果为3， 则多一次重试-------end
                if (Objects.equals(SingleWalletBetStatusEnum.RECEIVED_PAYOUT_REQUEST, singleWalletBetStatus)) {
                    betStatusService.asyncConvertBetStatus(betRecordDo, GameTypeEnum.valueOfId(betRecordDo.getGameId()));
                }
                // 获取余额
                BigDecimal userCredit = getUserCredit();
                return SingleWalletBetResultDo.builder()
                        .betId(betStatusRes.getBetId())
                        .betStatus(BetStatusEnum.valueOfSingleWalletBetStatus(singleWalletBetStatus))
                        .credit(Objects.nonNull(userCredit) ? AmountUtil.toLong(userCredit) : null)
                        .build();
            }
        } else if (Objects.equals(commonRes.getStatus(), SingleWalletResStatusEnum.INSUFFICIENT_CREDIT.getCode())) {
            return SingleWalletBetResultDo.builder()
                    .betStatus(BetStatusEnum.FAIL)
                    .errorCode(ErrorCode.INSUFFICIENT_CREDIT)
                    .errorMsg(commonRes.getErrorMessage())
                    .build();
        } else {
            return SingleWalletBetResultDo.builder()
                    .betStatus(BetStatusEnum.FAIL)
                    .errorCode(ErrorCode.ERR_NET)
                    .errorMsg(commonRes.getErrorMessage())
                    .build();
        }
    }

    public BetStatusRes getBetStatus(BetStatusReq req) {
        return singleWalletApi.getBetStatus(req);
    }

    private BigDecimal getUserCredit() {
        try {
            return getCredit();
        } catch (Exception e) {
            log.error("getUserCredit error, errMsg: {}", e, e.getMessage());
            return null;
        }
    }
}