package com.wegame.capi.biz.converter.user;

import com.wegame.capi.biz.domain.user.UserDo;
import com.wegame.base.context.Operator;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.OperatorUtil;
import com.wegame.capi.client.domain.vo.UserVo;
import com.wegame.capi.dao.mapper.domain.user.UserPo;
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
        userDo.setPlayerId(vo.getPlayerId());
        userDo.setName(vo.getName());
        userDo.setNickName(vo.getNickName());
        userDo.setRealName(vo.getRealName());
        userDo.setPhoneNumber(vo.getPhoneNumber());
        userDo.setGender(vo.getGender());
        userDo.setPlayerAvatar(vo.getPlayerAvatar());
        userDo.setPassword(vo.getPassword());
        userDo.setEmail(vo.getEmail());
        userDo.setNationality(vo.getNationality());
        userDo.setProvince(vo.getProvince());
        userDo.setCity(vo.getCity());
        userDo.setAddress(vo.getAddress());
        userDo.setPlatformId(vo.getPlatformId());
        userDo.setRegistrationTime(Objects.nonNull(vo.getRegistrationTime()) ? new Date(vo.getRegistrationTime()) : null);
        userDo.setStatus(vo.getStatus());
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
        userDo.setPlayerId(po.getPlayerId());
        userDo.setName(po.getName());
        userDo.setNickName(po.getNickName());
        userDo.setRealName(po.getRealName());
        userDo.setPhoneNumber(po.getPhoneNumber());
        userDo.setGender(po.getGender());
        userDo.setPlayerAvatar(po.getPlayerAvatar());
        userDo.setPassword(po.getPassword());
        userDo.setEmail(po.getEmail());
        userDo.setNationality(po.getNationality());
        userDo.setProvince(po.getProvince());
        userDo.setCity(po.getCity());
        userDo.setAddress(po.getAddress());
        userDo.setPlatformId(po.getPlatformId());
        userDo.setRegistrationTime(po.getRegistrationTime());
        userDo.setStatus(po.getStatus());
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
        Operator operator = OperatorUtil.getOperator();
        AssertUtil.assertNotNull(operator, ErrorCode.ERR_PARAM);
        UserPo po = new UserPo();
        po.setPlayerId(userDo.getPlayerId());
        po.setName(userDo.getName());
        po.setNickName(userDo.getNickName());
        po.setRealName(userDo.getRealName());
        po.setPhoneNumber(userDo.getPhoneNumber());
        po.setGender(userDo.getGender());
        po.setPlayerAvatar(userDo.getPlayerAvatar());
        po.setPassword(userDo.getPassword());
        po.setEmail(userDo.getEmail());
        po.setNationality(userDo.getNationality());
        po.setProvince(userDo.getProvince());
        po.setCity(userDo.getCity());
        po.setAddress(userDo.getAddress());
        po.setPlatformId(userDo.getPlatformId());
        po.setRegistrationTime(userDo.getRegistrationTime());
        po.setStatus(userDo.getStatus());
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
        vo.setPlayerId(userDo.getPlayerId());
        vo.setName(userDo.getName());
        vo.setNickName(userDo.getNickName());
        vo.setRealName(userDo.getRealName());
        vo.setPhoneNumber(userDo.getPhoneNumber());
        vo.setGender(userDo.getGender());
        vo.setPlayerAvatar(userDo.getPlayerAvatar());
        vo.setPassword(userDo.getPassword());
        vo.setEmail(userDo.getEmail());
        vo.setNationality(userDo.getNationality());
        vo.setProvince(userDo.getProvince());
        vo.setCity(userDo.getCity());
        vo.setAddress(userDo.getAddress());
        vo.setPlatformId(userDo.getPlatformId());
        vo.setRegistrationTime(Objects.nonNull(userDo.getRegistrationTime()) ? userDo.getRegistrationTime().getTime() : null);
        vo.setStatus(userDo.getStatus());
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

    ///////////////  above auto generated ///////////////

}