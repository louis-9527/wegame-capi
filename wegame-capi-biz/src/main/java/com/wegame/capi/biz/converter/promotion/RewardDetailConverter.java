package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.GetRewardDetailRes;
import com.wegame.capi.biz.domain.biz.promotion.RewardDetailDo;
import com.wegame.capi.client.domain.vo.promotion.RewardDetailVo;
import com.wegame.capi.common.utils.AmountUtil;

import java.util.Objects;


public class RewardDetailConverter {

    public static RewardDetailDo toDo(GetRewardDetailRes detailRes) {
        if (Objects.isNull(detailRes)) {
            return null;
        }
        RewardDetailDo rewordDetailDo = new RewardDetailDo();
        rewordDetailDo.setRewardConfigId(detailRes.getRewardConfigId());
        rewordDetailDo.setRefId(detailRes.getRefId());

        GetRewardDetailRes.RewardDetail resDetails = detailRes.getDetails();
        if (Objects.isNull(resDetails)) {
            return rewordDetailDo;
        }

        RewardDetailDo.RewardDetail rewordDetail = new RewardDetailDo.RewardDetail();
        rewordDetail.setName(resDetails.getName());
        rewordDetail.setDescription(resDetails.getDescription());
        rewordDetail.setBalanceRequired(AmountUtil.toLong(resDetails.getBalanceRequired()));
        rewordDetail.setImgUrl(resDetails.getImgUrl());
        rewordDetail.setGameId(resDetails.getGameId());
        rewordDetail.setGameCode(resDetails.getGameCode());
        rewordDetail.setGameName(resDetails.getGameName());
        rewordDetail.setRewardType(resDetails.getRewardType());
        rewordDetail.setBetValue(AmountUtil.toLong(resDetails.getBetValue()));
        rewordDetail.setStarLevel(resDetails.getStarLevel());
        rewordDetail.setPeriodDay(resDetails.getPeriodDay());
        rewordDetail.setPeriodHour(resDetails.getPeriodHour());
        rewordDetailDo.setDetails(rewordDetail);
        return rewordDetailDo;
    }

    public static RewardDetailVo doToVo(RewardDetailDo rewordDetailDo) {
        if (Objects.isNull(rewordDetailDo)) {
            return null;
        }
        RewardDetailVo detailVo = new RewardDetailVo();
        detailVo.setRefId(rewordDetailDo.getRefId());
        detailVo.setRewardConfigId(rewordDetailDo.getRewardConfigId());
        RewardDetailDo.RewardDetail details = rewordDetailDo.getDetails();
        if(Objects.isNull(details)){
            return detailVo;
        }
        RewardDetailVo.Detail vo = new RewardDetailVo.Detail();
        vo.setName(details.getName());
        vo.setDescription(details.getDescription());
        vo.setBalanceRequired(details.getBalanceRequired());
        vo.setImgUrl(details.getImgUrl());
        vo.setSkipGameId(details.getGameId());
        vo.setGameId(details.getGameCode());
        vo.setGameName(details.getGameName());
        vo.setRewardType(details.getRewardType());
        vo.setBetValue(details.getBetValue());
        vo.setStarLevel(details.getStarLevel());
        vo.setPeriodDay(details.getPeriodDay());
        vo.setPeriodHour(details.getPeriodHour());
        detailVo.setDetails(vo);
        return detailVo;
    }
}
