package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.GetHistoryMessageRes;
import com.wegame.capi.biz.domain.biz.promotion.HistoryMessageDo;
import com.wegame.capi.client.domain.vo.promotion.HistoryMessageVo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class HistoryMessageConverter {

    public static List<HistoryMessageVo> toVoList(List<HistoryMessageDo> messageDoList) {
        return Optional.ofNullable(messageDoList)
                .orElse(Collections.emptyList())
                .stream()
                .map(HistoryMessageConverter::toVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static HistoryMessageVo toVo(HistoryMessageDo messageDo) {
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

    public static HistoryMessageDo toDo(GetHistoryMessageRes.MessageRes messageRes) {
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

    public static List<HistoryMessageDo> toDoList(List<GetHistoryMessageRes.MessageRes> messageResList) {
        return Optional.ofNullable(messageResList)
                .orElse(Collections.emptyList())
                .stream()
                .map(HistoryMessageConverter::toDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
