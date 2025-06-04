package com.wegame.capi.biz.converter.jackpot;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.client.domain.vo.jackpot.JackpotRewardPoolInputRecordVo;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.JackpotRewardPoolInputTypeEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolInputRecordPo;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * JackpotRewardPoolInputRecordConverter
 *
 * @author generate
 */
public class JackpotRewardPoolInputRecordConverter {

    public static JackpotRewardPoolInputRecordDo voToDo(JackpotRewardPoolInputRecordVo vo) {
        if (Objects.isNull(vo)) {
            return null;
        }
        JackpotRewardPoolInputRecordDo jackpotRewardPoolInputRecordDo = new JackpotRewardPoolInputRecordDo();
        jackpotRewardPoolInputRecordDo.setType(JackpotRewardPoolInputTypeEnum.valueOfCode(vo.getType()));
        jackpotRewardPoolInputRecordDo.setGrandAmount(StringUtils.isNotEmpty(vo.getGrandAmount()) ? Long.valueOf(vo.getGrandAmount()) : null);
        jackpotRewardPoolInputRecordDo.setMajorAmount(StringUtils.isNotEmpty(vo.getMajorAmount()) ? Long.valueOf(vo.getMajorAmount()) : null);
        jackpotRewardPoolInputRecordDo.setMinorAmount(StringUtils.isNotEmpty(vo.getMinorAmount()) ? Long.valueOf(vo.getMinorAmount()) : null);
        jackpotRewardPoolInputRecordDo.setMiniAmount(StringUtils.isNotEmpty(vo.getMiniAmount()) ? Long.valueOf(vo.getMiniAmount()) : null);
        jackpotRewardPoolInputRecordDo.setCreator(vo.getCreator());
        jackpotRewardPoolInputRecordDo.setCreateTime(Objects.nonNull(vo.getCreateTime()) ? vo.getCreateTime() : null);
        jackpotRewardPoolInputRecordDo.setUpdater(vo.getUpdater());
        jackpotRewardPoolInputRecordDo.setUpdateTime(Objects.nonNull(vo.getUpdateTime()) ? vo.getUpdateTime() : null);
        return jackpotRewardPoolInputRecordDo;
    }

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

    public static JackpotRewardPoolInputRecordVo doToVo(JackpotRewardPoolInputRecordDo jackpotRewardPoolInputRecordDo) {
        if (Objects.isNull(jackpotRewardPoolInputRecordDo)) {
            return null;
        }
        JackpotRewardPoolInputRecordVo vo = new JackpotRewardPoolInputRecordVo();
        vo.setId(Objects.nonNull(jackpotRewardPoolInputRecordDo.getId()) ? String.valueOf(jackpotRewardPoolInputRecordDo.getId()) : null);
        vo.setType(jackpotRewardPoolInputRecordDo.getType().getCode());
        vo.setGrandAmount(Objects.nonNull(jackpotRewardPoolInputRecordDo.getGrandAmount()) ? String.valueOf(jackpotRewardPoolInputRecordDo.getGrandAmount()) : null);
        vo.setMajorAmount(Objects.nonNull(jackpotRewardPoolInputRecordDo.getMajorAmount()) ? String.valueOf(jackpotRewardPoolInputRecordDo.getMajorAmount()) : null);
        vo.setMinorAmount(Objects.nonNull(jackpotRewardPoolInputRecordDo.getMinorAmount()) ? String.valueOf(jackpotRewardPoolInputRecordDo.getMinorAmount()) : null);
        vo.setMiniAmount(Objects.nonNull(jackpotRewardPoolInputRecordDo.getMiniAmount()) ? String.valueOf(jackpotRewardPoolInputRecordDo.getMiniAmount()) : null);
        vo.setCreator(jackpotRewardPoolInputRecordDo.getCreator());
        vo.setCreateTime(Objects.nonNull(jackpotRewardPoolInputRecordDo.getCreateTime()) ? jackpotRewardPoolInputRecordDo.getCreateTime() : null);
        vo.setUpdater(jackpotRewardPoolInputRecordDo.getUpdater());
        vo.setUpdateTime(Objects.nonNull(jackpotRewardPoolInputRecordDo.getUpdateTime()) ? jackpotRewardPoolInputRecordDo.getUpdateTime() : null);
        return vo;
    }

    public static List<JackpotRewardPoolInputRecordDo> voToDoList(List<JackpotRewardPoolInputRecordVo> voList) {
        return Optional.ofNullable(voList)
                .orElse(Collections.emptyList())
                .stream()
                .map(JackpotRewardPoolInputRecordConverter::voToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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

    public static List<JackpotRewardPoolInputRecordVo> doToVoList(List<JackpotRewardPoolInputRecordDo> doList) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(JackpotRewardPoolInputRecordConverter::doToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    ///////////////  above auto generated ///////////////

}