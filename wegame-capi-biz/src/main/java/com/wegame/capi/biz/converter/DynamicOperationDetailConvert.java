package com.wegame.capi.biz.converter;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.utils.AmountUtil;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationDetailPo;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationPo;

import java.util.Objects;

public class DynamicOperationDetailConvert {

    public static DynamicOperationDetailPo convertPo(DynamicOperationEventDetailDo detailDo, OptEnum optEnum) {
        if (Objects.isNull(detailDo)) {
            return null;
        }
        DynamicOperationDetailPo po = DynamicOperationDetailPo.builder().build();
        po.setId(detailDo.getId());
        if (OptEnum.ADD.equals(optEnum)) {
            po.setEventId(detailDo.getEventId());
            po.setOperatorRecordId(detailDo.getOperatorRecordId());
            po.setOperatorGameId(detailDo.getOperatorGameId());
            po.setPlayerId(detailDo.getPlayerId());
            po.setPlayerName(detailDo.getPlayerName());
            po.setUserId(detailDo.getUserId());
            po.setPayOutAmount(detailDo.getPayOutAmount());
            po.setPayOutTime(detailDo.getPayOutTime());
            po.setBetId(detailDo.getBetId());
            po.setBetRecordId(detailDo.getBetRecordId());
            po.setBetStatus(detailDo.getBetStatus().getCode());
            po.setCreateTime(detailDo.getCreateTime());
            po.setUpdateTime(detailDo.getUpdateTime());
        } else {
            po.setUpdateTime(DateUtil.getCurrentMilli());
        }
        return po;
    }
}
