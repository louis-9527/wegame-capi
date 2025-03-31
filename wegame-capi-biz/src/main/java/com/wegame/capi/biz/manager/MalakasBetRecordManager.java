package com.wegame.capi.biz.manager;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.service.GameRecordService;
import com.wegame.capi.client.domain.vo.malakas.MalakasGameRecordVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
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

import static com.wegame.capi.infrastructure.config.MongodbTransactionConfig.malakasTransactionManager;

@Slf4j
@Component("malakasBetRecordManager")
public class MalakasBetRecordManager extends AbstractBetRecordManager<MalakasGameRecordDo> {

    @Autowired
    GameRecordService<MalakasGameRecordDo> gameRecordService;

    @Transactional(transactionManager = malakasTransactionManager)
    public Long add(BetRecordDo<MalakasGameRecordDo> betRecord, GameTypeEnum gameType) {
        // todo delete @maidong
        log.info("add: {}", betRecord.getId());
        Long id = betRecordService.add(betRecord, gameType);
        AssertUtil.assertNotNull(id, ErrorCode.ERR_INTER);
        List<MalakasGameRecordDo> gameRecordList = betRecord.getGameRecordList();
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
            gameRecordList.forEach(item -> {
                log.info("add GameRecord id:{}", item.getId());
            });
            boolean ret = gameRecordService.batchAdd(gameRecordList, gameType, MalakasGameRecordDo.class);
            AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        }
        return betRecord.getId();
    }

    @Transactional(transactionManager = malakasTransactionManager)
    @Override
    public boolean batchAdd(List<BetRecordDo<MalakasGameRecordDo>> betRecordList, GameTypeEnum gameType) {
        AssertUtil.assertNotEmpty(betRecordList, ErrorCode.ERR_PARAM);
        // todo delete @maidong
        for (int i = 0; i < betRecordList.size(); i++) {
            log.info("batchAdd index: {} betRecordId:{}", i, betRecordList.get(0).getId());
        }
        List<BetRecordDo> betRecordDos = betRecordList.stream().collect(Collectors.toList());
        boolean ret = betRecordService.batchAdd(betRecordDos, gameType);
        AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        List<MalakasGameRecordDo> gameRecordList = betRecordList.stream()
                .map(BetRecordDo::getGameRecordList)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
            // todo delete log
            gameRecordList.forEach(item -> {
                log.info("batchAdd GameRecord id:{}", item.getId());
            });
            boolean batchAddRet = gameRecordService.batchAdd(gameRecordList, gameType, MalakasGameRecordDo.class);
            AssertUtil.assertTrue(batchAddRet, ErrorCode.ERR_INTER);
        }
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
    public List<MalakasGameRecordVo> listGameRecordVo(Long betRecordId, GameTypeEnum gameType) {
        List<MalakasGameRecordDo> list = gameRecordService.list(GameRecordQuery.builder()
                .betRecordId(betRecordId)
                .orderByClause("_id")
                .direction(Sort.Direction.ASC)
                .build(), gameType, MalakasGameRecordDo.class);
        return Optional.ofNullable(list).orElse(Collections.emptyList())
                .stream()
                .map(MalakasGameRecordDo::toRecordVo)
                .collect(Collectors.toList());
    }

    @Override
    public Long countGameRecord(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.count(query, gameType);
    }
}
