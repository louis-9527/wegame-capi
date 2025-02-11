package com.wegame.capi.biz.manager;

import com.wegame.base.CacheConstants;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.biz.service.GameRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.updateBetRecordThreadPoolExecutor;

@Component
public abstract class AbstractRecordManager<T> {
    @Autowired
    BetRecordService betRecordService;

    public abstract Long add(BetRecordDo betRecord, List<T> gameRecordList, GameTypeEnum gameType);

    public Long addBetRecord(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        return betRecordService.add(betRecordDo, gameType);
    }

    @Async(updateBetRecordThreadPoolExecutor)
    @CacheEvict(value = CacheConstants.BET_RECORD, key = "#gameType.collectionName +'-'+ #betRecordDo.id")
    public void asyncUpdateBetRecord(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        betRecordService.update(betRecordDo, gameType);
    }

    public abstract List<T> listGameRecord(GameRecordQuery query, GameTypeEnum gameType);
}
