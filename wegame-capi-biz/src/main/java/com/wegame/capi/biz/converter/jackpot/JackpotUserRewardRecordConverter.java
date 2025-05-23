package com.wegame.capi.biz.converter.jackpot;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.domain.request.query.PageQueryJackpotUserRewardRecordRequest;
import com.wegame.capi.client.domain.vo.jackpot.JackpotUserRewardRecordVo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordPo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * JackpotUserRewardRecordConverter
 * @author generate
 */
public class JackpotUserRewardRecordConverter {

    public static JackpotUserRewardRecordDo voToDo(JackpotUserRewardRecordVo vo) {
        if (Objects.isNull(vo)) {
            return null;
        }
        JackpotUserRewardRecordDo jackpotUserRewardRecordDo = new JackpotUserRewardRecordDo();
        jackpotUserRewardRecordDo.setUserId(StringUtils.isNotEmpty(vo.getUserId()) ? Long.valueOf(vo.getUserId()) : null);
        jackpotUserRewardRecordDo.setPlayerName(vo.getPlayerName());
        jackpotUserRewardRecordDo.setBetRecordId(Objects.nonNull(vo.getBetRecordId()) ? new Date(vo.getBetRecordId()) : null);
        jackpotUserRewardRecordDo.setGameRecordId(Objects.nonNull(vo.getGameRecordId()) ? new Date(vo.getGameRecordId()) : null);
        jackpotUserRewardRecordDo.setBetId(StringUtils.isNotEmpty(vo.getBetId()) ? Long.valueOf(vo.getBetId()) : null);
        jackpotUserRewardRecordDo.setRewardPoolId(StringUtils.isNotEmpty(vo.getRewardPoolId()) ? Long.valueOf(vo.getRewardPoolId()) : null);
        jackpotUserRewardRecordDo.setRewardPoolType(vo.getRewardPoolType());
        jackpotUserRewardRecordDo.setRewardAmount(vo.getRewardAmount());
        jackpotUserRewardRecordDo.setRewardGrantSerialNo(vo.getRewardGrantSerialNo());
        jackpotUserRewardRecordDo.setRewardGrantStatus(vo.getRewardGrantStatus());
        jackpotUserRewardRecordDo.setRewardGrantTime(Objects.nonNull(vo.getRewardGrantTime()) ? new Date(vo.getRewardGrantTime()) : null);
        jackpotUserRewardRecordDo.setCreator(vo.getCreator());
        jackpotUserRewardRecordDo.setCreateTime(Objects.nonNull(vo.getCreateTime()) ? new Date(vo.getCreateTime()) : null);
        jackpotUserRewardRecordDo.setUpdater(vo.getUpdater());
        jackpotUserRewardRecordDo.setUpdateTime(Objects.nonNull(vo.getUpdateTime()) ? new Date(vo.getUpdateTime()) : null);
        return jackpotUserRewardRecordDo;
    }

    public static JackpotUserRewardRecordDo poToDo(JackpotUserRewardRecordPo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        JackpotUserRewardRecordDo jackpotUserRewardRecordDo = new JackpotUserRewardRecordDo();
        jackpotUserRewardRecordDo.setId(po.getId());
        jackpotUserRewardRecordDo.setUserId(po.getUserId());
        jackpotUserRewardRecordDo.setPlayerName(po.getPlayerName());
        jackpotUserRewardRecordDo.setBetRecordId(po.getBetRecordId());
        jackpotUserRewardRecordDo.setGameRecordId(po.getGameRecordId());
        jackpotUserRewardRecordDo.setBetId(po.getBetId());
        jackpotUserRewardRecordDo.setRewardPoolId(po.getRewardPoolId());
        jackpotUserRewardRecordDo.setRewardPoolType(po.getRewardPoolType());
        jackpotUserRewardRecordDo.setRewardAmount(po.getRewardAmount());
        jackpotUserRewardRecordDo.setRewardGrantSerialNo(po.getRewardGrantSerialNo());
        jackpotUserRewardRecordDo.setRewardGrantStatus(po.getRewardGrantStatus());
        jackpotUserRewardRecordDo.setRewardGrantTime(po.getRewardGrantTime());
        jackpotUserRewardRecordDo.setCreator(po.getCreator());
        jackpotUserRewardRecordDo.setCreateTime(po.getCreateTime());
        jackpotUserRewardRecordDo.setUpdater(po.getUpdater());
        jackpotUserRewardRecordDo.setUpdateTime(po.getUpdateTime());
        return jackpotUserRewardRecordDo;
    }

    public static JackpotUserRewardRecordPo doToPo(JackpotUserRewardRecordDo jackpotUserRewardRecordDo, OptEnum optEnum) {
        if (Objects.isNull(jackpotUserRewardRecordDo)) {
            return null;
        }
        Operator operator = OperatorContext.getOperator();
        AssertUtil.assertNotNull(operator, ErrorCode.ERR_PARAM);
        JackpotUserRewardRecordPo po = new JackpotUserRewardRecordPo();
        po.setUserId(jackpotUserRewardRecordDo.getUserId());
        po.setPlayerName(jackpotUserRewardRecordDo.getPlayerName());
        po.setBetRecordId(jackpotUserRewardRecordDo.getBetRecordId());
        po.setGameRecordId(jackpotUserRewardRecordDo.getGameRecordId());
        po.setBetId(jackpotUserRewardRecordDo.getBetId());
        po.setRewardPoolId(jackpotUserRewardRecordDo.getRewardPoolId());
        po.setRewardPoolType(jackpotUserRewardRecordDo.getRewardPoolType());
        po.setRewardAmount(jackpotUserRewardRecordDo.getRewardAmount());
        po.setRewardGrantSerialNo(jackpotUserRewardRecordDo.getRewardGrantSerialNo());
        po.setRewardGrantStatus(jackpotUserRewardRecordDo.getRewardGrantStatus());
        po.setRewardGrantTime(jackpotUserRewardRecordDo.getRewardGrantTime());
        if (OptEnum.ADD.equals(optEnum)) {
            po.setCreator(operator.getName());
            po.setCreateTime(new Date());
            po.setUpdater(operator.getName());
            po.setUpdateTime(new Date());
        } else {
            po.setUpdater(operator.getName());
            po.setUpdateTime(new Date());
            po.setId(jackpotUserRewardRecordDo.getId());
        }
        return po;
    }

    public static JackpotUserRewardRecordVo doToVo(JackpotUserRewardRecordDo jackpotUserRewardRecordDo) {
        if (Objects.isNull(jackpotUserRewardRecordDo)) {
            return null;
        }
        JackpotUserRewardRecordVo vo = new JackpotUserRewardRecordVo();
        vo.setId(Objects.nonNull(jackpotUserRewardRecordDo.getId()) ? String.valueOf(jackpotUserRewardRecordDo.getId()) : null);
        vo.setUserId(Objects.nonNull(jackpotUserRewardRecordDo.getUserId()) ? String.valueOf(jackpotUserRewardRecordDo.getUserId()) : null);
        vo.setPlayerName(jackpotUserRewardRecordDo.getPlayerName());
        vo.setBetRecordId(Objects.nonNull(jackpotUserRewardRecordDo.getBetRecordId()) ? jackpotUserRewardRecordDo.getBetRecordId().getTime() : null);
        vo.setGameRecordId(Objects.nonNull(jackpotUserRewardRecordDo.getGameRecordId()) ? jackpotUserRewardRecordDo.getGameRecordId().getTime() : null);
        vo.setBetId(Objects.nonNull(jackpotUserRewardRecordDo.getBetId()) ? String.valueOf(jackpotUserRewardRecordDo.getBetId()) : null);
        vo.setRewardPoolId(Objects.nonNull(jackpotUserRewardRecordDo.getRewardPoolId()) ? String.valueOf(jackpotUserRewardRecordDo.getRewardPoolId()) : null);
        vo.setRewardPoolType(jackpotUserRewardRecordDo.getRewardPoolType());
        vo.setRewardAmount(jackpotUserRewardRecordDo.getRewardAmount());
        vo.setRewardGrantSerialNo(jackpotUserRewardRecordDo.getRewardGrantSerialNo());
        vo.setRewardGrantStatus(jackpotUserRewardRecordDo.getRewardGrantStatus());
        vo.setRewardGrantTime(Objects.nonNull(jackpotUserRewardRecordDo.getRewardGrantTime()) ? jackpotUserRewardRecordDo.getRewardGrantTime().getTime() : null);
        vo.setCreator(jackpotUserRewardRecordDo.getCreator());
        vo.setCreateTime(Objects.nonNull(jackpotUserRewardRecordDo.getCreateTime()) ? jackpotUserRewardRecordDo.getCreateTime().getTime() : null);
        vo.setUpdater(jackpotUserRewardRecordDo.getUpdater());
        vo.setUpdateTime(Objects.nonNull(jackpotUserRewardRecordDo.getUpdateTime()) ? jackpotUserRewardRecordDo.getUpdateTime().getTime() : null);
        return vo;
    }

    public static List<JackpotUserRewardRecordDo> voToDoList(List<JackpotUserRewardRecordVo> voList) {
        return Optional.ofNullable(voList)
                .orElse(Collections.emptyList())
                .stream()
                .map(JackpotUserRewardRecordConverter::voToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<JackpotUserRewardRecordDo> poToDoList(List<JackpotUserRewardRecordPo> poList) {
        return Optional.ofNullable(poList)
            .orElse(Collections.emptyList())
            .stream()
            .map(JackpotUserRewardRecordConverter::poToDo)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public static List<JackpotUserRewardRecordPo> doToPoList(List<JackpotUserRewardRecordDo> doList, OptEnum optEnum) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> doToPo(item, optEnum))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<JackpotUserRewardRecordVo> doToVoList(List<JackpotUserRewardRecordDo> doList) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(JackpotUserRewardRecordConverter::doToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static JackpotUserRewardRecordQuery requestToQuery(PageQueryJackpotUserRewardRecordRequest request, String orderClause) {
        JackpotUserRewardRecordQuery query = new JackpotUserRewardRecordQuery();
        query.setId(StringUtils.isNotEmpty(request.getId()) ? Long.valueOf(request.getId()) : null);
        query.setUserId(StringUtils.isNotEmpty(request.getUserId()) ? Long.valueOf(request.getUserId()) : null);
        query.setPlayerName(request.getPlayerName());
        query.setBetRecordId(Objects.nonNull(request.getBetRecordId()) ? new Date(request.getBetRecordId()) : null);
        query.setGameRecordId(Objects.nonNull(request.getGameRecordId()) ? new Date(request.getGameRecordId()) : null);
        query.setBetId(StringUtils.isNotEmpty(request.getBetId()) ? Long.valueOf(request.getBetId()) : null);
        query.setRewardPoolId(StringUtils.isNotEmpty(request.getRewardPoolId()) ? Long.valueOf(request.getRewardPoolId()) : null);
        query.setRewardPoolType(request.getRewardPoolType());
        query.setRewardAmount(StringUtils.isNotEmpty(request.getRewardAmount()) ? Long.valueOf(request.getRewardAmount()) : null);
        query.setRewardGrantSerialNo(request.getRewardGrantSerialNo());
        query.setRewardGrantStatus(request.getRewardGrantStatus());
        query.setRewardGrantTime(Objects.nonNull(request.getRewardGrantTime()) ? new Date(request.getRewardGrantTime()) : null);
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

        query.setRewardPoolTypeList(request.getRewardPoolTypeList());
        return query;
    }

    ///////////////  above auto generated ///////////////

}