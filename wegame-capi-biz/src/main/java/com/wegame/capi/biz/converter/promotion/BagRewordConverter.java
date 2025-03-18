package com.wegame.capi.biz.converter.promotion;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.domain.res.promotion.GetBagCurrentRewardRes;
import com.wegame.capi.api.domain.res.promotion.GetBagOtherRewardRes;
import com.wegame.capi.api.domain.res.promotion.GetHistoryMessageRes;
import com.wegame.capi.biz.domain.biz.promotion.BagCurrentRewordDetailDo;
import com.wegame.capi.biz.domain.biz.promotion.BagOtherRewordDetailDo;
import com.wegame.capi.biz.domain.biz.promotion.HistoryMessageDo;
import com.wegame.capi.client.domain.vo.promotion.BagOtherRewardDetailVo;
import com.wegame.capi.client.domain.vo.promotion.BagRewardDetailVo;
import com.wegame.capi.client.domain.vo.promotion.HistoryMessageVo;
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
        rewordDetailDo.setRewardConfigId(rewardBag.getRewardConfigId());
        rewordDetailDo.setName(rewardBag.getName());
        rewordDetailDo.setStarLevel(rewardBag.getStarLevel());
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

    public static List<BagRewardDetailVo> currentDoToVoList(List<BagCurrentRewordDetailDo> detailDoList) {
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

    public static List<BagOtherRewardDetailVo> otherDoToVoList(List<BagOtherRewordDetailDo> detailDoList) {
        return Optional.ofNullable(detailDoList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BagRewordConverter::otherDoToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public static HistoryMessageDo messageResToDo(GetHistoryMessageRes.MessageRes messageRes) {
        if (Objects.isNull(messageRes)) {
            return null;
        }
        HistoryMessageDo messageDo = new HistoryMessageDo();
        messageDo.setPlayerName(messageRes.getPlayerName());
        messageDo.setChannelId(messageRes.getChannelId());
        messageDo.setMissionId(messageRes.getMissionId());
        messageDo.setMissionName(messageRes.getMissionName());
        messageDo.setMissionDescription(messageRes.getMissionDescription());
        messageDo.setRewardId(messageRes.getRewardId());
        messageDo.setRewardName(messageRes.getRewardName());
        messageDo.setRewardDescription(messageRes.getRewardDescription());
        messageDo.setType(messageRes.getType());
        messageDo.setCreateTime(messageRes.getCreateTime().getTime());
        return messageDo;
    }

    public static List<HistoryMessageDo> messageResToDos(List<GetHistoryMessageRes.MessageRes> messageResList) {
        return Optional.ofNullable(messageResList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BagRewordConverter::messageResToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static HistoryMessageVo messageDoToVo(HistoryMessageDo messageDo) {
        if (Objects.isNull(messageDo)) {
            return null;
        }
        HistoryMessageVo messageVo = new HistoryMessageVo();
        messageVo.setPlayerName(messageDo.getPlayerName());
        messageVo.setChannelId(messageDo.getChannelId());
        messageVo.setMissionId(messageDo.getMissionId());
        messageVo.setMissionName(messageDo.getMissionName());
        messageVo.setMissionDescription(messageDo.getMissionDescription());
        messageVo.setRewardId(messageDo.getRewardId());
        messageVo.setRewardName(messageDo.getRewardName());
        messageVo.setRewardDescription(messageDo.getRewardDescription());
        messageVo.setType(messageDo.getType());
        messageVo.setCreateTime(messageDo.getCreateTime());
        return messageVo;
    }

    public static List<HistoryMessageVo> messageDoToVos(List<HistoryMessageDo> messageDoList) {
        return Optional.ofNullable(messageDoList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BagRewordConverter::messageDoToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
