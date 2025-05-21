package com.wegame.capi.biz.converter.jackpot;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.domain.request.query.PageQueryJackpotRewardPoolRequest;
import com.wegame.capi.client.domain.vo.jackpot.JackpotRewardPoolVo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolPo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * JackpotRewardPoolConverter
 * @author generate
 */
public class JackpotRewardPoolConverter {

    public static JackpotRewardPoolDo voToDo(JackpotRewardPoolVo vo) {
        if (Objects.isNull(vo)) {
            return null;
        }
        JackpotRewardPoolDo jackpotRewardPoolDo = new JackpotRewardPoolDo();
        jackpotRewardPoolDo.setType(vo.getType());
        jackpotRewardPoolDo.setAmount(StringUtils.isNotEmpty(vo.getAmount()) ? Long.valueOf(vo.getAmount()) : null);
        jackpotRewardPoolDo.setCreator(vo.getCreator());
        jackpotRewardPoolDo.setCreateTime(Objects.nonNull(vo.getCreateTime()) ? new Date(vo.getCreateTime()) : null);
        jackpotRewardPoolDo.setUpdater(vo.getUpdater());
        jackpotRewardPoolDo.setUpdateTime(Objects.nonNull(vo.getUpdateTime()) ? new Date(vo.getUpdateTime()) : null);
        return jackpotRewardPoolDo;
    }

    public static JackpotRewardPoolDo poToDo(JackpotRewardPoolPo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        JackpotRewardPoolDo jackpotRewardPoolDo = new JackpotRewardPoolDo();
        jackpotRewardPoolDo.setId(po.getId());
        jackpotRewardPoolDo.setType(po.getType());
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
        po.setType(jackpotRewardPoolDo.getType());
        po.setAmount(jackpotRewardPoolDo.getAmount());
        if (OptEnum.ADD.equals(optEnum)) {
            po.setCreator(operator.getName());
            po.setCreateTime(new Date());
            po.setUpdater(operator.getName());
            po.setUpdateTime(new Date());
        } else {
            po.setUpdater(operator.getName());
            po.setUpdateTime(new Date());
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
        vo.setType(jackpotRewardPoolDo.getType());
        vo.setAmount(Objects.nonNull(jackpotRewardPoolDo.getAmount()) ? String.valueOf(jackpotRewardPoolDo.getAmount()) : null);
        vo.setCreator(jackpotRewardPoolDo.getCreator());
        vo.setCreateTime(Objects.nonNull(jackpotRewardPoolDo.getCreateTime()) ? jackpotRewardPoolDo.getCreateTime().getTime() : null);
        vo.setUpdater(jackpotRewardPoolDo.getUpdater());
        vo.setUpdateTime(Objects.nonNull(jackpotRewardPoolDo.getUpdateTime()) ? jackpotRewardPoolDo.getUpdateTime().getTime() : null);
        return vo;
    }

    public static List<JackpotRewardPoolDo> voToDoList(List<JackpotRewardPoolVo> voList) {
        return Optional.ofNullable(voList)
                .orElse(Collections.emptyList())
                .stream()
                .map(JackpotRewardPoolConverter::voToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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

    public static JackpotRewardPoolQuery requestToQuery(PageQueryJackpotRewardPoolRequest request, String orderClause) {
        JackpotRewardPoolQuery query = new JackpotRewardPoolQuery();
        query.setId(StringUtils.isNotEmpty(request.getId()) ? Long.valueOf(request.getId()) : null);
        query.setType(request.getType());
        query.setAmount(StringUtils.isNotEmpty(request.getAmount()) ? Long.valueOf(request.getAmount()) : null);
        query.setCreator(request.getCreator());
        query.setCreateTime(Objects.nonNull(request.getCreateTime()) ? new Date(request.getCreateTime()) : null);
        query.setUpdater(request.getUpdater());
        query.setUpdateTime(Objects.nonNull(request.getUpdateTime()) ? new Date(request.getUpdateTime()) : null);
        query.setStartCreateTime(Objects.nonNull(request.getStartCreateTime()) ? new Date(request.getStartCreateTime()) : null);
        query.setEndCreateTime(Objects.nonNull(request.getEndCreateTime()) ? new Date(request.getEndCreateTime()) : null);
        query.setStartUpdateTime(Objects.nonNull(request.getStartUpdateTime()) ? new Date(request.getStartUpdateTime()) : null);
        query.setEndUpdateTime(Objects.nonNull(request.getEndUpdateTime()) ? new Date(request.getEndUpdateTime()) : null);
        query.setLimit(request.getPageSize());
        query.setOffset(request.getOffset());
        query.setOrderByClause(orderClause);
        return query;
    }

    ///////////////  above auto generated ///////////////

}