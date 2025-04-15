package com.wegame.capi.biz.converter.backend;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.domain.request.query.PageQueryGameMaintainConfigRequest;
import com.wegame.capi.client.domain.vo.GameMaintainConfigVo;
import com.wegame.capi.biz.domain.backend.GameMaintainConfigDo;
import com.wegame.capi.dao.mapper.domain.backend.GameMaintainConfigPo;
import com.wegame.capi.dao.mapper.domain.backend.GameMaintainConfigQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * GameMaintainConfigConverter
 * @author generate
 */
public class GameMaintainConfigConverter {

    public static GameMaintainConfigDo voToDo(GameMaintainConfigVo vo) {
        if (Objects.isNull(vo)) {
            return null;
        }
        GameMaintainConfigDo gameMaintainConfigDo = new GameMaintainConfigDo();
        gameMaintainConfigDo.setGameId(vo.getGameId());
        gameMaintainConfigDo.setMaintainStartTime(Objects.nonNull(vo.getMaintainStartTime()) ? new Date(vo.getMaintainStartTime()) : null);
        gameMaintainConfigDo.setMaintainEndTime(Objects.nonNull(vo.getMaintainEndTime()) ? new Date(vo.getMaintainEndTime()) : null);
        gameMaintainConfigDo.setEnableStatus(vo.getEnableStatus());
        gameMaintainConfigDo.setCreator(vo.getCreator());
        gameMaintainConfigDo.setCreateTime(Objects.nonNull(vo.getCreateTime()) ? new Date(vo.getCreateTime()) : null);
        gameMaintainConfigDo.setUpdater(vo.getUpdater());
        gameMaintainConfigDo.setUpdateTime(Objects.nonNull(vo.getUpdateTime()) ? new Date(vo.getUpdateTime()) : null);
        return gameMaintainConfigDo;
    }

    public static GameMaintainConfigDo poToDo(GameMaintainConfigPo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        GameMaintainConfigDo gameMaintainConfigDo = new GameMaintainConfigDo();
        gameMaintainConfigDo.setId(po.getId());
        gameMaintainConfigDo.setGameId(po.getGameId());
        gameMaintainConfigDo.setMaintainStartTime(po.getMaintainStartTime());
        gameMaintainConfigDo.setMaintainEndTime(po.getMaintainEndTime());
        gameMaintainConfigDo.setEnableStatus(po.getEnableStatus());
        gameMaintainConfigDo.setCreator(po.getCreator());
        gameMaintainConfigDo.setCreateTime(po.getCreateTime());
        gameMaintainConfigDo.setUpdater(po.getUpdater());
        gameMaintainConfigDo.setUpdateTime(po.getUpdateTime());
        return gameMaintainConfigDo;
    }

    public static GameMaintainConfigPo doToPo(GameMaintainConfigDo gameMaintainConfigDo, OptEnum optEnum) {
        if (Objects.isNull(gameMaintainConfigDo)) {
            return null;
        }
        Operator operator = OperatorContext.getOperator();
        AssertUtil.assertNotNull(operator, ErrorCode.ERR_PARAM);
        GameMaintainConfigPo po = new GameMaintainConfigPo();
        po.setGameId(gameMaintainConfigDo.getGameId());
        po.setMaintainStartTime(gameMaintainConfigDo.getMaintainStartTime());
        po.setMaintainEndTime(gameMaintainConfigDo.getMaintainEndTime());
        po.setEnableStatus(gameMaintainConfigDo.getEnableStatus());
        if (OptEnum.ADD.equals(optEnum)) {
            po.setCreator(operator.getName());
            po.setCreateTime(new Date());
            po.setUpdater(operator.getName());
            po.setUpdateTime(new Date());
        } else {
            po.setUpdater(operator.getName());
            po.setUpdateTime(new Date());
            po.setId(gameMaintainConfigDo.getId());
        }
        return po;
    }

    public static GameMaintainConfigVo doToVo(GameMaintainConfigDo gameMaintainConfigDo) {
        if (Objects.isNull(gameMaintainConfigDo)) {
            return null;
        }
        GameMaintainConfigVo vo = new GameMaintainConfigVo();
        vo.setId(Objects.nonNull(gameMaintainConfigDo.getId()) ? String.valueOf(gameMaintainConfigDo.getId()) : null);
        vo.setGameId(gameMaintainConfigDo.getGameId());
        vo.setMaintainStartTime(Objects.nonNull(gameMaintainConfigDo.getMaintainStartTime()) ? gameMaintainConfigDo.getMaintainStartTime().getTime() : null);
        vo.setMaintainEndTime(Objects.nonNull(gameMaintainConfigDo.getMaintainEndTime()) ? gameMaintainConfigDo.getMaintainEndTime().getTime() : null);
        vo.setEnableStatus(gameMaintainConfigDo.getEnableStatus());
        vo.setCreator(gameMaintainConfigDo.getCreator());
        vo.setCreateTime(Objects.nonNull(gameMaintainConfigDo.getCreateTime()) ? gameMaintainConfigDo.getCreateTime().getTime() : null);
        vo.setUpdater(gameMaintainConfigDo.getUpdater());
        vo.setUpdateTime(Objects.nonNull(gameMaintainConfigDo.getUpdateTime()) ? gameMaintainConfigDo.getUpdateTime().getTime() : null);
        return vo;
    }

    public static List<GameMaintainConfigDo> voToDoList(List<GameMaintainConfigVo> voList) {
        return Optional.ofNullable(voList)
                .orElse(Collections.emptyList())
                .stream()
                .map(GameMaintainConfigConverter::voToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<GameMaintainConfigDo> poToDoList(List<GameMaintainConfigPo> poList) {
        return Optional.ofNullable(poList)
            .orElse(Collections.emptyList())
            .stream()
            .map(GameMaintainConfigConverter::poToDo)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public static List<GameMaintainConfigPo> doToPoList(List<GameMaintainConfigDo> doList, OptEnum optEnum) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> doToPo(item, optEnum))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<GameMaintainConfigVo> doToVoList(List<GameMaintainConfigDo> doList) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(GameMaintainConfigConverter::doToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static GameMaintainConfigQuery requestToQuery(PageQueryGameMaintainConfigRequest request, String orderClause) {
        GameMaintainConfigQuery query = new GameMaintainConfigQuery();
        query.setId(StringUtils.isNotEmpty(request.getId()) ? Long.valueOf(request.getId()) : null);
        query.setGameId(request.getGameId());
        query.setMaintainStartTime(Objects.nonNull(request.getMaintainStartTime()) ? new Date(request.getMaintainStartTime()) : null);
        query.setMaintainEndTime(Objects.nonNull(request.getMaintainEndTime()) ? new Date(request.getMaintainEndTime()) : null);
        query.setEnableStatus(request.getEnableStatus());
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