package com.wegame.capi.biz.converter.promotion;

import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.promotion.StarCardUseRecordDo;
import com.wegame.capi.common.enums.StarCardReportStatusEnum;
import com.wegame.capi.common.enums.StarCardUseStatusEnum;
import com.wegame.capi.dao.repository.domain.star.StarCardUseRecordPo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class StartCardUseRecordConvert {

    public static StarCardUseRecordPo doToPo(StarCardUseRecordDo recordDo, OptEnum optEnum) {
        if (Objects.isNull(recordDo)) {
            return null;
        }
        StarCardUseRecordPo recordPo = new StarCardUseRecordPo();
        recordPo.setId(recordDo.getId());
        recordPo.setRewardId(recordDo.getRewardId());
        recordPo.setRewardConfigId(recordDo.getRewardConfigId());
        recordPo.setGameId(recordDo.getGameId());
        recordPo.setChannelId(recordDo.getChannelId());
        recordPo.setPlayerId(recordDo.getPlayerId());
        recordPo.setPlayName(recordDo.getPlayName());
        recordPo.setBetAmount(recordDo.getBetAmount());
        recordPo.setPayOutAmount(recordDo.getPayOutAmount());
        if (OptEnum.ADD == optEnum) {
            recordPo.setUseStatus(StarCardUseStatusEnum.ING.getCode());
            recordPo.setReportStatus(StarCardReportStatusEnum.ING.getCode());
            long currentMilli = DateUtil.getCurrentMilli();
            recordPo.setCreateTime(currentMilli);
            recordPo.setUpdateTime(currentMilli);
        } else {
            recordPo.setUpdateTime(DateUtil.getCurrentMilli());
        }
        return recordPo;
    }


    public static List<StarCardUseRecordDo> doTodoList(List<StarCardUseRecordPo> recordPoList) {
        return Optional.ofNullable(recordPoList)
                .orElse(Collections.emptyList())
                .stream()
                .map(StartCardUseRecordConvert::poTodo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public static StarCardUseRecordDo poTodo(StarCardUseRecordPo recordPo) {
        if (Objects.isNull(recordPo)) {
            return null;
        }
        StarCardUseRecordDo recordDo = new StarCardUseRecordDo();
        recordDo.setId(recordPo.getId());
        recordDo.setRewardId(recordPo.getRewardId());
        recordDo.setRewardConfigId(recordPo.getRewardConfigId());
        recordDo.setBetRecordId(recordPo.getBetRecordId());
        recordDo.setPlayerId(recordPo.getPlayerId());
        recordDo.setPlayName(recordPo.getPlayName());
        recordDo.setGameId(recordPo.getGameId());
        recordDo.setChannelId(recordPo.getChannelId());
        recordDo.setBetAmount(recordPo.getBetAmount());
        recordDo.setPayOutAmount(recordPo.getPayOutAmount());
        recordDo.setUseStatus(recordPo.getUseStatus() == null ? null : StarCardUseStatusEnum.valueOfCode(recordPo.getUseStatus()));
        recordDo.setFailMsg(recordPo.getFailMsg());
        recordDo.setReportStatus(recordPo.getReportStatus() == null ? null : StarCardReportStatusEnum.valueOfCode(recordPo.getReportStatus()));
        recordDo.setCreateTime(recordPo.getCreateTime());
        recordDo.setUpdateTime(recordPo.getUpdateTime());
        return recordDo;

    }
}
