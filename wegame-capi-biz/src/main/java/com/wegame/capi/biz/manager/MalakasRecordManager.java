package com.wegame.capi.biz.manager;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.service.GameRecordService;
import com.wegame.capi.client.domain.vo.BetRecordVo;
import com.wegame.capi.client.domain.vo.malakas.MalakasGameRecordVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("malakasRecordManager")
public class MalakasRecordManager extends AbstractRecordManager<MalakasGameRecordDo> {

    @Autowired
    GameRecordService<MalakasGameRecordDo> gameRecordService;

//    @Transactional(transactionManager = "mariamakilingTransactionManager") todo fix
    public Long add(BetRecordDo<MalakasGameRecordDo> betRecord, GameTypeEnum gameType) {
        Long id = betRecordService.add(betRecord, gameType);
        AssertUtil.assertNotNull(id, ErrorCode.ERR_INTER);
        List<MalakasGameRecordDo> gameRecordList = betRecord.getGameRecordList();
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
            boolean ret = gameRecordService.batchInsert(gameRecordList, gameType);
            AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        }
        return betRecord.getId();
    }

//    @Transactional(transactionManager = "mariamakilingTransactionManager")  todo fix
    @Override
    public boolean batchAdd(List<BetRecordDo<MalakasGameRecordDo>> betRecordList, GameTypeEnum gameType) {
        AssertUtil.assertNotEmpty(betRecordList, ErrorCode.ERR_PARAM);
        List<MalakasGameRecordDo> gameRecordList = betRecordList.stream()
                .map(BetRecordDo::getGameRecordList).flatMap(List::stream)
                .collect(Collectors.toList());
        gameRecordService.batchInsert(gameRecordList, gameType);
        List<BetRecordDo> recordList = betRecordList.stream()
                .map(item -> {
                    item.setGameRecordList(null);
                    return item;
                })
                .collect(Collectors.toList());
        betRecordService.batchInsert(recordList, gameType);
        return true;
    }

    @Override
    public List<MalakasGameRecordDo> listGameRecord(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.list(query, gameType, MalakasGameRecordDo.class);
    }

    @Override
    @Cacheable(value = CacheConstants.BET_RECORD, key = "#gameType.name + '_' + #query.playerId + '_'  + #query.id")
    public List<MalakasGameRecordDo> listGameRecordWithCache(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.list(query, gameType, MalakasGameRecordDo.class);
    }

    @Override
    public List<MalakasGameRecordVo> listGameRecordVo(GameRecordQuery query, GameTypeEnum gameType) {
        List<MalakasGameRecordDo> list = gameRecordService.list(query, gameType, MalakasGameRecordDo.class);
        return Optional.ofNullable(list).orElse(Collections.emptyList())
                .stream()
                .map(MalakasGameRecordDo::toRecordVo)
                .collect(Collectors.toList());
    }
}
