package com.wegame.capi.biz.module.base.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.SymbolEnum;
import lombok.extern.slf4j.Slf4j;
import com.wegame.capi.common.enums.SymbolSizeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

/**
 * 抽象游戏
 *
 * @param <R>
 */
@Slf4j
public abstract class AbstractGame<R extends ToVoConverter> implements Game<R> {

    @Autowired
    public RandomService randomService;
    @Autowired
    public SnowflakeIdGenerator snowflakeIdGenerator;

    public long generateGameRecordId() {
        long gameRecordId = snowflakeIdGenerator.nextId();
        log.info("generateGameRecord id:{}", gameRecordId);
        return gameRecordId;
    }

    public List<Long> generateGameRecordIdList(int count) {
        List gameRecordIdList = new ArrayList<>();
        int temp = 0;
        while (temp < count) {
            long gameRecordId = snowflakeIdGenerator.nextId();
            log.info("generateGameRecord id, free wheel:{}", gameRecordId);
            gameRecordIdList.add(gameRecordId);
            temp++;
        }
        return gameRecordIdList;
    }

    public TreeMap<String, Integer> buildColumProbabilisticMap(int cellNo, Map<String, List<BigDecimal>> probabilisticConfigMap) {
        AssertUtil.assertTrue(MapUtils.isNotEmpty(probabilisticConfigMap), ErrorCode.ERR_INTER);
        TreeMap<String, Integer> map = new TreeMap<>();
        probabilisticConfigMap.entrySet().forEach(item -> {
            int val = (item.getValue().get(cellNo).multiply(new BigDecimal(Constants.CONST_10000))).intValue();
            if (val > 0) {
                map.put(item.getKey(), val);
            }
        });
        Integer sum = map.values()
                .stream()
                .reduce(0, Integer::sum);
        AssertUtil.assertTrue(sum == Constants.CONST_1000000, ErrorCode.ERR_INTER, "Symbol config error,cellNo:" + cellNo);
        return map;
    }

    public SymbolEnum randomOneSymbol(TreeMap<String, Integer> map, int randomNumber) {
        int startVal = 0;
        for (Map.Entry<String, Integer> entry : CollectionUtils.emptyIfNull(map.entrySet())) {
            if (randomNumber > startVal && randomNumber <= startVal + entry.getValue()) {
                return SymbolEnum.valueOfId(entry.getKey());
            } else {
                startVal += entry.getValue();
            }
        }
        throw new BusinessException(ErrorCode.ERR_INTER);
    }

    public SymbolDo randomNonScatterSymbol(Integer randomNumber, GameInfoDo gameInfo, TreeMap<String, Integer> columnProbabilisticMap) {
        Integer tempRandomNumber = randomNumber;
        while (true) {
            SymbolEnum symbol = randomOneSymbol(columnProbabilisticMap, tempRandomNumber);
            SymbolDo symbolDo = gameInfo.getSymbolById(symbol.getId());
            if (!symbolDo.isScatterSymbol()) {
                return symbolDo;
            }
            tempRandomNumber = randomService.randomInt(1, Constants.CONST_1000000);
        }
    }

    public SymbolDo randomNormalSymbol(Integer randomNumber, GameInfoDo gameInfo, TreeMap<String, Integer> columnProbabilisticMap) {
        SymbolEnum symbol = randomOneSymbol(columnProbabilisticMap, randomNumber);
        return gameInfo.getSymbolById(symbol.getId());
    }

    public SymbolSizeEnum randomSymbolSize(Map<String, Integer> map, int randomNumber) {
        int startVal = 0;
        for (Map.Entry<String, Integer> entry : CollectionUtils.emptyIfNull(map.entrySet())) {
            if (randomNumber > startVal && randomNumber <= startVal + entry.getValue()) {
                return SymbolSizeEnum.valueOfCode(entry.getKey());
            } else {
                startVal += entry.getValue();
            }
        }
        throw new BusinessException(ErrorCode.ERR_INTER);
    }
}
