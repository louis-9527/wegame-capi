package com.wegame.capi.biz.converter.user;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.GameRecordDetailDo;
import com.wegame.capi.client.domain.request.query.PageQueryGameRecordDetailRequest;
import com.wegame.capi.client.domain.vo.GameRecordDetailVo;
import com.wegame.capi.dao.mapper.domain.user.GameRecordDetailPo;
import com.wegame.capi.dao.mapper.domain.user.GameRecordDetailQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * GameRecordDetailConverter
 *
 * @author generate
 */
public class GameRecordDetailConverter {

    public static GameRecordDetailDo voToDo(GameRecordDetailVo vo) {
        if (Objects.isNull(vo)) {
            return null;
        }
        GameRecordDetailDo gameRecordDetailDo = new GameRecordDetailDo();
        gameRecordDetailDo.setBetAmount(StringUtils.isNotEmpty(vo.getBetAmount()) ? Long.valueOf(vo.getBetAmount()) : null);
        gameRecordDetailDo.setRewardAmount(StringUtils.isNotEmpty(vo.getRewardAmount()) ? Long.valueOf(vo.getRewardAmount()) : null);
        gameRecordDetailDo.setGameResult(vo.getGameResult());
        gameRecordDetailDo.setRewardResult(vo.getRewardResult());
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
        gameRecordDetailDo.setRewardAmount(po.getRewardAmount());
        gameRecordDetailDo.setGameResult(po.getGameResult());
        gameRecordDetailDo.setRewardResult(po.getRewardResult());
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
        po.setRewardAmount(gameRecordDetailDo.getRewardAmount());
        po.setGameResult(gameRecordDetailDo.getGameResult());
        po.setRewardResult(gameRecordDetailDo.getRewardResult());
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
        vo.setRewardAmount(Objects.nonNull(gameRecordDetailDo.getRewardAmount()) ? String.valueOf(gameRecordDetailDo.getRewardAmount()) : null);
        vo.setGameResult(gameRecordDetailDo.getGameResult());
        vo.setRewardResult(gameRecordDetailDo.getRewardResult());
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
        // query.setId(StringUtils.isNotEmpty(request.getId()) ? Long.valueOf(request.getId()) : null);
        query.setBetAmount(StringUtils.isNotEmpty(request.getBetAmount()) ? Long.valueOf(request.getBetAmount()) : null);
        query.setRewardAmount(StringUtils.isNotEmpty(request.getRewardAmount()) ? Long.valueOf(request.getRewardAmount()) : null);
        query.setGameResult(request.getGameResult());
        query.setRewardResult(request.getRewardResult());
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