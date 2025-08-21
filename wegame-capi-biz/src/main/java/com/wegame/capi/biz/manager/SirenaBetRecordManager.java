package com.wegame.capi.biz.manager;

import com.google.common.collect.Lists;
import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.mongo.KapreMongoConfig;
import com.wegame.capi.biz.config.mongo.SirenaMongoConfig;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.KapreGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.SirenaGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.SirenaSymbolDo;
import com.wegame.capi.biz.module.sirena.SirenaGameHelper;
import com.wegame.capi.biz.service.GameRecordService;
import com.wegame.capi.client.domain.vo.kapre.KapreGameRecordVo;
import com.wegame.capi.client.domain.vo.sirena.SirenaGameRecordVo;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.repository.domain.business.BetRecordQuery;
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


@Slf4j
@Component("sirenaBetRecordManager")
public class SirenaBetRecordManager extends AbstractBetRecordManager<SirenaGameRecordDo> {

    @Autowired
    GameRecordService<SirenaGameRecordDo> gameRecordService;

    @Transactional(transactionManager = SirenaMongoConfig.sirenaTransactionManager)
    public Long add(BetRecordDo<SirenaGameRecordDo> betRecord, GameTypeEnum gameType) {
        Long id = betRecordService.add(betRecord, gameType);
        AssertUtil.assertNotNull(id, ErrorCode.ERR_INTER);
        List<SirenaGameRecordDo> gameRecordList = betRecord.getGameRecordList();
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
            gameRecordList.forEach(item -> {
                log.info("add GameRecord id:{}", item.getId());
            });
            boolean ret = gameRecordService.batchAdd(gameRecordList, gameType, SirenaGameRecordDo.class);
            AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        }
        return betRecord.getId();
    }

    @Transactional(transactionManager = SirenaMongoConfig.sirenaTransactionManager)
    @Override
    public boolean batchAdd(List<BetRecordDo<SirenaGameRecordDo>> betRecordList, GameTypeEnum gameType) {
        AssertUtil.assertNotEmpty(betRecordList, ErrorCode.ERR_PARAM);
        List<BetRecordDo> betRecordDos = betRecordList.stream().collect(Collectors.toList());
        boolean ret = betRecordService.batchAdd(betRecordDos, gameType);
        AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        List<SirenaGameRecordDo> gameRecordList = betRecordList.stream()
                .map(BetRecordDo::getGameRecordList)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
            boolean batchAddRet = gameRecordService.batchAdd(gameRecordList, gameType, SirenaGameRecordDo.class);
            AssertUtil.assertTrue(batchAddRet, ErrorCode.ERR_INTER);
        }
        return true;
    }

    @Override
    public List<SirenaGameRecordDo> listGameRecord(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.list(query, gameType, SirenaGameRecordDo.class);
    }

    @Override
    @Cacheable(value = CacheConstants.BET_RECORD, key = "#gameType.name + '_' + #query.playerId + '_'  + #query.id")
    public List<SirenaGameRecordDo> listGameRecordWithCache(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.list(query, gameType, SirenaGameRecordDo.class);
    }

    @Override
    public List<SirenaGameRecordVo> listGameRecordVo(Long betRecordId, GameTypeEnum gameType) {
        List<SirenaGameRecordDo> list = gameRecordService.list(GameRecordQuery.builder()
                .betRecordId(betRecordId)
                .orderByClause("_id")
                .direction(Sort.Direction.ASC)
                .build(), gameType, SirenaGameRecordDo.class);
        return Optional.ofNullable(list).orElse(Collections.emptyList())
                .stream()
                .map(SirenaGameRecordDo::toRecordVo)
                .collect(Collectors.toList());
    }

    @Override
    public Long countGameRecord(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.count(query, gameType);
    }

    @Override
    public SirenaGameRecordDo findOneGameRecord(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.findOne(query, gameType, SirenaGameRecordDo.class);
    }

    public BetRecordDo findLastOneGameRecord(BetRecordQuery query, GameTypeEnum gameType) {
        BetRecordDo oneLastByTime = betRecordService.getOneLastByTime(query, gameType);
        if (oneLastByTime == null) {
            return null;
        }
        if (!BetSubTypeEnum.NORMAL_BET.getCode().equals(oneLastByTime.getBetSubType())) {
            return null;
        }
        if (oneLastByTime.getPayOutAmount().longValue() <= 0L) {
            return null;
        }
        // 查询对应的游戏记录
        Sort sort = Sort.by(Sort.Direction.ASC, "index");
        GameRecordQuery recordQuery = GameRecordQuery.builder().betRecordId(oneLastByTime.getId()).sort(sort).build();
        SirenaGameRecordDo gameRecordDo = gameRecordService.findOne(recordQuery, gameType, SirenaGameRecordDo.class);
        if (gameRecordDo == null) {
            return null;
        }
        // 判断是否为空,并且符合条件
        if (CollectionUtils.isEmpty(gameRecordDo.getRewardResult().getRewardLineDetailList())) {
            return null;
        }
        // 判断是否有珍珠
        boolean pearlWild = this.checkPearlWild(gameRecordDo.getRewardResult().getRewardLineDetailList());
        if (pearlWild) {
            oneLastByTime.setGameRecordList(Lists.newArrayList(gameRecordDo));
        }
        return oneLastByTime;
    }

    @Override
    public void putGameRecordCache(List<SirenaGameRecordDo> gameRecordList, GameTypeEnum gameType, String cacheKey) {
        // 异步设置游戏记录缓存
        gameRecordService.asyncPutCache(gameRecordList, gameType, cacheKey);
    }

    private boolean checkPearlWild(List<RewardLineDetailDo<SirenaSymbolDo>> rewardLineDetailList) {
        for (RewardLineDetailDo<SirenaSymbolDo> rewardLineDetail : rewardLineDetailList) {
            List<RewardSymbolDo<SirenaSymbolDo>> rewardSymbolList = rewardLineDetail.getRewardSymbolList();
            for (RewardSymbolDo<SirenaSymbolDo> rewardSymbol : rewardSymbolList) {
                if (SirenaGameHelper.SIRENA_Wild.getId().equals(rewardSymbol.getSymbol().getId())) {
                    return true;
                }

            }
        }
        return false;
    }
}
