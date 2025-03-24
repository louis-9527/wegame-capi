package com.wegame.capi.biz.converter.promotion;

import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.promotion.StarCardUseRecordDo;
import com.wegame.capi.common.enums.StarCardReportStatusEnum;
import com.wegame.capi.common.enums.StarCardUseStatusEnum;
import com.wegame.capi.dao.mapper.domain.star.StarCardUseRecordPo;

import java.util.Objects;

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
}
