package com.wegame.capi.biz.converter.business;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.domain.request.query.PageQueryJackpotRewardPoolInputRecordRequest;
import com.wegame.capi.client.domain.vo.JackpotRewardPoolInputRecordVo;
import com.wegame.capi.biz.domain.business.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.dao.mapper.domain.business.JackpotRewardPoolInputRecordPo;
import com.wegame.capi.dao.mapper.domain.business.JackpotRewardPoolInputRecordQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * JackpotRewardPoolInputRecordConverter
 * @author generate
 */
public class JackpotRewardPoolInputRecordConverter {

    public static JackpotRewardPoolInputRecordDo voToDo(JackpotRewardPoolInputRecordVo vo) {
        if (Objects.isNull(vo)) {
            return null;
        }
        JackpotRewardPoolInputRecordDo jackpotRewardPoolInputRecordDo = new JackpotRewardPoolInputRecordDo();
        jackpotRewardPoolInputRecordDo.setType(vo.getType());
        jackpotRewardPoolInputRecordDo.setGrandAmount(StringUtils.isNotEmpty(vo.getGrandAmount()) ? Long.valueOf(vo.getGrandAmount()) : null);
        jackpotRewardPoolInputRecordDo.setMajorAmount(StringUtils.isNotEmpty(vo.getMajorAmount()) ? Long.valueOf(vo.getMajorAmount()) : null);
        jackpotRewardPoolInputRecordDo.setMinorAmount(StringUtils.isNotEmpty(vo.getMinorAmount()) ? Long.valueOf(vo.getMinorAmount()) : null);
        jackpotRewardPoolInputRecordDo.setMiniAmount(StringUtils.isNotEmpty(vo.getMiniAmount()) ? Long.valueOf(vo.getMiniAmount()) : null);
        jackpotRewardPoolInputRecordDo.setCreator(vo.getCreator());
        jackpotRewardPoolInputRecordDo.setCreateTime(Objects.nonNull(vo.getCreateTime()) ? new Date(vo.getCreateTime()) : null);
        jackpotRewardPoolInputRecordDo.setUpdater(vo.getUpdater());
        jackpotRewardPoolInputRecordDo.setUpdateTime(Objects.nonNull(vo.getUpdateTime()) ? new Date(vo.getUpdateTime()) : null);
        return jackpotRewardPoolInputRecordDo;
    }

    public static JackpotRewardPoolInputRecordDo poToDo(JackpotRewardPoolInputRecordPo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        JackpotRewardPoolInputRecordDo jackpotRewardPoolInputRecordDo = new JackpotRewardPoolInputRecordDo();
        jackpotRewardPoolInputRecordDo.setId(po.getId());
        jackpotRewardPoolInputRecordDo.setType(po.getType());
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
        po.setType(jackpotRewardPoolInputRecordDo.getType());
        po.setGrandAmount(jackpotRewardPoolInputRecordDo.getGrandAmount());
        po.setMajorAmount(jackpotRewardPoolInputRecordDo.getMajorAmount());
        po.setMinorAmount(jackpotRewardPoolInputRecordDo.getMinorAmount());
        po.setMiniAmount(jackpotRewardPoolInputRecordDo.getMiniAmount());
        if (OptEnum.ADD.equals(optEnum)) {
            po.setCreator(operator.getName());
            po.setCreateTime(new Date());
            po.setUpdater(operator.getName());
            po.setUpdateTime(new Date());
        } else {
            po.setUpdater(operator.getName());
            po.setUpdateTime(new Date());
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
        vo.setType(jackpotRewardPoolInputRecordDo.getType());
        vo.setGrandAmount(Objects.nonNull(jackpotRewardPoolInputRecordDo.getGrandAmount()) ? String.valueOf(jackpotRewardPoolInputRecordDo.getGrandAmount()) : null);
        vo.setMajorAmount(Objects.nonNull(jackpotRewardPoolInputRecordDo.getMajorAmount()) ? String.valueOf(jackpotRewardPoolInputRecordDo.getMajorAmount()) : null);
        vo.setMinorAmount(Objects.nonNull(jackpotRewardPoolInputRecordDo.getMinorAmount()) ? String.valueOf(jackpotRewardPoolInputRecordDo.getMinorAmount()) : null);
        vo.setMiniAmount(Objects.nonNull(jackpotRewardPoolInputRecordDo.getMiniAmount()) ? String.valueOf(jackpotRewardPoolInputRecordDo.getMiniAmount()) : null);
        vo.setCreator(jackpotRewardPoolInputRecordDo.getCreator());
        vo.setCreateTime(Objects.nonNull(jackpotRewardPoolInputRecordDo.getCreateTime()) ? jackpotRewardPoolInputRecordDo.getCreateTime().getTime() : null);
        vo.setUpdater(jackpotRewardPoolInputRecordDo.getUpdater());
        vo.setUpdateTime(Objects.nonNull(jackpotRewardPoolInputRecordDo.getUpdateTime()) ? jackpotRewardPoolInputRecordDo.getUpdateTime().getTime() : null);
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

    public static JackpotRewardPoolInputRecordQuery requestToQuery(PageQueryJackpotRewardPoolInputRecordRequest request, String orderClause) {
        JackpotRewardPoolInputRecordQuery query = new JackpotRewardPoolInputRecordQuery();
        query.setId(StringUtils.isNotEmpty(request.getId()) ? Long.valueOf(request.getId()) : null);
        query.setType(request.getType());
        query.setGrandAmount(StringUtils.isNotEmpty(request.getGrandAmount()) ? Long.valueOf(request.getGrandAmount()) : null);
        query.setMajorAmount(StringUtils.isNotEmpty(request.getMajorAmount()) ? Long.valueOf(request.getMajorAmount()) : null);
        query.setMinorAmount(StringUtils.isNotEmpty(request.getMinorAmount()) ? Long.valueOf(request.getMinorAmount()) : null);
        query.setMiniAmount(StringUtils.isNotEmpty(request.getMiniAmount()) ? Long.valueOf(request.getMiniAmount()) : null);
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