package com.wegame.capi.biz.manager;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.domain.vo.BetResultVo;
import com.wegame.capi.client.domain.vo.ReplayBetResultVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.repository.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.wegame.capi.common.Constants.BUILD_BEAN_FORMAT;
import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.betRecordThreadPoolExecutor;
import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.gameRecordPutCacheThreadPoolExecutor;

@Slf4j
@Component
public abstract class AbstractBetRecordManager<T extends ToVoConverter> implements InitializingBean {
    @Autowired
    BetRecordService betRecordService;

    public abstract Long add(BetRecordDo<T> betRecord, GameTypeEnum gameType);

    public abstract boolean batchAdd(List<BetRecordDo<T>> betRecordList, GameTypeEnum gameType);

    @Override
    public void afterPropertiesSet() {
        log.info(BUILD_BEAN_FORMAT, this.getClass().getSimpleName());
    }

    @Async(betRecordThreadPoolExecutor)
    // @CacheEvict(value = CacheConstants.BET_RECORD, key = "#gameType.name + '_' + #betRecordDo.playerId + '_'  + #betRecordDo.id")
    public void asyncUpdateBetRecord(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        betRecordService.update(betRecordDo, gameType);
    }

    public BetRecordDo getBetRecord(Long betRecordId, GameTypeEnum gameType) {
        return betRecordService.get(betRecordId, gameType);
    }

    public BetRecordDo getBetRecordByIdFromPrimary(Long betRecordId, GameTypeEnum gameType) {
        return betRecordService.getById(betRecordId, gameType, true);
    }

    public abstract List<T> listGameRecord(GameRecordQuery query, GameTypeEnum gameType);

    public abstract List<T> listGameRecordWithCache(GameRecordQuery query, GameTypeEnum gameType);

    public abstract List<?> listGameRecordVo(Long betRecordId, GameTypeEnum gameType);

    public abstract Long countGameRecord(GameRecordQuery query, GameTypeEnum gameType);

    public abstract void putGameRecordCache(List<T> gameRecordList, GameTypeEnum gameType, String cacheKey);

    @Async(gameRecordPutCacheThreadPoolExecutor)
    @CacheEvict(value = CacheConstants.GAME_RECORD, key = "#gameType.name + '_' + #query.getCacheKey()")
    public void asyncEvictGameRecordCache(GameRecordQuery query, GameTypeEnum gameType) {
    }

    public ReplayBetResultVo getReplayResult(UserInfo userInfo, Long betRecordId, GameTypeEnum gameType) {
        throw new BusinessException(ErrorCode.ERR_INTER);
    }

}
