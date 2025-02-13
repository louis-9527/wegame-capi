package com.wegame.capi.biz.manager;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.service.GameRecordService;
import com.wegame.capi.client.domain.vo.mariamaking.MariaMakilingGameRecordVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component("mariaMakilingRecordManager")
public class MariaMakilingRecordManager extends AbstractRecordManager<MariaMakilingGameRecordDo> {

    @Autowired
    GameRecordService<MariaMakilingGameRecordDo> gameRecordService;

    @Override
    @Transactional(transactionManager = "mariamakilingTransactionManager")
    public Long add(BetRecordDo<MariaMakilingGameRecordDo> betRecord, GameTypeEnum gameType) {
        AssertUtil.assertNotEmpty(betRecord.getGameRecordList(), ErrorCode.ERR_PARAM);
        Long betRecordId = betRecordService.add(betRecord, gameType);
        AssertUtil.assertNotNull(betRecordId, ErrorCode.ERR_INTER);
        boolean ret = gameRecordService.batchInsert(betRecord.getGameRecordList(), gameType, MariaMakilingGameRecordDo.class);
        AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        return betRecord.getId();
    }

    @Override
    @Transactional(transactionManager = "mariamakilingTransactionManager")
    public boolean batchAdd(List<BetRecordDo<MariaMakilingGameRecordDo>> betRecordList, GameTypeEnum gameType) {
        AssertUtil.assertNotEmpty(betRecordList, ErrorCode.ERR_PARAM);
        List<BetRecordDo> betRecordDos = betRecordList.stream().collect(Collectors.toList());
        boolean ret = betRecordService.batchInsert(betRecordDos, gameType);
        AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        List<MariaMakilingGameRecordDo> gameRecordList = betRecordList.stream()
                .filter(item -> CollectionUtils.isNotEmpty(item.getGameRecordList()))
                .map(BetRecordDo::getGameRecordList)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
            gameRecordList.forEach(item -> {
                log.info("addGameRecord id:{}", item.getId());
            });
            boolean insertRet = gameRecordService.batchInsert(gameRecordList, gameType, MariaMakilingGameRecordDo.class);
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
        AssertUtil.assertStrNotEmpty(query.getCacheKey(), ErrorCode.ERR_PARAM);
        return gameRecordService.list(query, gameType, MariaMakilingGameRecordDo.class);
    }

    @Override
    public List<MariaMakilingGameRecordVo> listGameRecordVo(GameRecordQuery query, GameTypeEnum gameType) {
        List<MariaMakilingGameRecordDo> list = gameRecordService.list(query, gameType, MariaMakilingGameRecordDo.class);
        return Optional.ofNullable(list).orElse(Collections.emptyList())
                .stream()
                .map(MariaMakilingGameRecordDo::toRecordVo)
                .collect(Collectors.toList());
    }
}
