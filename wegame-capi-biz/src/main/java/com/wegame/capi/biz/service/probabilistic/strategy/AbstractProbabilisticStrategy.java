package com.wegame.capi.biz.service.probabilistic.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.common.enums.GameTypeEnum;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 概率策略
 */
public abstract class AbstractProbabilisticStrategy implements ProbabilisticStrategy {

    private static final String KEY_NAME_SPLITTER = "_";

    /**
     * 获取概率配置表
     * @return 概率配置表
     */
    public abstract Map<String, List<BigDecimal>> getProbabilisticConfigMap();

    public SymbolDo randomOneSymbol(GameTypeEnum gameType, int cellNo) {
        // todo 自测
        // todo 扩展性设计与实现
        // todo 配置数据正确性校验
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "Not Support Game");
        AssertUtil.assertNotNull(cellNo < 0, ErrorCode.ERR_PARAM, "Not Support Game");
        Map<String, List<BigDecimal>> probabilisticConfigMap = getProbabilisticConfigMap();
        // todo 数据校验
        Map<String, Integer> map = new HashMap<>();
        probabilisticConfigMap.entrySet().forEach(item -> {
            int val = (item.getValue().get(cellNo - 1).multiply(new BigDecimal(100))).intValue();
            if (val > 0) {
                map.put(item.getKey(), val);
            }
        });
        Integer sum = map.values()
                .stream()
                .reduce(0, Integer::sum);
        AssertUtil.assertTrue(sum == 10000, ErrorCode.ERR_PARAM, "Not Support Game");
        Random random = new Random();
        // 生成一个1到10000之间的随机整数
        int randomNumber = 1 + random.nextInt(10000);
        int startVal = 0;
        for (Map.Entry<String, Integer> entry : CollectionUtils.emptyIfNull(map.entrySet())) {
            if (randomNumber > startVal && randomNumber <= startVal + entry.getValue()) {
                String[] strArr = entry.getKey().split(KEY_NAME_SPLITTER);
                return SymbolDo.builder()
                        .id(Integer.valueOf(strArr[0]))
                        .name(strArr[1])
                        .build();
            } else {
                startVal += entry.getValue();
            }
        }
        throw new BusinessException(ErrorCode.ERR_INTER);
    }
}
