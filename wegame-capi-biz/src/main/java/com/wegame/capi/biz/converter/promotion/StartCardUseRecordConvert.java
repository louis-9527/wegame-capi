package com.wegame.capi.biz.converter.promotion;

import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.promotion.StartCardUseRecordDo;
import com.wegame.capi.common.enums.StartCardReportStatusEnum;
import com.wegame.capi.common.enums.StartCardUseStatusEnum;
import com.wegame.capi.dao.mapper.domain.business.StartCardUseRecordPo;

public class StartCardUseRecordConvert {

    public static StartCardUseRecordPo doToPo(StartCardUseRecordDo recordDo, OptEnum optEnum) {
        if (recordDo == null) {
            return null;
        }
        StartCardUseRecordPo recordPo = new StartCardUseRecordPo();
        recordPo.setId(recordDo.getId());
        recordPo.setRewardId(recordDo.getRewardId());
        recordPo.setRewardConfigId(recordDo.getRewardConfigId());
        recordPo.setGameId(recordDo.getGameId());
        recordPo.setPlayerId(recordDo.getPlayerId());
        recordPo.setPlayName(recordDo.getPlayName());
        recordPo.setBetAmount(recordDo.getBetAmount());
        recordPo.setPayOutAmount(recordDo.getPayOutAmount());
        if(OptEnum.ADD == optEnum){
            recordPo.setUseStatus(StartCardUseStatusEnum.ING.getCode());
            recordPo.setReportStatus(StartCardReportStatusEnum.ING.getCode());
            long currentMilli = DateUtil.getCurrentMilli();
            recordPo.setCreateTime(currentMilli);
            recordPo.setUpdateTime(currentMilli);
        }else {
            recordPo.setUpdateTime(DateUtil.getCurrentMilli());
        }
        return recordPo;
    }
}
