package com.wegame.capi.biz.converter.jackpot;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.JackpotAccFlagEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolInputTypeEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolInputRecordPo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * JackpotRewardPoolInputRecordConverter
 *
 * @author generate
 */
public class JackpotRewardPoolInputRecordConverter {

    public static JackpotRewardPoolInputRecordDo poToDo(JackpotRewardPoolInputRecordPo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        JackpotRewardPoolInputRecordDo jackpotRewardPoolInputRecordDo = new JackpotRewardPoolInputRecordDo();
        jackpotRewardPoolInputRecordDo.setId(po.getId());
        jackpotRewardPoolInputRecordDo.setType(JackpotRewardPoolInputTypeEnum.valueOfCode(po.getType()));
        jackpotRewardPoolInputRecordDo.setGrandAmount(po.getGrandAmount());
        jackpotRewardPoolInputRecordDo.setMajorAmount(po.getMajorAmount());
        jackpotRewardPoolInputRecordDo.setMinorAmount(po.getMinorAmount());
        jackpotRewardPoolInputRecordDo.setMiniAmount(po.getMiniAmount());

        jackpotRewardPoolInputRecordDo.setGrandReservedAmount(po.getGrandReservedAmount());
        jackpotRewardPoolInputRecordDo.setMajorReservedAmount(po.getMajorReservedAmount());
        jackpotRewardPoolInputRecordDo.setMinorReservedAmount(po.getMinorReservedAmount());
        jackpotRewardPoolInputRecordDo.setMiniReservedAmount(po.getMiniReservedAmount());

        jackpotRewardPoolInputRecordDo.setAccFlag(JackpotAccFlagEnum.valueOfCode(po.getAccFlag()));
        jackpotRewardPoolInputRecordDo.setCreator(po.getCreator());
        jackpotRewardPoolInputRecordDo.setCreateTime(po.getCreateTime());
        jackpotRewardPoolInputRecordDo.setUpdater(po.getUpdater());
        jackpotRewardPoolInputRecordDo.setUpdateTime(po.getUpdateTime());
        return jackpotRewardPoolInputRecordDo;
    }

    public static JackpotRewardPoolInputRecordPo doToPo(JackpotRewardPoolInputRecordDo jackpotRewardPoolInputRecordDo, OptEnum optEnum) {
        if (Objects.isNull(jackpotRewardPoolInputRecordDo)) {
            return null;
        }
        Operator operator = OperatorContext.getOperator();
        AssertUtil.assertNotNull(operator, ErrorCode.ERR_PARAM);
        JackpotRewardPoolInputRecordPo po = new JackpotRewardPoolInputRecordPo();
        po.setId(jackpotRewardPoolInputRecordDo.getId());
        po.setType(jackpotRewardPoolInputRecordDo.getType().getCode());
        po.setGrandAmount(jackpotRewardPoolInputRecordDo.getGrandAmount());
        po.setMajorAmount(jackpotRewardPoolInputRecordDo.getMajorAmount());
        po.setMinorAmount(jackpotRewardPoolInputRecordDo.getMinorAmount());
        po.setMiniAmount(jackpotRewardPoolInputRecordDo.getMiniAmount());

        po.setGrandReservedAmount(jackpotRewardPoolInputRecordDo.getGrandReservedAmount());
        po.setMajorReservedAmount(jackpotRewardPoolInputRecordDo.getMajorReservedAmount());
        po.setMinorReservedAmount(jackpotRewardPoolInputRecordDo.getMinorReservedAmount());
        po.setMiniReservedAmount(jackpotRewardPoolInputRecordDo.getMiniReservedAmount());

        po.setAccFlag(jackpotRewardPoolInputRecordDo.getAccFlag().getCode());
        if (OptEnum.ADD.equals(optEnum)) {
            long currentMilli = DateUtil.getCurrentMilli();
            po.setCreator(operator.getName());
            po.setCreateTime(currentMilli);
            po.setUpdater(operator.getName());
            po.setUpdateTime(currentMilli);
            po.setExpireAtTime(DateUtil.addSeconds(new Date(), Constants.JACKPOT_REWARD_POOL_EXPIRED_SECONDS));
        } else {
            po.setUpdater(operator.getName());
            po.setUpdateTime(DateUtil.getCurrentMilli());
            po.setId(jackpotRewardPoolInputRecordDo.getId());
        }
        return po;
    }

    public static List<JackpotRewardPoolInputRecordDo> poToDoList(List<JackpotRewardPoolInputRecordPo> poList) {
        return Optional.ofNullable(poList)
                .orElse(Collections.emptyList())
                .stream()
                .map(JackpotRewardPoolInputRecordConverter::poToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<JackpotRewardPoolInputRecordPo> doToPoList(List<JackpotRewardPoolInputRecordDo> doList, OptEnum optEnum) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> doToPo(item, optEnum))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    ///////////////  above auto generated ///////////////

}