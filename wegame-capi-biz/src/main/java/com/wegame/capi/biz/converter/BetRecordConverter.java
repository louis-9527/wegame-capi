package com.wegame.capi.biz.converter;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.MariaMakilingBetRecordDetailDo;
import com.wegame.capi.client.domain.request.query.PageQueryBetRecordRequest;
import com.wegame.capi.client.domain.vo.BetRecordVo;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.dao.mapper.domain.business.BetRecordPo;
import com.wegame.capi.dao.mapper.domain.business.BetRecordQuery;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;

/**
 * BetRecordConverter
 *
 * @author generate
 */
public class BetRecordConverter {

    public static BetRecordDo poToDo(BetRecordPo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        BetRecordDo betRecordDo = new BetRecordDo();
        betRecordDo.setId(po.getId());
        betRecordDo.setPId(po.getPId());
        betRecordDo.setChannelId(po.getChannelId());
        betRecordDo.setGameId(po.getGameId());
        betRecordDo.setUserId(po.getUserId());
        betRecordDo.setPlayerId(po.getPlayerId());
        betRecordDo.setPlayerName(po.getPlayerName());
        betRecordDo.setBetType(po.getBetType());
        betRecordDo.setBetSubType(po.getBetSubType());
        betRecordDo.setBetId(po.getBetId());
        betRecordDo.setBetSerialNo(po.getBetSerialNo());
        betRecordDo.setBetAmount(po.getBetAmount());
        betRecordDo.setPayAmount(po.getPayAmount());
        betRecordDo.setPayOutAmount(po.getPayOutAmount());
        betRecordDo.setMarketFlag(po.getMarketFlag());
        betRecordDo.setStatus(po.getStatus());
        betRecordDo.setCreator(po.getCreator());
        betRecordDo.setCreateTime(po.getCreateTime());
        betRecordDo.setUpdater(po.getUpdater());
        betRecordDo.setUpdateTime(po.getUpdateTime());
        return betRecordDo;
    }

    public static BetRecordPo doToPo(BetRecordDo betRecordDo, OptEnum optEnum) {
        if (Objects.isNull(betRecordDo)) {
            return null;
        }
        BetRecordPo po = new BetRecordPo();
        po.setId(betRecordDo.getId());
        po.setPId(betRecordDo.getPId());
        po.setChannelId(betRecordDo.getChannelId());
        po.setGameId(betRecordDo.getGameId());
        po.setUserId(betRecordDo.getUserId());
        po.setPlayerId(betRecordDo.getPlayerId());
        po.setPlayerName(betRecordDo.getPlayerName());
        po.setBetType(betRecordDo.getBetType());
        po.setBetSubType(betRecordDo.getBetSubType());
        po.setBetId(betRecordDo.getBetId());
        po.setBetSerialNo(betRecordDo.getBetSerialNo());
        po.setBetAmount(betRecordDo.getBetAmount());
        po.setPayAmount(betRecordDo.getPayAmount());
        po.setPayOutAmount(betRecordDo.getPayOutAmount());
        po.setMarketFlag(betRecordDo.getMarketFlag());
        po.setStatus(betRecordDo.getStatus());
        if (OptEnum.ADD.equals(optEnum)) {
            long currentMilli = DateUtil.getCurrentMilli();
            po.setCreator(betRecordDo.getCreator());
            po.setCreateTime(betRecordDo.getCreateTime() == null ? currentMilli : betRecordDo.getCreateTime());
            po.setUpdater(betRecordDo.getUpdater());
            po.setUpdateTime(betRecordDo.getUpdateTime() == null ? currentMilli : betRecordDo.getUpdateTime());
        } else {
            po.setUpdater(betRecordDo.getUpdater());
            po.setUpdateTime(betRecordDo.getUpdateTime() == null ? DateUtil.getCurrentMilli() : betRecordDo.getUpdateTime());
            po.setId(betRecordDo.getId());
        }
        return po;
    }

    public static BetRecordVo doToVo(BetRecordDo betRecordDo) {
        if (Objects.isNull(betRecordDo)) {
            return null;
        }
        BetRecordVo vo = new BetRecordVo();
        vo.setId(Objects.nonNull(betRecordDo.getId()) ? String.valueOf(betRecordDo.getId()) : null);
        vo.setUserId(Objects.nonNull(betRecordDo.getUserId()) ? String.valueOf(betRecordDo.getUserId()) : null);
        vo.setPlayerName(betRecordDo.getPlayerName());
        vo.setBetAmount(betRecordDo.getBetAmount());
        vo.setPayAmount(betRecordDo.getPayAmount());
        vo.setPayOutAmount(betRecordDo.getPayOutAmount());
        vo.setStatus(betRecordDo.getStatus());
        vo.setCreateTime(betRecordDo.getCreateTime());
        return vo;
    }

    public static List<BetRecordDo> poToDoList(List<BetRecordPo> poList) {
        return Optional.ofNullable(poList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BetRecordConverter::poToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<BetRecordPo> doToPoList(List<BetRecordDo> doList, OptEnum optEnum) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> doToPo(item, optEnum))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<BetRecordVo> doToVoList(List<BetRecordDo> doList) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(record -> BetRecordConverter.doToVo(record))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static BetRecordQuery requestToQuery(PageQueryBetRecordRequest request, String orderClause) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        BetRecordQuery query = new BetRecordQuery();
        query.setId(Optional.ofNullable(request.getId()).map(Long::valueOf).orElse(null));
        query.setPlayerId(userInfo.getPlayerId());
        query.setStartCreateTime(request.getStartCreateTime());
        query.setEndCreateTime(request.getEndCreateTime());
        query.setOrderByClause(orderClause);
        query.setLimit(request.getPageSize());
        query.setLastId(request.getLastId());
        query.setDirection(Sort.Direction.DESC);
        return query;
    }

    ///////////////  above auto generated ///////////////

}