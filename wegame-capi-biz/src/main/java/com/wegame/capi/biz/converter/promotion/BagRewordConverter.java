package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.GetBagCurrentRewardRes;
import com.wegame.capi.api.domain.res.promotion.GetBagOtherRewardRes;
import com.wegame.capi.biz.domain.biz.promotion.BagCurrentRewordDetailDo;
import com.wegame.capi.biz.domain.biz.promotion.BagOtherRewordDetailDo;
import com.wegame.capi.client.domain.vo.promotion.BagOtherRewardDetailVo;
import com.wegame.capi.client.domain.vo.promotion.BagRewardDetailVo;
import com.wegame.capi.common.utils.AmountUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class BagRewordConverter {

    public static BagCurrentRewordDetailDo currentResToDo(GetBagCurrentRewardRes.GetBagCurrentRewardRecord rewardBag) {
        if (rewardBag == null) {
            return null;
        }
        BagCurrentRewordDetailDo rewordDetailDo = new BagCurrentRewordDetailDo();
        rewordDetailDo.setRewardId(rewardBag.getRewardId());
        rewordDetailDo.setName(rewardBag.getName());
        rewordDetailDo.setDescription(rewardBag.getDescription());
        rewordDetailDo.setBalanceRequired(AmountUtil.toLong(rewardBag.getBalanceRequired()));
        rewordDetailDo.setSkipGameId(rewardBag.getGameId());
        rewordDetailDo.setGameId(rewardBag.getGameCode());
        rewordDetailDo.setGameName(rewardBag.getGameName());
        rewordDetailDo.setEffectTime(rewardBag.getRewardDate().getTime());
        rewordDetailDo.setExpiredTime(rewardBag.getExpiryDate().getTime());
        rewordDetailDo.setRewardType(rewardBag.getRewardType());
        rewordDetailDo.setBetValue(AmountUtil.toLong(rewardBag.getBetValue()));
        rewordDetailDo.setImgUrl(rewardBag.getImgUrl());
        return rewordDetailDo;
    }

    public static List<BagCurrentRewordDetailDo> currentResToDoList(List<GetBagCurrentRewardRes.GetBagCurrentRewardRecord> rewardBagList) {
        return Optional.ofNullable(rewardBagList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BagRewordConverter::currentResToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public static BagRewardDetailVo currentDoToVo(BagCurrentRewordDetailDo detailDo) {
        if (detailDo == null) {
            return null;
        }
        BagRewardDetailVo detailVo = new BagRewardDetailVo();
        detailVo.setRewardId(detailDo.getRewardId());
        detailVo.setName(detailDo.getName());
        detailVo.setDescription(detailDo.getDescription());
        detailVo.setBalanceRequired(detailDo.getBalanceRequired());
        detailVo.setSkipGameId(detailDo.getSkipGameId());
        detailVo.setGameId(detailDo.getGameId());
        detailVo.setGameName(detailDo.getGameName());
        detailVo.setEffectTime(detailDo.getEffectTime());
        detailVo.setExpiredTime(detailDo.getExpiredTime());
        detailVo.setRewardType(detailDo.getRewardType());
        detailVo.setBetValue(detailDo.getBetValue());
        detailVo.setImgUrl(detailDo.getImgUrl());
        return detailVo;
    }

    public static List<BagRewardDetailVo> currentDoToVoList(List<BagCurrentRewordDetailDo> detailDoList){
        return Optional.ofNullable(detailDoList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BagRewordConverter::currentDoToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public static BagOtherRewordDetailDo otherResToDo(GetBagOtherRewardRes.GetBagOtherRewardRecord rewardBag) {
        if (rewardBag == null) {
            return null;
        }
        BagOtherRewordDetailDo rewordDetailDo = new BagOtherRewordDetailDo();
        rewordDetailDo.setRewardId(rewardBag.getRewardId());
        rewordDetailDo.setName(rewardBag.getName());
        rewordDetailDo.setDescription(rewardBag.getDescription());
        rewordDetailDo.setSkipGameId(rewardBag.getGameId());
        rewordDetailDo.setGameId(rewardBag.getGameCode());
        rewordDetailDo.setGameName(rewardBag.getGameName());
        rewordDetailDo.setImgUrl(rewardBag.getImgUrl());
        rewordDetailDo.setTotalCount(rewardBag.getTotalCount());

        return rewordDetailDo;
    }

    public static List<BagOtherRewordDetailDo> otherResToDoList(List<GetBagOtherRewardRes.GetBagOtherRewardRecord> rewardBagList) {
        return Optional.ofNullable(rewardBagList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BagRewordConverter::otherResToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public static BagOtherRewardDetailVo otherDoToVo(BagOtherRewordDetailDo detailDo) {
        if (detailDo == null) {
            return null;
        }
        BagOtherRewardDetailVo detailVo = new BagOtherRewardDetailVo();
        detailVo.setRewardId(detailDo.getRewardId());
        detailVo.setName(detailDo.getName());
        detailVo.setDescription(detailDo.getDescription());
        detailVo.setSkipGameId(detailDo.getSkipGameId());
        detailVo.setGameId(detailDo.getGameId());
        detailVo.setGameName(detailDo.getGameName());
        detailVo.setImgUrl(detailDo.getImgUrl());
        detailVo.setTotalCount(detailDo.getTotalCount());
        return detailVo;
    }

    public static List<BagOtherRewardDetailVo> otherDoToVoList(List<BagOtherRewordDetailDo> detailDoList){
        return Optional.ofNullable(detailDoList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BagRewordConverter::otherDoToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
