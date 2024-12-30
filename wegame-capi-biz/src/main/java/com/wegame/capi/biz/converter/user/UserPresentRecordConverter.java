package com.wegame.capi.biz.converter.user;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.domain.request.query.PageQueryUserPresentRecordRequest;
import com.wegame.capi.client.domain.vo.UserPresentRecordVo;
import com.wegame.capi.biz.domain.user.UserPresentRecordDo;
import com.wegame.capi.dao.mapper.domain.user.UserPresentRecordPo;
import com.wegame.capi.dao.mapper.domain.user.UserPresentRecordQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * UserPresentRecordConverter
 * @author generate
 */
public class UserPresentRecordConverter {

    public static UserPresentRecordDo voToDo(UserPresentRecordVo vo) {
        if (Objects.isNull(vo)) {
            return null;
        }
        UserPresentRecordDo userPresentRecordDo = new UserPresentRecordDo();
        userPresentRecordDo.setUserId(StringUtils.isNotEmpty(vo.getUserId()) ? Long.valueOf(vo.getUserId()) : null);
        userPresentRecordDo.setPlayerId(vo.getPlayerId());
        userPresentRecordDo.setType(vo.getType());
        userPresentRecordDo.setSubType(vo.getSubType());
        userPresentRecordDo.setBizId(vo.getBizId());
        userPresentRecordDo.setAmount(StringUtils.isNotEmpty(vo.getAmount()) ? Long.valueOf(vo.getAmount()) : null);
        userPresentRecordDo.setStatus(vo.getStatus());
        userPresentRecordDo.setCreator(vo.getCreator());
        userPresentRecordDo.setCreateTime(Objects.nonNull(vo.getCreateTime()) ? new Date(vo.getCreateTime()) : null);
        userPresentRecordDo.setUpdater(vo.getUpdater());
        userPresentRecordDo.setUpdateTime(Objects.nonNull(vo.getUpdateTime()) ? new Date(vo.getUpdateTime()) : null);
        return userPresentRecordDo;
    }

    public static UserPresentRecordDo poToDo(UserPresentRecordPo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        UserPresentRecordDo userPresentRecordDo = new UserPresentRecordDo();
        userPresentRecordDo.setId(po.getId());
        userPresentRecordDo.setUserId(po.getUserId());
        userPresentRecordDo.setPlayerId(po.getPlayerId());
        userPresentRecordDo.setType(po.getType());
        userPresentRecordDo.setSubType(po.getSubType());
        userPresentRecordDo.setBizId(po.getBizId());
        userPresentRecordDo.setAmount(po.getAmount());
        userPresentRecordDo.setStatus(po.getStatus());
        userPresentRecordDo.setCreator(po.getCreator());
        userPresentRecordDo.setCreateTime(po.getCreateTime());
        userPresentRecordDo.setUpdater(po.getUpdater());
        userPresentRecordDo.setUpdateTime(po.getUpdateTime());
        return userPresentRecordDo;
    }

    public static UserPresentRecordPo doToPo(UserPresentRecordDo userPresentRecordDo, OptEnum optEnum) {
        if (Objects.isNull(userPresentRecordDo)) {
            return null;
        }
        Operator operator = OperatorContext.getOperator();
        AssertUtil.assertNotNull(operator, ErrorCode.ERR_PARAM);
        UserPresentRecordPo po = new UserPresentRecordPo();
        po.setUserId(userPresentRecordDo.getUserId());
        po.setPlayerId(userPresentRecordDo.getPlayerId());
        po.setType(userPresentRecordDo.getType());
        po.setSubType(userPresentRecordDo.getSubType());
        po.setBizId(userPresentRecordDo.getBizId());
        po.setAmount(userPresentRecordDo.getAmount());
        po.setStatus(userPresentRecordDo.getStatus());
        if (OptEnum.ADD.equals(optEnum)) {
            po.setCreator(operator.getName());
            po.setCreateTime(new Date());
            po.setUpdater(operator.getName());
            po.setUpdateTime(new Date());
        } else {
            po.setUpdater(operator.getName());
            po.setUpdateTime(new Date());
            po.setId(userPresentRecordDo.getId());
        }
        return po;
    }

    public static UserPresentRecordVo doToVo(UserPresentRecordDo userPresentRecordDo) {
        if (Objects.isNull(userPresentRecordDo)) {
            return null;
        }
        UserPresentRecordVo vo = new UserPresentRecordVo();
        vo.setId(Objects.nonNull(userPresentRecordDo.getId()) ? String.valueOf(userPresentRecordDo.getId()) : null);
        vo.setUserId(Objects.nonNull(userPresentRecordDo.getUserId()) ? String.valueOf(userPresentRecordDo.getUserId()) : null);
        vo.setPlayerId(userPresentRecordDo.getPlayerId());
        vo.setType(userPresentRecordDo.getType());
        vo.setSubType(userPresentRecordDo.getSubType());
        vo.setBizId(userPresentRecordDo.getBizId());
        vo.setAmount(Objects.nonNull(userPresentRecordDo.getAmount()) ? String.valueOf(userPresentRecordDo.getAmount()) : null);
        vo.setStatus(userPresentRecordDo.getStatus());
        vo.setCreator(userPresentRecordDo.getCreator());
        vo.setCreateTime(Objects.nonNull(userPresentRecordDo.getCreateTime()) ? userPresentRecordDo.getCreateTime().getTime() : null);
        vo.setUpdater(userPresentRecordDo.getUpdater());
        vo.setUpdateTime(Objects.nonNull(userPresentRecordDo.getUpdateTime()) ? userPresentRecordDo.getUpdateTime().getTime() : null);
        return vo;
    }

    public static List<UserPresentRecordDo> voToDoList(List<UserPresentRecordVo> voList) {
        return Optional.ofNullable(voList)
                .orElse(Collections.emptyList())
                .stream()
                .map(UserPresentRecordConverter::voToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<UserPresentRecordDo> poToDoList(List<UserPresentRecordPo> poList) {
        return Optional.ofNullable(poList)
            .orElse(Collections.emptyList())
            .stream()
            .map(UserPresentRecordConverter::poToDo)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public static List<UserPresentRecordPo> doToPoList(List<UserPresentRecordDo> doList, OptEnum optEnum) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> doToPo(item, optEnum))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<UserPresentRecordVo> doToVoList(List<UserPresentRecordDo> doList) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(UserPresentRecordConverter::doToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static UserPresentRecordQuery requestToQuery(PageQueryUserPresentRecordRequest request, String orderClause) {
        UserPresentRecordQuery query = new UserPresentRecordQuery();
        query.setId(StringUtils.isNotEmpty(request.getId()) ? Long.valueOf(request.getId()) : null);
        query.setUserId(StringUtils.isNotEmpty(request.getUserId()) ? Long.valueOf(request.getUserId()) : null);
        query.setPlayerId(request.getPlayerId());
        query.setType(request.getType());
        query.setSubType(request.getSubType());
        query.setBizId(request.getBizId());
        query.setAmount(StringUtils.isNotEmpty(request.getAmount()) ? Long.valueOf(request.getAmount()) : null);
        query.setStatus(request.getStatus());
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