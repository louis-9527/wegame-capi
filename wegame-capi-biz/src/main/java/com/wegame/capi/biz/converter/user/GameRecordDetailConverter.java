package com.wegame.capi.biz.converter.user;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.domain.request.query.PageQueryGameRecordDetailRequest;
import com.wegame.capi.client.domain.vo.GameRecordDetailVo;
import com.wegame.capi.biz.domain.user.GameRecordDetailDo;
import com.wegame.capi.dao.mapper.domain.user.GameRecordDetailPo;
import com.wegame.capi.dao.mapper.domain.user.GameRecordDetailQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * GameRecordDetailConverter
 * @author generate
 */
public class GameRecordDetailConverter {

    public static GameRecordDetailDo voToDo(GameRecordDetailVo vo) {
        if (Objects.isNull(vo)) {
            return null;
        }
        GameRecordDetailDo gameRecordDetailDo = new GameRecordDetailDo();
        gameRecordDetailDo.setBetAmount(StringUtils.isNotEmpty(vo.getBetAmount()) ? Long.valueOf(vo.getBetAmount()) : null);
        gameRecordDetailDo.setPayOutAmount(StringUtils.isNotEmpty(vo.getPayOutAmount()) ? Long.valueOf(vo.getPayOutAmount()) : null);
        gameRecordDetailDo.setGameResult(vo.getGameResult());
        gameRecordDetailDo.setWinResult(vo.getWinResult());
        gameRecordDetailDo.setOdds(vo.getOdds());
        gameRecordDetailDo.setMultiple(vo.getMultiple());
        return gameRecordDetailDo;
    }

    public static GameRecordDetailDo poToDo(GameRecordDetailPo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        GameRecordDetailDo gameRecordDetailDo = new GameRecordDetailDo();
        gameRecordDetailDo.setId(po.getId());
        gameRecordDetailDo.setBetAmount(po.getBetAmount());
        gameRecordDetailDo.setPayOutAmount(po.getPayOutAmount());
        gameRecordDetailDo.setGameResult(po.getGameResult());
        gameRecordDetailDo.setWinResult(po.getWinResult());
        gameRecordDetailDo.setOdds(po.getOdds());
        gameRecordDetailDo.setMultiple(po.getMultiple());
        return gameRecordDetailDo;
    }

    public static GameRecordDetailPo doToPo(GameRecordDetailDo gameRecordDetailDo, OptEnum optEnum) {
        if (Objects.isNull(gameRecordDetailDo)) {
            return null;
        }
        Operator operator = OperatorContext.getOperator();
        AssertUtil.assertNotNull(operator, ErrorCode.ERR_PARAM);
        GameRecordDetailPo po = new GameRecordDetailPo();
        po.setBetAmount(gameRecordDetailDo.getBetAmount());
        po.setPayOutAmount(gameRecordDetailDo.getPayOutAmount());
        po.setGameResult(gameRecordDetailDo.getGameResult());
        po.setWinResult(gameRecordDetailDo.getWinResult());
        po.setOdds(gameRecordDetailDo.getOdds());
        po.setMultiple(gameRecordDetailDo.getMultiple());
        if (OptEnum.ADD.equals(optEnum)) {
        } else {
            po.setId(gameRecordDetailDo.getId());
        }
        return po;
    }

    public static GameRecordDetailVo doToVo(GameRecordDetailDo gameRecordDetailDo) {
        if (Objects.isNull(gameRecordDetailDo)) {
            return null;
        }
        GameRecordDetailVo vo = new GameRecordDetailVo();
        vo.setId(Objects.nonNull(gameRecordDetailDo.getId()) ? String.valueOf(gameRecordDetailDo.getId()) : null);
        vo.setBetAmount(Objects.nonNull(gameRecordDetailDo.getBetAmount()) ? String.valueOf(gameRecordDetailDo.getBetAmount()) : null);
        vo.setPayOutAmount(Objects.nonNull(gameRecordDetailDo.getPayOutAmount()) ? String.valueOf(gameRecordDetailDo.getPayOutAmount()) : null);
        vo.setGameResult(gameRecordDetailDo.getGameResult());
        vo.setWinResult(gameRecordDetailDo.getWinResult());
        vo.setOdds(gameRecordDetailDo.getOdds());
        vo.setMultiple(gameRecordDetailDo.getMultiple());
        return vo;
    }

    public static List<GameRecordDetailDo> voToDoList(List<GameRecordDetailVo> voList) {
        return Optional.ofNullable(voList)
                .orElse(Collections.emptyList())
                .stream()
                .map(GameRecordDetailConverter::voToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<GameRecordDetailDo> poToDoList(List<GameRecordDetailPo> poList) {
        return Optional.ofNullable(poList)
            .orElse(Collections.emptyList())
            .stream()
            .map(GameRecordDetailConverter::poToDo)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public static List<GameRecordDetailPo> doToPoList(List<GameRecordDetailDo> doList, OptEnum optEnum) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> doToPo(item, optEnum))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<GameRecordDetailVo> doToVoList(List<GameRecordDetailDo> doList) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(GameRecordDetailConverter::doToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static GameRecordDetailQuery requestToQuery(PageQueryGameRecordDetailRequest request, String orderClause) {
        GameRecordDetailQuery query = new GameRecordDetailQuery();
        query.setId(StringUtils.isNotEmpty(request.getId()) ? Long.valueOf(request.getId()) : null);
        query.setBetAmount(StringUtils.isNotEmpty(request.getBetAmount()) ? Long.valueOf(request.getBetAmount()) : null);
        query.setPayOutAmount(StringUtils.isNotEmpty(request.getPayOutAmount()) ? Long.valueOf(request.getPayOutAmount()) : null);
        query.setGameResult(request.getGameResult());
        query.setWinResult(request.getWinResult());
        query.setOdds(request.getOdds());
        query.setMultiple(request.getMultiple());
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