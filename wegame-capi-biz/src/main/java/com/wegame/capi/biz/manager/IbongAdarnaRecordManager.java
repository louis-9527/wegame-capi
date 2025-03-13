package com.wegame.capi.biz.manager;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.IbongAdarnaGameRecordDo;
import com.wegame.capi.biz.service.GameRecordService;
import com.wegame.capi.client.domain.vo.ibongadarna.IbongAdarnaGameRecordVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
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

import static com.wegame.capi.infrastructure.config.MongodbTransactionConfig.ibongAdarnaTransactionManager;
import static com.wegame.capi.infrastructure.config.MongodbTransactionConfig.mariamakilingTransactionManager;

@Slf4j
@Component("ibongAdarnaRecordManager")
public class IbongAdarnaRecordManager extends AbstractRecordManager<IbongAdarnaGameRecordDo> {

    @Autowired
    GameRecordService<IbongAdarnaGameRecordDo> gameRecordService;

    @Override
    @Transactional(transactionManager = ibongAdarnaTransactionManager)
    public Long add(BetRecordDo<IbongAdarnaGameRecordDo> betRecord, GameTypeEnum gameType) {
        try {
            AssertUtil.assertNotEmpty(betRecord.getGameRecordList(), ErrorCode.ERR_PARAM);
            betRecordService.add(betRecord, gameType);
        } catch (DuplicateKeyException e) {
            log.warn("add Duplicate key, betRecordId: {}", betRecord.getId(), e);
            return betRecord.getId();
        }
        boolean ret = gameRecordService.batchAdd(betRecord.getGameRecordList(), gameType, IbongAdarnaGameRecordDo.class);
        AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        return betRecord.getId();
    }

    @Override
    @Transactional(transactionManager = ibongAdarnaTransactionManager)
    public boolean batchAdd(List<BetRecordDo<IbongAdarnaGameRecordDo>> betRecordList, GameTypeEnum gameType) {
        AssertUtil.assertNotEmpty(betRecordList, ErrorCode.ERR_PARAM);
        List<BetRecordDo> betRecordDos = betRecordList.stream().collect(Collectors.toList());
        boolean ret = betRecordService.batchAdd(betRecordDos, gameType);
        AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        List<IbongAdarnaGameRecordDo> gameRecordList = betRecordList.stream()
                .filter(item -> CollectionUtils.isNotEmpty(item.getGameRecordList()))
                .map(BetRecordDo::getGameRecordList)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
            // todo delete log
            gameRecordList.forEach(item -> {
                log.info("addGameRecord id:{}", item.getId());
            });
            boolean insertRet = gameRecordService.batchAdd(gameRecordList, gameType, IbongAdarnaGameRecordDo.class);
            AssertUtil.assertTrue(insertRet, ErrorCode.ERR_INTER);
        }
        return true;
    }

    @Override
    public List<IbongAdarnaGameRecordDo> listGameRecord(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.list(query, gameType, IbongAdarnaGameRecordDo.class);
    }

    @Override
    @Cacheable(value = CacheConstants.GAME_RECORD, key = "#gameType.name + '_' + #query.getCacheKey()")
    public List<IbongAdarnaGameRecordDo> listGameRecordWithCache(GameRecordQuery query, GameTypeEnum gameType) {
        AssertUtil.assertStrNotEmpty(query.getCacheKey(), ErrorCode.ERR_PARAM);
        return gameRecordService.list(query, gameType, IbongAdarnaGameRecordDo.class);
    }

    @Override
    public List<IbongAdarnaGameRecordVo> listGameRecordVo(Long betRecordId, GameTypeEnum gameType) {
        List<IbongAdarnaGameRecordDo> list = gameRecordService.list(GameRecordQuery.builder()
                .betRecordId(betRecordId)
                .orderByClause("_id")
                .direction(Sort.Direction.ASC)
                .build(), gameType, IbongAdarnaGameRecordDo.class);
        return Optional.ofNullable(list).orElse(Collections.emptyList())
                .stream()
                .map(IbongAdarnaGameRecordDo::toRecordVo)
                .collect(Collectors.toList());
    }

    @Override
    public Long countGameRecord(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.count(query, gameType);
    }
}
