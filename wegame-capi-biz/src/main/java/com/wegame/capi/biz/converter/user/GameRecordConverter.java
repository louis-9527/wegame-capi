package com.wegame.capi.biz.converter.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.GameBetResultDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.dao.mapper.domain.user.GameRecordDetailPo;
import com.wegame.capi.dao.mapper.domain.user.BetRecordPo;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * GameRecordConverter
 *
 * @author generate
 */
public class GameRecordConverter {
//
//    public static GameRecordDo voToDo(GameRecordVo vo) {
//        if (Objects.isNull(vo)) {
//            return null;
//        }
//        GameRecordDo gameRecordDo = new GameRecordDo();
//        gameRecordDo.setPId(vo.getPId());
//        gameRecordDo.setChannelId(vo.getChannelId());
//        gameRecordDo.setGameId(vo.getGameId());
//        gameRecordDo.setUserId(StringUtils.isNotEmpty(vo.getUserId()) ? Long.valueOf(vo.getUserId()) : null);
//        gameRecordDo.setPlayerId(vo.getPlayerId());
//        gameRecordDo.setPlayerName(vo.getPlayerName());
//        gameRecordDo.setBetType(vo.getBetType());
//        gameRecordDo.setBetSubType(vo.getBetSubType());
//        gameRecordDo.setBetId(vo.getBetId());
//        gameRecordDo.setBetSerialNo(vo.getBetSerialNo());
//        gameRecordDo.setBetAmount(StringUtils.isNotEmpty(vo.getBetAmount()) ? Long.valueOf(vo.getBetAmount()) : null);
//        gameRecordDo.setPayOutAmount(StringUtils.isNotEmpty(vo.getPayOutAmount()) ? Long.valueOf(vo.getPayOutAmount()) : null);
//        gameRecordDo.setMarketFlag(vo.getMarketFlag());
//        gameRecordDo.setStatus(vo.getStatus());
//        gameRecordDo.setFreeSpinsFlag(vo.getFreeSpinsFlag());
//        gameRecordDo.setFreeSpinsTimes(vo.getFreeSpinsTimes());
//        gameRecordDo.setDetail(vo.getDetail());
//        gameRecordDo.setCreator(vo.getCreator());
//        gameRecordDo.setCreateTime(Objects.nonNull(vo.getCreateTime()) ? new Date(vo.getCreateTime()) : null);
//        gameRecordDo.setUpdater(vo.getUpdater());
//        gameRecordDo.setUpdateTime(Objects.nonNull(vo.getUpdateTime()) ? new Date(vo.getUpdateTime()) : null);
//        return gameRecordDo;
//    }
//
//    public static GameRecordDo poToDo(GameRecordPo po) {
//        if (Objects.isNull(po)) {
//            return null;
//        }
//        GameRecordDo gameRecordDo = new GameRecordDo();
//        gameRecordDo.setId(po.getId());
//        gameRecordDo.setPId(po.getPId());
//        gameRecordDo.setChannelId(po.getChannelId());
//        gameRecordDo.setGameId(po.getGameId());
//        gameRecordDo.setUserId(po.getUserId());
//        gameRecordDo.setPlayerId(po.getPlayerId());
//        gameRecordDo.setPlayerName(po.getPlayerName());
//        gameRecordDo.setBetType(po.getBetType());
//        gameRecordDo.setBetSubType(po.getBetSubType());
//        gameRecordDo.setBetId(po.getBetId());
//        gameRecordDo.setBetSerialNo(po.getBetSerialNo());
//        gameRecordDo.setBetAmount(po.getBetAmount());
//        gameRecordDo.setPayOutAmount(po.getPayOutAmount());
//        gameRecordDo.setMarketFlag(po.getMarketFlag());
//        gameRecordDo.setStatus(po.getStatus());
//        gameRecordDo.setFreeSpinsFlag(po.getFreeSpinsFlag());
//        gameRecordDo.setFreeSpinsTimes(po.getFreeSpinsTimes());
//        gameRecordDo.setDetail(po.getDetail());
//        gameRecordDo.setCreator(po.getCreator());
//        gameRecordDo.setCreateTime(po.getCreateTime());
//        gameRecordDo.setUpdater(po.getUpdater());
//        gameRecordDo.setUpdateTime(po.getUpdateTime());
//        return gameRecordDo;
//    }
//
//    public static GameRecordPo doToPo(GameRecordDo gameRecordDo, OptEnum optEnum) {
//        if (Objects.isNull(gameRecordDo)) {
//            return null;
//        }
//        Operator operator = OperatorContext.getOperator();
//        AssertUtil.assertNotNull(operator, ErrorCode.ERR_PARAM);
//        GameRecordPo po = new GameRecordPo();
//        po.setPId(gameRecordDo.getPId());
//        po.setChannelId(gameRecordDo.getChannelId());
//        po.setGameId(gameRecordDo.getGameId());
//        po.setUserId(gameRecordDo.getUserId());
//        po.setPlayerId(gameRecordDo.getPlayerId());
//        po.setPlayerName(gameRecordDo.getPlayerName());
//        po.setBetType(gameRecordDo.getBetType());
//        po.setBetSubType(gameRecordDo.getBetSubType());
//        po.setBetId(gameRecordDo.getBetId());
//        po.setBetSerialNo(gameRecordDo.getBetSerialNo());
//        po.setBetAmount(gameRecordDo.getBetAmount());
//        po.setPayOutAmount(gameRecordDo.getPayOutAmount());
//        po.setMarketFlag(gameRecordDo.getMarketFlag());
//        po.setStatus(gameRecordDo.getStatus());
//        po.setFreeSpinsFlag(gameRecordDo.getFreeSpinsFlag());
//        po.setFreeSpinsTimes(gameRecordDo.getFreeSpinsTimes());
//        po.setDetail(gameRecordDo.getDetail());
//        if (OptEnum.ADD.equals(optEnum)) {
//            po.setCreator(operator.getName());
//            po.setCreateTime(new Date());
//            po.setUpdater(operator.getName());
//            po.setUpdateTime(new Date());
//        } else {
//            po.setUpdater(operator.getName());
//            po.setUpdateTime(new Date());
//            po.setId(gameRecordDo.getId());
//        }
//        return po;
//    }
//
//    public static GameRecordVo doToVo(GameRecordDo gameRecordDo) {
//        if (Objects.isNull(gameRecordDo)) {
//            return null;
//        }
//        GameRecordVo vo = new GameRecordVo();
//        vo.setId(Objects.nonNull(gameRecordDo.getId()) ? String.valueOf(gameRecordDo.getId()) : null);
//        vo.setPId(gameRecordDo.getPId());
//        vo.setChannelId(gameRecordDo.getChannelId());
//        vo.setGameId(gameRecordDo.getGameId());
//        vo.setUserId(Objects.nonNull(gameRecordDo.getUserId()) ? String.valueOf(gameRecordDo.getUserId()) : null);
//        vo.setPlayerId(gameRecordDo.getPlayerId());
//        vo.setPlayerName(gameRecordDo.getPlayerName());
//        vo.setBetType(gameRecordDo.getBetType());
//        vo.setBetSubType(gameRecordDo.getBetSubType());
//        vo.setBetId(gameRecordDo.getBetId());
//        vo.setBetSerialNo(gameRecordDo.getBetSerialNo());
//        vo.setBetAmount(Objects.nonNull(gameRecordDo.getBetAmount()) ? String.valueOf(gameRecordDo.getBetAmount()) : null);
//        vo.setPayOutAmount(Objects.nonNull(gameRecordDo.getPayOutAmount()) ? String.valueOf(gameRecordDo.getPayOutAmount()) : null);
//        vo.setMarketFlag(gameRecordDo.getMarketFlag());
//        vo.setStatus(gameRecordDo.getStatus());
//        vo.setFreeSpinsFlag(gameRecordDo.getFreeSpinsFlag());
//        vo.setFreeSpinsTimes(gameRecordDo.getFreeSpinsTimes());
//        vo.setDetail(gameRecordDo.getDetail());
//        vo.setCreator(gameRecordDo.getCreator());
//        vo.setCreateTime(Objects.nonNull(gameRecordDo.getCreateTime()) ? gameRecordDo.getCreateTime().getTime() : null);
//        vo.setUpdater(gameRecordDo.getUpdater());
//        vo.setUpdateTime(Objects.nonNull(gameRecordDo.getUpdateTime()) ? gameRecordDo.getUpdateTime().getTime() : null);
//        return vo;
//    }

//
//    public static List<GameRecordDo> poToDoList(List<GameRecordPo> poList) {
//        return Optional.ofNullable(poList)
//                .orElse(Collections.emptyList())
//                .stream()
//                .map(GameRecordConverter::poToDo)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//    }
//
//    public static List<GameRecordPo> doToPoList(List<GameRecordDo> doList, OptEnum optEnum) {
//        return Optional.ofNullable(doList)
//                .orElse(Collections.emptyList())
//                .stream()
//                .map(item -> doToPo(item, optEnum))
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//    }
//
//    public static List<GameRecordVo> doToVoList(List<GameRecordDo> doList) {
//        return Optional.ofNullable(doList)
//                .orElse(Collections.emptyList())
//                .stream()
//                .map(GameRecordConverter::doToVo)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//    }
//
//    public static GameRecordQuery requestToQuery(PageQueryGameRecordRequest request, String orderClause) {
//        GameRecordQuery query = new GameRecordQuery();
//        query.setId(StringUtils.isNotEmpty(request.getId()) ? Long.valueOf(request.getId()) : null);
//        query.setPId(request.getPId());
//        query.setChannelId(request.getChannelId());
//        query.setGameId(request.getGameId());
//        query.setUserId(StringUtils.isNotEmpty(request.getUserId()) ? Long.valueOf(request.getUserId()) : null);
//        query.setPlayerId(request.getPlayerId());
//        query.setPlayerName(request.getPlayerName());
//        query.setBetType(request.getBetType());
//        query.setBetSubType(request.getBetSubType());
//        query.setBetId(request.getBetId());
//        query.setBetSerialNo(request.getBetSerialNo());
//        query.setBetAmount(StringUtils.isNotEmpty(request.getBetAmount()) ? Long.valueOf(request.getBetAmount()) : null);
//        query.setPayOutAmount(StringUtils.isNotEmpty(request.getPayOutAmount()) ? Long.valueOf(request.getPayOutAmount()) : null);
//        query.setMarketFlag(request.getMarketFlag());
//        query.setStatus(request.getStatus());
//        query.setFreeSpinsFlag(request.getFreeSpinsFlag());
//        query.setFreeSpinsTimes(request.getFreeSpinsTimes());
//        query.setDetail(request.getDetail());
//        query.setCreator(request.getCreator());
//        query.setCreateTime(Objects.nonNull(request.getCreateTime()) ? new Date(request.getCreateTime()) : null);
//        query.setUpdater(request.getUpdater());
//        query.setUpdateTime(Objects.nonNull(request.getUpdateTime()) ? new Date(request.getUpdateTime()) : null);
//        query.setStartCreateTime(Objects.nonNull(request.getStartCreateTime()) ? new Date(request.getStartCreateTime()) : null);
//        query.setEndCreateTime(Objects.nonNull(request.getEndCreateTime()) ? new Date(request.getEndCreateTime()) : null);
//        query.setStartUpdateTime(Objects.nonNull(request.getStartUpdateTime()) ? new Date(request.getStartUpdateTime()) : null);
//        query.setEndUpdateTime(Objects.nonNull(request.getEndUpdateTime()) ? new Date(request.getEndUpdateTime()) : null);
//        query.setLimit(request.getPageSize());
//        query.setOffset(request.getOffset());
//        query.setOrderByClause(orderClause);
//        return query;
//    }

    ///////////////  above auto generated ///////////////


    public static BetRecordPo doToPo(BetRecordDo betRecordDo) {
        if (Objects.isNull(betRecordDo)) {
            return null;
        }
        Operator operator = OperatorContext.getOperator();
        AssertUtil.assertNotNull(operator, ErrorCode.ERR_PARAM);
        BetRecordPo po = new BetRecordPo();
        po.setId(betRecordDo.getId());
        po.setBetSerialNo(betRecordDo.getBetSerialNo());
        po.setPId(betRecordDo.getPId());
        po.setChannelId(betRecordDo.getChannelId());
        po.setGameId(betRecordDo.getGameId());
        po.setUserId(operator.getUserId());
        po.setPlayerId(operator.getPlayerId());
        po.setPlayerName(operator.getName());
        po.setBetType(betRecordDo.getBetType());
        po.setBetSubType(betRecordDo.getBetSubType());
        po.setBetSerialNo(betRecordDo.getBetSerialNo());
        po.setBetAmount(betRecordDo.getBetAmount());
        po.setPayOutAmount(betRecordDo.getPayOutAmount());
        po.setMarketFlag(betRecordDo.getMarketFlag());
        po.setStatus(betRecordDo.getStatus());
        po.setDetail(betRecordDo.getDetail());
        po.setCreator(operator.getName());
        po.setCreateTime(new Date());
        po.setUpdater(operator.getName());
        po.setUpdateTime(new Date());
        return po;
    }


    public static List<GameRecordDetailPo> doToDetailPoList(List<GameBetResultDetailDo> doList) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(GameRecordConverter::doToDetailPo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static GameRecordDetailPo doToDetailPo(GameBetResultDetailDo detailDo) {
        if (detailDo == null) {
            return null;
        }
        GameRecordDetailPo detailPo = new GameRecordDetailPo();
        detailPo.setId(detailDo.getId());
        detailPo.setGameResult(detailDo.getGameResult() == null ? null : JSON.toJSONString(detailDo.getGameResult()));
        detailPo.setMultiple(detailDo.getMultiple());
        detailPo.setBetAmount(detailDo.getBetAmount());
        detailPo.setRewardAmount(detailDo.getRewardAmount());
        detailPo.setFreeSpinsFlag(detailDo.isFreeSpinsFlag());
        detailPo.setRewardResult(detailDo.getRewardResults() == null ? null : JSON.toJSONString(detailDo.getRewardResults()));

        return detailPo;
    }

    public static GameBetResultDetailDo poToDetailDo(GameRecordDetailPo detailPo) {
        if (detailPo == null) {
            return null;
        }
        GameBetResultDetailDo detaildo = new GameBetResultDetailDo();
        detaildo.setId(detaildo.getId());
        detaildo.setMultiple(detaildo.getMultiple());
        detaildo.setBetAmount(detaildo.getBetAmount());
        detaildo.setRewardAmount(detaildo.getRewardAmount());
        detaildo.setFreeSpinsFlag(detailPo.isFreeSpinsFlag());
        detaildo.setGameResult(convertJsonToSymbolDoArray(detailPo.getGameResult()));

        detaildo.setRewardResults(StringUtils.isEmpty(detailPo.getRewardResult()) ? null : JSON.parseArray(detailPo.getRewardResult(), GameBetResultDetailDo.RewardResult.class));

        return detaildo;
    }

    private static SymbolDo[][] convertJsonToSymbolDoArray(String jsonString) {
        JSONArray jsonArray = JSON.parseArray(jsonString);
        int rows = jsonArray.size();
        int cols = jsonArray.getJSONArray(0).size();

        SymbolDo[][] symbolDoArray = new SymbolDo[rows][cols];

        for (int i = 0; i < rows; i++) {
            JSONArray rowArray = jsonArray.getJSONArray(i);
            for (int j = 0; j < cols; j++) {
                JSONObject jsonObject = rowArray.getJSONObject(j);
                SymbolDo symbolDo = jsonObject.toJavaObject(SymbolDo.class);
                symbolDoArray[i][j] = symbolDo;
            }
        }

        return symbolDoArray;
    }

}