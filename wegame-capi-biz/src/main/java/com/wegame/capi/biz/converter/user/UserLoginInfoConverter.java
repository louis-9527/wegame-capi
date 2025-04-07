package com.wegame.capi.biz.converter.user;

import com.wegame.base.context.Operator;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.OperatorUtil;
import com.wegame.capi.biz.domain.user.UserLoginInfoDo;
import com.wegame.capi.client.domain.vo.UserLoginInfoVo;
import com.wegame.capi.dao.mapper.domain.user.UserLoginInfoPo;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * UserLoginInfoConverter
 *
 * @author generate
 */
public class UserLoginInfoConverter {

    public static UserLoginInfoDo voToDo(UserLoginInfoVo vo) {
        if (Objects.isNull(vo)) {
            return null;
        }
        UserLoginInfoDo userLoginInfoDo = new UserLoginInfoDo();
        userLoginInfoDo.setChannelId(StringUtils.isNotEmpty(vo.getChannelId()) ? Long.valueOf(vo.getChannelId()) : null);
        userLoginInfoDo.setGameId(vo.getGameId());
        userLoginInfoDo.setUserId(StringUtils.isNotEmpty(vo.getUserId()) ? Long.valueOf(vo.getUserId()) : null);
        userLoginInfoDo.setPlayerId(StringUtils.isNotEmpty(vo.getPlayerId()) ? vo.getPlayerId() : null);
        userLoginInfoDo.setRegisterTime(Objects.nonNull(vo.getRegisterTime()) ? new Date(vo.getRegisterTime()) : null);
        userLoginInfoDo.setLoginTime(Objects.nonNull(vo.getLoginTime()) ? new Date(vo.getLoginTime()) : null);
        userLoginInfoDo.setCreator(vo.getCreator());
        userLoginInfoDo.setCreateTime(Objects.nonNull(vo.getCreateTime()) ? new Date(vo.getCreateTime()) : null);
        userLoginInfoDo.setUpdater(vo.getUpdater());
        userLoginInfoDo.setUpdateTime(Objects.nonNull(vo.getUpdateTime()) ? new Date(vo.getUpdateTime()) : null);
        return userLoginInfoDo;
    }

    public static UserLoginInfoDo poToDo(UserLoginInfoPo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        UserLoginInfoDo userLoginInfoDo = new UserLoginInfoDo();
        userLoginInfoDo.setId(po.getId());
        userLoginInfoDo.setChannelId(po.getChannelId());
        userLoginInfoDo.setGameId(po.getGameId());
        userLoginInfoDo.setUserId(po.getUserId());
        userLoginInfoDo.setPlayerId(po.getPlayerId());
        userLoginInfoDo.setRegisterTime(po.getRegisterTime());
        userLoginInfoDo.setLoginTime(po.getLoginTime());
        userLoginInfoDo.setCreator(po.getCreator());
        userLoginInfoDo.setCreateTime(po.getCreateTime());
        userLoginInfoDo.setUpdater(po.getUpdater());
        userLoginInfoDo.setUpdateTime(po.getUpdateTime());
        return userLoginInfoDo;
    }

    public static UserLoginInfoPo doToPo(UserLoginInfoDo userLoginInfoDo, OptEnum optEnum) {
        if (Objects.isNull(userLoginInfoDo)) {
            return null;
        }
        Operator operator = OperatorUtil.getOperator();
        AssertUtil.assertNotNull(operator, ErrorCode.ERR_PARAM);
        UserLoginInfoPo po = new UserLoginInfoPo();
        po.setChannelId(userLoginInfoDo.getChannelId());
        po.setGameId(userLoginInfoDo.getGameId());
        po.setUserId(userLoginInfoDo.getUserId());
        po.setPlayerId(userLoginInfoDo.getPlayerId());
        po.setRegisterTime(userLoginInfoDo.getRegisterTime());
        po.setLoginTime(userLoginInfoDo.getLoginTime());
        if (OptEnum.ADD.equals(optEnum)) {
            po.setCreator(operator.getName());
            po.setCreateTime(new Date());
            po.setUpdater(operator.getName());
            po.setUpdateTime(new Date());
        } else {
            po.setUpdater(operator.getName());
            po.setUpdateTime(new Date());
            po.setId(userLoginInfoDo.getId());
        }
        return po;
    }

    public static UserLoginInfoVo doToVo(UserLoginInfoDo userLoginInfoDo) {
        if (Objects.isNull(userLoginInfoDo)) {
            return null;
        }
        UserLoginInfoVo vo = new UserLoginInfoVo();
        vo.setId(Objects.nonNull(userLoginInfoDo.getId()) ? String.valueOf(userLoginInfoDo.getId()) : null);
        vo.setChannelId(Objects.nonNull(userLoginInfoDo.getChannelId()) ? String.valueOf(userLoginInfoDo.getChannelId()) : null);
        vo.setGameId(userLoginInfoDo.getGameId());
        vo.setUserId(Objects.nonNull(userLoginInfoDo.getUserId()) ? String.valueOf(userLoginInfoDo.getUserId()) : null);
        vo.setPlayerId(Objects.nonNull(userLoginInfoDo.getPlayerId()) ? String.valueOf(userLoginInfoDo.getPlayerId()) : null);
        vo.setRegisterTime(Objects.nonNull(userLoginInfoDo.getRegisterTime()) ? userLoginInfoDo.getRegisterTime().getTime() : null);
        vo.setLoginTime(Objects.nonNull(userLoginInfoDo.getLoginTime()) ? userLoginInfoDo.getLoginTime().getTime() : null);
        vo.setCreator(userLoginInfoDo.getCreator());
        vo.setCreateTime(Objects.nonNull(userLoginInfoDo.getCreateTime()) ? userLoginInfoDo.getCreateTime().getTime() : null);
        vo.setUpdater(userLoginInfoDo.getUpdater());
        vo.setUpdateTime(Objects.nonNull(userLoginInfoDo.getUpdateTime()) ? userLoginInfoDo.getUpdateTime().getTime() : null);
        return vo;
    }

    public static List<UserLoginInfoDo> voToDoList(List<UserLoginInfoVo> voList) {
        return Optional.ofNullable(voList)
                .orElse(Collections.emptyList())
                .stream()
                .map(UserLoginInfoConverter::voToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<UserLoginInfoDo> poToDoList(List<UserLoginInfoPo> poList) {
        return Optional.ofNullable(poList)
                .orElse(Collections.emptyList())
                .stream()
                .map(UserLoginInfoConverter::poToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<UserLoginInfoPo> doToPoList(List<UserLoginInfoDo> doList, OptEnum optEnum) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> doToPo(item, optEnum))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<UserLoginInfoVo> doToVoList(List<UserLoginInfoDo> doList) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(UserLoginInfoConverter::doToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    ///////////////  above auto generated ///////////////

}