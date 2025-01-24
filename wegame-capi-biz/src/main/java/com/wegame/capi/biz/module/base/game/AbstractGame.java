package com.wegame.capi.biz.module.base.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.SymbolEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽象游戏
 *
 * @param <R>
 */
public abstract class AbstractGame<R extends ToVoConverter> implements Game<R> {

    @Autowired
    public RandomService randomService;

    public Map<String, Integer> buildColumProbabilisticMap(int cellNo, Map<String, List<BigDecimal>> probabilisticConfigMap) {
        AssertUtil.assertTrue(MapUtils.isNotEmpty(probabilisticConfigMap), ErrorCode.ERR_INTER);
        Map<String, Integer> map = new HashMap<>();
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

    public SymbolEnum randomOneSymbol(Map<String, Integer> map, int randomNumber) {
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

    public SymbolDo randomNonScatterSymbol(Integer randomNumber, GameInfoDo gameInfo, Map<String, Integer> columProbabilisticMap) {
        Integer tempRandomNumber = randomNumber;
        while (true) {
            SymbolEnum symbol = randomOneSymbol(columProbabilisticMap, tempRandomNumber);
            SymbolDo symbolDo = gameInfo.getSymbolById(symbol.getId());
            if (!symbolDo.isScatterSymbol()) {
                return symbolDo;
            }
            // 如果非第一次则更新Random值(小概率)
            tempRandomNumber = randomService.randomInt(1, Constants.CONST_1000000);
        }
    }

    public SymbolDo randomNormalSymbol(Integer randomNumber, GameInfoDo gameInfo, Map<String, Integer> columProbabilisticMap) {
        SymbolEnum symbol = randomOneSymbol(columProbabilisticMap, randomNumber);
        return gameInfo.getSymbolById(symbol.getId());
    }
}
