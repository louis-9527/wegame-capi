package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.GetBagCurrentRewardRes;
import com.wegame.capi.biz.domain.biz.promotion.BagCurrentRewardDetailDo;
import com.wegame.capi.client.domain.vo.promotion.BagRewardDetailVo;
import com.wegame.capi.common.utils.AmountUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class BagRewardConverter {

    public static List<BagRewardDetailVo> toVoList(List<BagCurrentRewardDetailDo> detailDoList) {
        return Optional.ofNullable(detailDoList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BagRewardConverter::toVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static BagRewardDetailVo toVo(BagCurrentRewardDetailDo detailDo) {
        if (Objects.isNull(detailDo)) {
            return null;
        }
        BagRewardDetailVo detailVo = new BagRewardDetailVo();
        detailVo.setRewardId(detailDo.getRewardId());
        detailVo.setRewardConfigId(detailDo.getRewardConfigId());
        detailVo.setName(detailDo.getName());
        detailVo.setStarLevel(detailDo.getStarLevel());
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

    public static List<BagCurrentRewardDetailDo> toDoList(List<GetBagCurrentRewardRes.GetBagCurrentRewardRecord> rewardBagList) {
        return Optional.ofNullable(rewardBagList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BagRewardConverter::toDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static BagCurrentRewardDetailDo toDo(GetBagCurrentRewardRes.GetBagCurrentRewardRecord rewardRecord) {
        if (Objects.isNull(rewardRecord)) {
            return null;
        }
        BagCurrentRewardDetailDo rewordDetailDo = new BagCurrentRewardDetailDo();
        rewordDetailDo.setRewardId(rewardRecord.getRewardId());
        rewordDetailDo.setRewardConfigId(rewardRecord.getRewardConfigId());
        rewordDetailDo.setName(rewardRecord.getName());
        rewordDetailDo.setStarLevel(rewardRecord.getStarLevel());
        rewordDetailDo.setDescription(rewardRecord.getDescription());
        rewordDetailDo.setBalanceRequired(AmountUtil.toLong(rewardRecord.getBalanceRequired()));
        rewordDetailDo.setSkipGameId(rewardRecord.getGameId());
        rewordDetailDo.setGameId(rewardRecord.getGameCode());
        rewordDetailDo.setGameName(rewardRecord.getGameName());
        rewordDetailDo.setEffectTime(rewardRecord.getRewardDate().getTime());
        rewordDetailDo.setExpiredTime(rewardRecord.getExpiryDate().getTime());
        rewordDetailDo.setRewardType(rewardRecord.getRewardType());
        rewordDetailDo.setBetValue(AmountUtil.toLong(rewardRecord.getBetValue()));
        rewordDetailDo.setImgUrl(rewardRecord.getImgUrl());
        return rewordDetailDo;
    }
}
