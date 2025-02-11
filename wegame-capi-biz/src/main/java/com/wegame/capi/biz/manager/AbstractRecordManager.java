package com.wegame.capi.biz.manager;

import com.wegame.base.CacheConstants;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.updateBetRecordThreadPoolExecutor;

@Component
public abstract class AbstractRecordManager<T extends ToVoConverter> {
    @Autowired
    BetRecordService betRecordService;

    public abstract Long add(BetRecordDo<T> betRecord, GameTypeEnum gameType);

    public abstract boolean batchAdd(List<BetRecordDo<T>> betRecordList, GameTypeEnum gameType);

    public Long addBetRecord(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        return betRecordService.add(betRecordDo, gameType);
    }

    @Async(updateBetRecordThreadPoolExecutor)
    @Cacheable(value = CacheConstants.BET_RECORD, key = "#gameType.name + '_' + #betRecordDo.playerId + '_'  + #betRecordDo.id")
    public void asyncUpdateBetRecord(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        betRecordService.update(betRecordDo, gameType);
    }

    public abstract List<T> listGameRecord(GameRecordQuery query, GameTypeEnum gameType);

    public abstract List<T> listGameRecordWithCache(GameRecordQuery query, GameTypeEnum gameType);

    public abstract List<?> listGameRecordVo(GameRecordQuery query, GameTypeEnum gameType);

}
