package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.GetBagOtherRewardRes;
import com.wegame.capi.biz.domain.biz.promotion.BagOtherRewardDetailDo;
import com.wegame.capi.client.domain.vo.promotion.BagOtherRewardDetailVo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class BagOtherRewardDetailConverter {

    public static BagOtherRewardDetailVo toVo(BagOtherRewardDetailDo detailDo) {
        if (detailDo == null) {
            return null;
        }
        BagOtherRewardDetailVo detailVo = new BagOtherRewardDetailVo();
        detailVo.setRewardId(detailDo.getRewardId());
        detailVo.setName(detailDo.getName());
        detailVo.setDescription(detailDo.getDescription());
        detailVo.setGameCode(detailDo.getGameCode());
        detailVo.setGameId(detailDo.getGameId());
        detailVo.setGameName(detailDo.getGameName());
        detailVo.setImgUrl(detailDo.getImgUrl());
        detailVo.setTotalCount(detailDo.getTotalCount());
        detailVo.setIsFreeBet(detailDo.getIsFreeBet());
        return detailVo;
    }

    public static List<BagOtherRewardDetailVo> toVoList(List<BagOtherRewardDetailDo> doList) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BagOtherRewardDetailConverter::toVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<BagOtherRewardDetailDo> toDoList(List<GetBagOtherRewardRes.GetBagOtherRewardRecord> rewardBagList) {
        return Optional.ofNullable(rewardBagList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BagOtherRewardDetailConverter::toDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static BagOtherRewardDetailDo toDo(GetBagOtherRewardRes.GetBagOtherRewardRecord rewardBag) {
        if (Objects.isNull(rewardBag)) {
            return null;
        }
        BagOtherRewardDetailDo rewordDetailDo = new BagOtherRewardDetailDo();
        rewordDetailDo.setRewardId(rewardBag.getRewardId());
        rewordDetailDo.setName(rewardBag.getName());
        rewordDetailDo.setDescription(rewardBag.getDescription());
        rewordDetailDo.setGameCode(rewardBag.getGameCode());
        rewordDetailDo.setGameId(rewardBag.getGameId());
        rewordDetailDo.setGameName(rewardBag.getGameName());
        rewordDetailDo.setImgUrl(rewardBag.getImgUrl());
        rewordDetailDo.setTotalCount(rewardBag.getTotalCount());
        rewordDetailDo.setIsFreeBet(rewardBag.getIsFreeBet());
        return rewordDetailDo;
    }

}
