package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.common.enums.GameTypeEnum;
import com.wegame.capi.infrastructure.config.nacos.NacosMysteriousProbabilisticConfig;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.Collections;

import java.math.BigDecimal;
import java.util.*;

/**
 * 概率服务
 */
@Component
public class ProbabilisticService {

    @Autowired
    NacosMysteriousProbabilisticConfig nacosMysteriousProbabilisticConfig;

    /**
     * 随机一个游戏元素
     *
     * @param gameId
     * @param cellNo
     * @return
     */
    public int randomOneSymbol(long gameId, int cellNo) {
        // todo 扩展性
        GameTypeEnum gameType = GameTypeEnum.valueOfCode(gameId);
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "Not Support Game");
        AssertUtil.assertNotNull(cellNo < 0, ErrorCode.ERR_PARAM, "Not Support Game");
        Map<String, List<Double>> configMap = nacosMysteriousProbabilisticConfig.getConfigMap();
        // todo 校验
        Map<String, Integer> map = new HashMap<>();
        configMap.entrySet().forEach(item -> {
            int val = new BigDecimal(item.getValue().get(cellNo - 1) * 100).intValue();
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
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (randomNumber >= startVal && randomNumber <= entry.getValue()) {
                return NumberUtils.toInt(entry.getKey().split("_")[0]);
            } else {
                startVal += entry.getValue();
            }
        }
        throw new BusinessException(ErrorCode.ERR_INTER);
    }
}
