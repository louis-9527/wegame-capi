package com.wegame.capi.biz.manager;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.service.GameRecordService;
import com.wegame.capi.client.domain.vo.BetRecordVo;
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

@Component("mariaMakilingRecordManager")
public class MariaMakilingRecordManager extends AbstractRecordManager<MariaMakilingGameRecordDo> {

    @Autowired
    GameRecordService<MariaMakilingGameRecordDo> gameRecordService;

    @Transactional(transactionManager = "mariamakilingTransactionManager")
    public Long add(BetRecordDo<MariaMakilingGameRecordDo> betRecord, GameTypeEnum gameType) {
        Long id = betRecordService.add(betRecord, gameType);
        AssertUtil.assertNotNull(id, ErrorCode.ERR_INTER);
        List<MariaMakilingGameRecordDo> gameRecordList = betRecord.getGameRecordList();
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
            boolean ret = gameRecordService.batchInsert(gameRecordList, gameType);
            AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        }
        return betRecord.getId();
    }

    @Transactional(transactionManager = "mariamakilingTransactionManager")
    @Override
    public boolean batchAdd(List<BetRecordDo<MariaMakilingGameRecordDo>> betRecordList, GameTypeEnum gameType) {
        AssertUtil.assertNotEmpty(betRecordList, ErrorCode.ERR_PARAM);
        List<MariaMakilingGameRecordDo> gameRecordList = betRecordList.stream()
                .map(BetRecordDo::getGameRecordList).flatMap(List::stream)
                .collect(Collectors.toList());
        gameRecordService.batchInsert(gameRecordList, gameType);
        List<BetRecordDo> recordList = betRecordList.stream()
                .map(item -> {item.setGameRecordList(null); return item; })
                .collect(Collectors.toList());
        betRecordService.batchInsert(recordList, gameType);
        return true;
    }

    @Override
    public List<MariaMakilingGameRecordDo> listGameRecord(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.list(query, gameType, MariaMakilingGameRecordDo.class);
    }

    @Override
    @Cacheable(value = CacheConstants.GAME_RECORD, key = "#gameType.name + '_' + #query.playerId + '_'  + #query.id")
    public List<MariaMakilingGameRecordDo> listGameRecordWithCache(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.list(query, gameType, MariaMakilingGameRecordDo.class);
    }

    @Override
    public List<BetRecordVo.MariaMakilingGameRecordVo> listGameRecordVo(GameRecordQuery query, GameTypeEnum gameType) {
        List<MariaMakilingGameRecordDo> list = gameRecordService.list(query, gameType, MariaMakilingGameRecordDo.class);
        return Optional.ofNullable(list).orElse(Collections.emptyList())
                .stream()
                .map(MariaMakilingGameRecordDo::toRecordVo)
                .collect(Collectors.toList());
    }
}
