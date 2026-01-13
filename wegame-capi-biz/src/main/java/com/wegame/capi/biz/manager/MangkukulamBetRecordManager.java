package com.wegame.capi.biz.manager;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.service.GameRecordService;
import com.wegame.capi.client.domain.vo.mangkukulam.MangkukulamGameRecordVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.repository.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.config.mongo.MangkukulamMongoConfig.mangkukulamTransactionManager;

@Slf4j
@Component("mangkukulamBetRecordManager")
public class MangkukulamBetRecordManager extends AbstractBetRecordManager<MangkukulamGameRecordDo> {

    @Autowired
    GameRecordService<MangkukulamGameRecordDo> gameRecordService;

    @Transactional(transactionManager = mangkukulamTransactionManager)
    public Long add(BetRecordDo<MangkukulamGameRecordDo> betRecord, GameTypeEnum gameType) {
        log.info("add: {}", betRecord.getId());
        Long id = betRecordService.add(betRecord, gameType);
        AssertUtil.assertNotNull(id, ErrorCode.ERR_INTER);
        List<MangkukulamGameRecordDo> gameRecordList = betRecord.getGameRecordList();
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
//            gameRecordList.forEach(item -> {
//                log.info("add GameRecord id:{}", item.getId());
//            });
            boolean ret = gameRecordService.batchAdd(gameRecordList, gameType, MangkukulamGameRecordDo.class);
            AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        }
        return betRecord.getId();
    }

    @Transactional(transactionManager = mangkukulamTransactionManager)
    @Override
    public boolean batchAdd(List<BetRecordDo<MangkukulamGameRecordDo>> betRecordList, GameTypeEnum gameType) {
        AssertUtil.assertNotEmpty(betRecordList, ErrorCode.ERR_PARAM);
        for (int i = 0; i < betRecordList.size(); i++) {
            log.info("batchAdd index: {} betRecordId:{}", i, betRecordList.get(i).getId());
        }
        List<BetRecordDo> betRecordDos = betRecordList.stream().collect(Collectors.toList());
        boolean ret = betRecordService.batchAdd(betRecordDos, gameType);
        AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        List<MangkukulamGameRecordDo> gameRecordList = betRecordList.stream()
                .map(BetRecordDo::getGameRecordList)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
            gameRecordList.forEach(item -> {
                log.info("add GameRecord id:{}", item.getId());
            });
            boolean batchAddRet = gameRecordService.batchAdd(gameRecordList, gameType, MangkukulamGameRecordDo.class);
            AssertUtil.assertTrue(batchAddRet, ErrorCode.ERR_INTER);
        }
        return true;
    }

    @Override
    public List<MangkukulamGameRecordDo> listGameRecord(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.list(query, gameType, MangkukulamGameRecordDo.class);
    }

    @Override
    @Cacheable(value = CacheConstants.BET_RECORD, key = "#gameType.name + '_' + #query.playerId + '_'  + #query.id")
    public List<MangkukulamGameRecordDo> listGameRecordWithCache(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.list(query, gameType, MangkukulamGameRecordDo.class);
    }

    @Override
    public List<MangkukulamGameRecordVo> listGameRecordVo(Long betRecordId, GameTypeEnum gameType) {
        List<MangkukulamGameRecordDo> list = gameRecordService.list(GameRecordQuery.builder()
                .betRecordId(betRecordId)
                .orderByClause("_id")
                .direction(Sort.Direction.ASC)
                .build(), gameType, MangkukulamGameRecordDo.class);
        return Optional.ofNullable(list).orElse(Collections.emptyList())
                .stream()
                .map(MangkukulamGameRecordDo::toRecordVo)
                .collect(Collectors.toList());
    }

    @Override
    public Long countGameRecord(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.count(query, gameType);
    }

    @Override
    public void putGameRecordCache(List<MangkukulamGameRecordDo> gameRecordList, GameTypeEnum gameType, String cacheKey) {
        // 异步设置游戏记录缓存
        gameRecordService.asyncPutCache(gameRecordList, gameType, cacheKey);
    }
}
