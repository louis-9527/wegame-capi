package com.wegame.capi.biz.service;

import cn.hutool.core.util.RandomUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.api.domain.req.singlewallet.BetStatusReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.singlewallet.*;
import com.wegame.capi.biz.builder.req.BetStatusReqBuilder;
import com.wegame.capi.biz.builder.req.GetAuthLoginTokenReqBuilder;
import com.wegame.capi.biz.builder.req.GetCreditReqBuilder;
import com.wegame.capi.biz.builder.req.JackpotReqBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletJackpotResultDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotAwardDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.biz.domain.user.UserLoginDo;
import com.wegame.capi.biz.service.freespin.FreeSpinService;
import com.wegame.capi.biz.service.jackpot.JackpotRewardPoolService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.enums.*;
import com.wegame.capi.common.enums.*;
import com.wegame.capi.common.utils.AmountUtil;
import com.wegame.capi.infrastructure.config.properties.GameApiProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
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

    @Autowired
    GameApiProperty gameApiProperty;
    @Autowired
    SingleWalletApi singleWalletApi;
    @Autowired
    BetStatusService betStatusService;
    @Autowired
    FreeSpinService freeSpinService;

    @Autowired
    JackpotRewardPoolService jackpotRewardPoolService;

    public boolean authLoginToken(UserLoginDo userLoginDo) {
        if (!gameApiProperty.isInvokeSingle()) {
            return true;
        } else {
            AuthLoginTokenRes res = singleWalletApi.authLoginToken(GetAuthLoginTokenReqBuilder.buildReq(userLoginDo));
            AssertUtil.assertNotNull(res, ErrorCode.ERR_NET);
            return true;
        }
    }

    public BigDecimal getCredit() {
        GetCreditRes res = singleWalletApi.getCredit(GetCreditReqBuilder.buildReq());
        AssertUtil.assertNotNull(res, ErrorCode.ERR_NET);
        AssertUtil.assertNotNull(res.getCredit(), ErrorCode.ERR_NET);
        return res.getCredit();
    }

    public SingleWalletBetResultDo betWithPayout(BetRecordDo betRecordDo) {
        if (!gameApiProperty.isInvokeSingle()) {
            return SingleWalletBetResultDo.builder()
                    .betId(RandomUtil.randomNumbers(8))
                    .refId(RandomUtil.randomString(10))
                    .credit(RandomUtil.randomLong())
                    .betStatus(BetStatusEnum.SUCCESS)
                    .build();
        }
        CommonRes<SingleWalletBetWithPayoutRes> commonRes = null;
        try {
            //todo 生成jackpot分成消息
            GameTypeEnum gameType = GameTypeEnum.valueOfId(betRecordDo.getGameId());
            JackpotTypeEnum jackpotType = JackpotTypeEnum.valueOfGameType(gameType);
            // 当前游戏支持jackpot则需要用jackpot 构建请求
            if (Objects.nonNull(jackpotType)) {
                if (Objects.equals(BetPromotionTypeEnum.FREE_SPIN.getCode(), betRecordDo.getBetPromotionType())) {
                    commonRes = freeSpinService.freeSpin(buildFreeSpinReq(betRecordDo));
                } else if (Objects.equals(BetPromotionTypeEnum.STAR_CARD.getCode(), betRecordDo.getBetPromotionType())) {
                    commonRes = singleWalletApi.starSpin(buildStarSpinReq(betRecordDo, jackpotRewardPoolService.listAllWithCache(jackpotType)));
                } else {
                    commonRes = singleWalletApi.betWithPayout(buildReq(betRecordDo, jackpotRewardPoolService.listAllWithCache(jackpotType)));
                }
            } else {
                if (Objects.equals(BetPromotionTypeEnum.FREE_SPIN.getCode(), betRecordDo.getBetPromotionType())) {
                    commonRes = freeSpinService.freeSpin(buildFreeSpinReq(betRecordDo));
                } else if (Objects.equals(BetPromotionTypeEnum.STAR_CARD.getCode(), betRecordDo.getBetPromotionType())) {
                    commonRes = singleWalletApi.starSpin(buildStarSpinReq(betRecordDo));
                } else {
                    commonRes = singleWalletApi.betWithPayout(buildReq(betRecordDo));
                }
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
        } else if(Objects.equals(commonRes.getStatus(), SingleWalletResStatusEnum.INVALID_BET.getCode())){
            return SingleWalletBetResultDo.builder()
                    .betStatus(BetStatusEnum.FAIL)
                    .errorCode(ErrorCode.INVALID_BET)
                    .errorMsg(commonRes.getErrorMessage())
                    .build();
        } else {
            if (Objects.equals(BetPromotionTypeEnum.FREE_SPIN.getCode(), betRecordDo.getBetPromotionType())) {
                FreeSpinErrorCode freeSpinErrorCode = FreeSpinErrorCode.valueOfThirdCode(commonRes.getStatus());
                return SingleWalletBetResultDo.builder()
                        .betStatus(BetStatusEnum.FAIL)
                        .errorCode(Objects.isNull(freeSpinErrorCode) ? ErrorCode.ERR_NET : freeSpinErrorCode.getErrorCode())
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
    }

    public SingleWalletJackpotResultDo jackpot(JackpotAwardDo jackpotAwardDo, Map<JackpotRewardPoolTypeEnum, JackpotRewardPoolDo> jackpotRewardPoolMap) {
        CommonRes<JackpotRes> jackpot = singleWalletApi.jackpot(JackpotReqBuilder.buildReq(jackpotAwardDo, jackpotRewardPoolMap));
        return SingleWalletJackpotResultDo.builder()
                .success(jackpot.isSuc())
                .status(jackpot.getStatus())
                .message(jackpot.getMessage())
                .resultData(SingleWalletJackpotResultDo.resultData.builder()
                        .playerName(jackpot.getData().getPlayerName())
                        .credit(AmountUtil.toLong(jackpot.getData().getCredit()))
                        .refId(jackpot.getData().getRefId())
                        .build())
                .build();

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