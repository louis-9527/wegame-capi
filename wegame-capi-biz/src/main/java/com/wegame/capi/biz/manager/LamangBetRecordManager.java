package com.wegame.capi.biz.manager;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.BetRecordConverter;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.LamangGameRecordDo;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigRecordDo;
import com.wegame.capi.biz.service.GameRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.domain.vo.BetRecordVo;
import com.wegame.capi.client.domain.vo.BetResultVo;
import com.wegame.capi.client.domain.vo.ReplayBetResultVo;
import com.wegame.capi.client.domain.vo.lamang.LamangBetResultVo;
import com.wegame.capi.client.domain.vo.lamang.LamangGameRecordVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.repository.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.config.mongo.LamangMongoConfig.lamangTransactionManager;

@Slf4j
@Component("lamangBetRecordManager")
public class LamangBetRecordManager extends AbstractBetRecordManager<LamangGameRecordDo> {

    @Autowired
    GameRecordService<LamangGameRecordDo> gameRecordService;

    @Transactional(transactionManager = lamangTransactionManager)
    public Long add(BetRecordDo<LamangGameRecordDo> betRecord, GameTypeEnum gameType) {
        log.info("add: {}", betRecord.getId());
        Long id = betRecordService.add(betRecord, gameType);
        AssertUtil.assertNotNull(id, ErrorCode.ERR_INTER);
        List<LamangGameRecordDo> gameRecordList = betRecord.getGameRecordList();
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
            //            gameRecordList.forEach(item -> {
            //                log.info("add GameRecord id:{}", item.getId());
            //            });
            boolean ret = gameRecordService.batchAdd(gameRecordList, gameType, LamangGameRecordDo.class);
            AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        }
        return betRecord.getId();
    }

    @Transactional(transactionManager = lamangTransactionManager)
    @Override
    public boolean batchAdd(List<BetRecordDo<LamangGameRecordDo>> betRecordList, GameTypeEnum gameType) {
        AssertUtil.assertNotEmpty(betRecordList, ErrorCode.ERR_PARAM);
        for (int i = 0; i < betRecordList.size(); i++) {
            log.info("batchAdd index: {} betRecordId:{}", i, betRecordList.get(i).getId());
        }
        List<BetRecordDo> betRecordDos = betRecordList.stream().collect(Collectors.toList());
        boolean ret = betRecordService.batchAdd(betRecordDos, gameType);
        AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        List<LamangGameRecordDo> gameRecordList = betRecordList.stream()
                .map(BetRecordDo::getGameRecordList)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
            gameRecordList.forEach(item -> {
                log.info("add GameRecord id:{}", item.getId());
            });
            boolean batchAddRet = gameRecordService.batchAdd(gameRecordList, gameType, LamangGameRecordDo.class);
            AssertUtil.assertTrue(batchAddRet, ErrorCode.ERR_INTER);
        }
        return true;
    }

    @Override
    public List<LamangGameRecordDo> listGameRecord(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.list(query, gameType, LamangGameRecordDo.class);
    }

    @Override
    @Cacheable(value = CacheConstants.BET_RECORD, key = "#gameType.name + '_' + #query.playerId + '_'  + #query.id")
    public List<LamangGameRecordDo> listGameRecordWithCache(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.list(query, gameType, LamangGameRecordDo.class);
    }

    @Override
    public List<LamangGameRecordVo> listGameRecordVo(Long betRecordId, GameTypeEnum gameType) {
        List<LamangGameRecordDo> list = gameRecordService.list(GameRecordQuery.builder()
                .betRecordId(betRecordId)
                .orderByClause("_id")
                .direction(Sort.Direction.ASC)
                .build(), gameType, LamangGameRecordDo.class);
        return Optional.ofNullable(list).orElse(Collections.emptyList())
                .stream()
                .map(LamangGameRecordDo::toRecordVo)
                .collect(Collectors.toList());
    }

    @Override
    public Long countGameRecord(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.count(query, gameType);
    }

    @Override
    public void putGameRecordCache(List<LamangGameRecordDo> gameRecordList, GameTypeEnum gameType, String cacheKey) {
        // 异步设置游戏记录缓存
        gameRecordService.asyncPutCache(gameRecordList, gameType, cacheKey);
    }

    @Override
    public ReplayBetResultVo getReplayResult(UserInfo userInfo, Long betRecordId, GameTypeEnum gameType) {
        Map<String, BetResultVo> resultMap = new HashMap<>();
        //BetRecordDo betRecordDo = betRecordService.get(betRecordId, userInfo.getPlayerId(), gameType);
        List<LamangGameRecordDo> gameRecordList = listGameRecord(GameRecordQuery.builder()
                .betRecordId(Long.valueOf(betRecordId))
                .playerId(userInfo.getPlayerId())
                .build(), gameType);
        AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_PARAM);
        gameRecordList = gameRecordList.stream()
                .sorted(Comparator.comparing(LamangGameRecordDo::getBatchId).thenComparing(LamangGameRecordDo::getIndex)) // 第二排序（次，仅主字段相同时生效）
                .collect(Collectors.toList());
        LamangGameRecordDo firstGameRecord = gameRecordList.get(0);
        Map<Long, List<LamangGameRecordDo>> groupByBatch = gameRecordList.stream()
                .collect(Collectors.groupingBy(LamangGameRecordDo::getBatchId));
        for (Map.Entry<Long, List<LamangGameRecordDo>> entry : groupByBatch.entrySet()) {
            List<LamangBetResultVo> resultList = entry.getValue().stream().map(ToVoConverter::toResultVo).collect(Collectors.toList());
            BetResultVo vo = BetResultVo.builder()
                    .betRecordId(String.valueOf(betRecordId))
                    //.gameId(betRecordDo.getGameId())
                    //.betType(betRecordDo.getBetType())
                    //.betSubType(betRecordDo.getBetSubType())
                    .credit(null)
                    .overallRewardAmount(resultList.stream().filter(Objects::nonNull).mapToLong(item -> item.getTotalRewardAmount()).sum())
                    //.totalPayoutAmount(betRecordDo.getPayOutAmount())
                    .resultDetail(resultList)
                    .build();
            resultMap.put(String.valueOf(entry.getKey()), vo);
        }
        return ReplayBetResultVo.builder()
                .firstBatchId(String.valueOf(firstGameRecord.getBatchId()))
                .betResultMap(resultMap)
                .build();
    }
}
