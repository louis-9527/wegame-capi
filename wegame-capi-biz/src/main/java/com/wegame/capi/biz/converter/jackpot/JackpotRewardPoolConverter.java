package com.wegame.capi.biz.converter.jackpot;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.client.domain.vo.jackpot.JackpotRewardPoolVo;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolPo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JackpotRewardPoolConverter
 *
 * @author generate
 */
public class JackpotRewardPoolConverter {

    public static JackpotRewardPoolDo poToDo(JackpotRewardPoolPo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        JackpotRewardPoolDo jackpotRewardPoolDo = new JackpotRewardPoolDo();
        jackpotRewardPoolDo.setId(po.getId());
        jackpotRewardPoolDo.setType(JackpotRewardPoolTypeEnum.valueOfCode(po.getType()));
        jackpotRewardPoolDo.setAmount(po.getAmount());
        jackpotRewardPoolDo.setCreator(po.getCreator());
        jackpotRewardPoolDo.setCreateTime(po.getCreateTime());
        jackpotRewardPoolDo.setUpdater(po.getUpdater());
        jackpotRewardPoolDo.setUpdateTime(po.getUpdateTime());
        return jackpotRewardPoolDo;
    }

    public static JackpotRewardPoolPo doToPo(JackpotRewardPoolDo jackpotRewardPoolDo, OptEnum optEnum) {
        if (Objects.isNull(jackpotRewardPoolDo)) {
            return null;
        }
        Operator operator = OperatorContext.getOperator();
        AssertUtil.assertNotNull(operator, ErrorCode.ERR_PARAM);
        JackpotRewardPoolPo po = new JackpotRewardPoolPo();
        po.setType(jackpotRewardPoolDo.getType().getCode());
        po.setAmount(jackpotRewardPoolDo.getAmount());
        po.setReservedAmount(jackpotRewardPoolDo.getReservedAmount());
        if (OptEnum.ADD.equals(optEnum)) {
            po.setCreator(operator.getName());
            po.setCreateTime(DateUtil.getCurrentMilli());
            po.setUpdater(operator.getName());
            po.setUpdateTime(DateUtil.getCurrentMilli());
        } else {
            po.setUpdater(operator.getName());
            po.setUpdateTime(DateUtil.getCurrentMilli());
            po.setId(jackpotRewardPoolDo.getId());
        }
        return po;
    }

    public static JackpotRewardPoolVo doToVo(JackpotRewardPoolDo jackpotRewardPoolDo) {
        if (Objects.isNull(jackpotRewardPoolDo)) {
            return null;
        }
        JackpotRewardPoolVo vo = new JackpotRewardPoolVo();
        vo.setId(Objects.nonNull(jackpotRewardPoolDo.getId()) ? String.valueOf(jackpotRewardPoolDo.getId()) : null);
        vo.setType(jackpotRewardPoolDo.getType().getCode());
        vo.setAmount(jackpotRewardPoolDo.getAmount());
        return vo;
    }

    public static List<JackpotRewardPoolDo> poToDoList(List<JackpotRewardPoolPo> poList) {
        return Optional.ofNullable(poList)
                .orElse(Collections.emptyList())
                .stream()
                .map(JackpotRewardPoolConverter::poToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<JackpotRewardPoolPo> doToPoList(List<JackpotRewardPoolDo> doList, OptEnum optEnum) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> doToPo(item, optEnum))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<JackpotRewardPoolVo> doToVoList(List<JackpotRewardPoolDo> doList) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(JackpotRewardPoolConverter::doToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /// ////////////  above auto generated ///////////////
}