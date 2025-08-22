package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.contract.RedeemPlayerContractRes;
import com.wegame.capi.api.domain.res.promotion.RedeemTreasureRes;
import com.wegame.capi.biz.domain.biz.contract.RedeemPlayerContractDo;
import com.wegame.capi.biz.domain.biz.promotion.RedeemTreasureDo;
import com.wegame.capi.client.domain.vo.contract.RedeemPlayerContractVo;
import com.wegame.capi.client.domain.vo.promotion.RedeemTreasureVo;

import java.util.Objects;

public class RedeemPlayerContractConverter {

    public static RedeemPlayerContractDo resToDo(RedeemPlayerContractRes res) {
        if (Objects.isNull(res)) {
            return null;
        }
        return RedeemPlayerContractDo.builder()
                .isClaimed(res.getIsClaimed())
                .rewardType(res.getRewardType())
                .rewardInfo(resToDo(res.getRewardInfo()))
                .build();
    }

    public static RedeemPlayerContractDo.ContractRewardInfo resToDo(RedeemPlayerContractRes.ContractRewardInfo res) {
        if (Objects.isNull(res)) {
            return null;
        }
        return RedeemPlayerContractDo.ContractRewardInfo.builder()
                .gameId(res.getGameId())
                .gameCode(res.getGameCode())
                .gameName(res.getGameName())
                .imgURL(res.getImgURL())
                .rewardName(res.getRewardName())
                .rewardConfigId(res.getRewardConfigId())
                .starLevel(res.getStarLevel())
                .betValue(res.getBetValue())
                .balanceRequired(res.getBalanceRequired())
                .campaignId(res.getCampaignId())
                .freeBet(res.getFreeBet())
                .count(res.getCount())
                .build();
    }


    public static RedeemPlayerContractVo doToVo(RedeemPlayerContractDo redeemPlayerContractDo) {
        if (Objects.isNull(redeemPlayerContractDo)) {
            return null;
        }
        return RedeemPlayerContractVo.builder()
                .isClaimed(redeemPlayerContractDo.getIsClaimed())
                .rewardType(redeemPlayerContractDo.getRewardType())
                .rewardInfo(doToVo(redeemPlayerContractDo.getRewardInfo()))
                .build();
    }

    public static RedeemPlayerContractVo.ContractRewardInfo doToVo(RedeemPlayerContractDo.ContractRewardInfo doRewardInfo) {
        if (Objects.isNull(doRewardInfo)) {
            return null;
        }
        return RedeemPlayerContractVo.ContractRewardInfo.builder()
                .gameId(doRewardInfo.getGameId())
                .gameCode(doRewardInfo.getGameCode())
                .gameName(doRewardInfo.getGameName())
                .imgURL(doRewardInfo.getImgURL())
                .rewardName(doRewardInfo.getRewardName())
                .rewardConfigId(doRewardInfo.getRewardConfigId())
                .starLevel(doRewardInfo.getStarLevel())
                .betValue(doRewardInfo.getBetValue())
                .balanceRequired(doRewardInfo.getBalanceRequired())
                .campaignId(doRewardInfo.getCampaignId())
                .freeBet(doRewardInfo.getFreeBet())
                .count(doRewardInfo.getCount())
                .build();
    }
}
