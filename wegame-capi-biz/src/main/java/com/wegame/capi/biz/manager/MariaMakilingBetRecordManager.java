package com.wegame.capi.biz.manager;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.service.GameRecordService;
import com.wegame.capi.client.domain.vo.mariamaking.MariaMakilingGameRecordVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.repository.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.wegame.capi.infrastructure.config.MongodbTransactionConfig.mariamakilingTransactionManager;

@Slf4j
@Component("mariaMakilingBetRecordManager")
public class MariaMakilingBetRecordManager extends AbstractBetRecordManager<MariaMakilingGameRecordDo> {

    @Autowired
    GameRecordService<MariaMakilingGameRecordDo> gameRecordService;

    @Override
    @Transactional(transactionManager = mariamakilingTransactionManager, rollbackFor = Exception.class)
    public Long add(BetRecordDo<MariaMakilingGameRecordDo> betRecord, GameTypeEnum gameType) {
        try {
            AssertUtil.assertNotEmpty(betRecord.getGameRecordList(), ErrorCode.ERR_PARAM);
            betRecordService.add(betRecord, gameType);
        } catch (DuplicateKeyException e) {
            log.warn("add Duplicate key, betRecordId: {}", betRecord.getId());
            throw e;
        }
        boolean ret = gameRecordService.batchAdd(betRecord.getGameRecordList(), gameType, MariaMakilingGameRecordDo.class);
        AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        // 异步设置游戏记录缓存
        //        String cacheKey = String.format("%s_%s", betRecord.getId(), betRecord.getPlayerId());
        //        gameRecordService.asyncPutCache(betRecord.getGameRecordList(), gameType, cacheKey);
        return betRecord.getId();
    }

    @Override
    @Transactional(transactionManager = mariamakilingTransactionManager, rollbackFor = Exception.class)
    public boolean batchAdd(List<BetRecordDo<MariaMakilingGameRecordDo>> betRecordList, GameTypeEnum gameType) {
        AssertUtil.assertNotEmpty(betRecordList, ErrorCode.ERR_PARAM);
        List<BetRecordDo> betRecordDos = betRecordList.stream().collect(Collectors.toList());
        boolean ret = betRecordService.batchAdd(betRecordDos, gameType);
        AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        List<MariaMakilingGameRecordDo> gameRecordList = betRecordList.stream()
                .filter(item -> CollectionUtils.isNotEmpty(item.getGameRecordList()))
                .map(BetRecordDo::getGameRecordList)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
            boolean insertRet = gameRecordService.batchAdd(gameRecordList, gameType, MariaMakilingGameRecordDo.class);
            AssertUtil.assertTrue(insertRet, ErrorCode.ERR_INTER);
        }
        return true;
    }

    @Override
    public List<MariaMakilingGameRecordDo> listGameRecord(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.list(query, gameType, MariaMakilingGameRecordDo.class);
    }

    @Override
    @Cacheable(value = CacheConstants.GAME_RECORD, key = "#gameType.name + '_' + #query.getCacheKey()")
    public List<MariaMakilingGameRecordDo> listGameRecordWithCache(GameRecordQuery query, GameTypeEnum gameType) {
        //        AssertUtil.assertStrNotEmpty(query.getCacheKey(), ErrorCode.ERR_PARAM);
        //        List<MariaMakilingGameRecordDo> recordDos = gameRecordService.list(query, gameType, MariaMakilingGameRecordDo.class);
        //        if (CollectionUtils.isNotEmpty(recordDos)) {
        //            log.warn("list game record ret is empty, then query from primary, {}, {}", gameType, query);
        //            query.setFromPrimary(true);
        //            recordDos = gameRecordService.list(query, gameType, MariaMakilingGameRecordDo.class);
        //        }
        log.info("list game record from db");
        return gameRecordService.list(query, gameType, MariaMakilingGameRecordDo.class);
    }

    @Override
    public List<MariaMakilingGameRecordVo> listGameRecordVo(Long betRecordId, GameTypeEnum gameType) {
        List<MariaMakilingGameRecordDo> list = gameRecordService.list(GameRecordQuery.builder()
                        .betRecordId(betRecordId)
                        .orderByClause("_id")
                        .direction(Sort.Direction.ASC)
                        .build(),
                gameType,
                MariaMakilingGameRecordDo.class);
        return Optional.ofNullable(list)
                .orElse(Collections.emptyList())
                .stream()
                .map(MariaMakilingGameRecordDo::toRecordVo)
                .collect(Collectors.toList());
    }

    @Override
    public Long countGameRecord(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.count(query, gameType);
    }

    @Override
    public void putGameRecordCache(List<MariaMakilingGameRecordDo> gameRecordList, GameTypeEnum gameType, String cacheKey) {
        // 异步设置游戏记录缓存
        gameRecordService.asyncPutCache(gameRecordList, gameType, cacheKey);
    }
}
