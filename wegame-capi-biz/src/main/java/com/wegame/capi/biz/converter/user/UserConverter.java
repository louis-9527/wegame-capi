package com.wegame.capi.biz.converter.user;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.domain.request.query.PageQueryUserRequest;
import com.wegame.capi.client.domain.vo.UserVo;
import com.wegame.capi.biz.domain.user.UserDo;
import com.wegame.capi.dao.mapper.domain.user.UserPo;
import com.wegame.capi.dao.mapper.domain.user.UserQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * UserConverter
 * @author generate
 */
public class UserConverter {

    public static UserDo voToDo(UserVo vo) {
        if (Objects.isNull(vo)) {
            return null;
        }
        UserDo userDo = new UserDo();
        userDo.setPlatformId(vo.getPlatformId());
        userDo.setPlayerId(vo.getPlayerId());
        userDo.setPlayerName(vo.getPlayerName());
        userDo.setNickName(vo.getNickName());
        userDo.setUserLevel(vo.getUserLevel());
        userDo.setUserName(vo.getUserName());
        userDo.setPresentAmount(StringUtils.isNotEmpty(vo.getPresentAmount()) ? Long.valueOf(vo.getPresentAmount()) : null);
        userDo.setExtend(vo.getExtend());
        userDo.setCreator(vo.getCreator());
        userDo.setCreateTime(Objects.nonNull(vo.getCreateTime()) ? new Date(vo.getCreateTime()) : null);
        userDo.setUpdater(vo.getUpdater());
        userDo.setUpdateTime(Objects.nonNull(vo.getUpdateTime()) ? new Date(vo.getUpdateTime()) : null);
        return userDo;
    }

    public static UserDo poToDo(UserPo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        UserDo userDo = new UserDo();
        userDo.setId(po.getId());
        userDo.setPlatformId(po.getPlatformId());
        userDo.setPlayerId(po.getPlayerId());
        userDo.setPlayerName(po.getPlayerName());
        userDo.setNickName(po.getNickName());
        userDo.setUserLevel(po.getUserLevel());
        userDo.setUserName(po.getUserName());
        userDo.setPresentAmount(po.getPresentAmount());
        userDo.setExtend(po.getExtend());
        userDo.setCreator(po.getCreator());
        userDo.setCreateTime(po.getCreateTime());
        userDo.setUpdater(po.getUpdater());
        userDo.setUpdateTime(po.getUpdateTime());
        return userDo;
    }

    public static UserPo doToPo(UserDo userDo, OptEnum optEnum) {
        if (Objects.isNull(userDo)) {
            return null;
        }
        Operator operator = OperatorContext.getOperator();
        AssertUtil.assertNotNull(operator, ErrorCode.ERR_PARAM);
        UserPo po = new UserPo();
        po.setPlatformId(userDo.getPlatformId());
        po.setPlayerId(userDo.getPlayerId());
        po.setPlayerName(userDo.getPlayerName());
        po.setNickName(userDo.getNickName());
        po.setUserLevel(userDo.getUserLevel());
        po.setUserName(userDo.getUserName());
        po.setPresentAmount(userDo.getPresentAmount());
        po.setExtend(userDo.getExtend());
        if (OptEnum.ADD.equals(optEnum)) {
            po.setCreator(operator.getName());
            po.setCreateTime(new Date());
            po.setUpdater(operator.getName());
            po.setUpdateTime(new Date());
        } else {
            po.setUpdater(operator.getName());
            po.setUpdateTime(new Date());
            po.setId(userDo.getId());
        }
        return po;
    }

    public static UserVo doToVo(UserDo userDo) {
        if (Objects.isNull(userDo)) {
            return null;
        }
        UserVo vo = new UserVo();
        vo.setId(Objects.nonNull(userDo.getId()) ? String.valueOf(userDo.getId()) : null);
        vo.setPlatformId(userDo.getPlatformId());
        vo.setPlayerId(userDo.getPlayerId());
        vo.setPlayerName(userDo.getPlayerName());
        vo.setNickName(userDo.getNickName());
        vo.setUserLevel(userDo.getUserLevel());
        vo.setUserName(userDo.getUserName());
        vo.setPresentAmount(Objects.nonNull(userDo.getPresentAmount()) ? String.valueOf(userDo.getPresentAmount()) : null);
        vo.setExtend(userDo.getExtend());
        vo.setCreator(userDo.getCreator());
        vo.setCreateTime(Objects.nonNull(userDo.getCreateTime()) ? userDo.getCreateTime().getTime() : null);
        vo.setUpdater(userDo.getUpdater());
        vo.setUpdateTime(Objects.nonNull(userDo.getUpdateTime()) ? userDo.getUpdateTime().getTime() : null);
        return vo;
    }

    public static List<UserDo> voToDoList(List<UserVo> voList) {
        return Optional.ofNullable(voList)
                .orElse(Collections.emptyList())
                .stream()
                .map(UserConverter::voToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<UserDo> poToDoList(List<UserPo> poList) {
        return Optional.ofNullable(poList)
            .orElse(Collections.emptyList())
            .stream()
            .map(UserConverter::poToDo)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public static List<UserPo> doToPoList(List<UserDo> doList, OptEnum optEnum) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> doToPo(item, optEnum))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<UserVo> doToVoList(List<UserDo> doList) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(UserConverter::doToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static UserQuery requestToQuery(PageQueryUserRequest request, String orderClause) {
        UserQuery query = new UserQuery();
        query.setId(StringUtils.isNotEmpty(request.getId()) ? Long.valueOf(request.getId()) : null);
        query.setPlatformId(request.getPlatformId());
        query.setPlayerId(request.getPlayerId());
        query.setPlayerName(request.getPlayerName());
        query.setNickName(request.getNickName());
        query.setUserLevel(request.getUserLevel());
        query.setUserName(request.getUserName());
        query.setPresentAmount(StringUtils.isNotEmpty(request.getPresentAmount()) ? Long.valueOf(request.getPresentAmount()) : null);
        query.setExtend(request.getExtend());
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