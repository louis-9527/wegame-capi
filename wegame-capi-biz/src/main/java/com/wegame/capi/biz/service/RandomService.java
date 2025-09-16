package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.utils.SecureRandomUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class RandomService {

    /**
     * 生成随机数据[min, max]
     *
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public int randomInt(int min, int max) {
        AssertUtil.assertTrue(min < max, ErrorCode.ERR_INTER);
        return SecureRandomUtil.randomIntInRange(min, max);
    }

    /**
     * 生成随机数据[min, max]
     *
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public List<Integer> randomIntList(int min, int max, int count) {
        return SecureRandomUtil.randomIntInRangeList(min, max, count);
    }

    /**
     * 根据权重随机key
     *
     * @param map 索引值 权重
     * @return 索引值
     */
    public <T> T randomKeyByWeight(Map<T, Integer> map, Integer totalWeight) {
        int mapSize = map.size();
        AssertUtil.assertTrue(mapSize > 0, ErrorCode.ERR_INTER, "weight map size is less than 1");
        AssertUtil.assertTrue(totalWeight > 0, ErrorCode.ERR_INTER, "total weight is less than 1");
        if (mapSize > 1 && totalWeight > 1) {
            int randomNumber = this.randomInt(Constants.CONST_1, totalWeight);
            int startVal = 0;
            for (Map.Entry<T, Integer> entry : CollectionUtils.emptyIfNull(map.entrySet())) {
                if (randomNumber > startVal && randomNumber <= startVal + entry.getValue()) {
                    return entry.getKey();
                } else {
                    startVal += entry.getValue();
                }
            }
        } else {
            return map.entrySet().stream()
                    .filter(e -> e.getValue() > 0)
                    .findFirst()
                    .map(Map.Entry::getKey)
                    .orElseThrow(() -> new BusinessException(ErrorCode.ERR_INTER, "random key generation failed"));
        }
        throw new BusinessException(ErrorCode.ERR_INTER, "random key generation failed");
    }

    public <T> T randomKeyByWeight(Map<T, Integer> map) {
        int totalWeights = map.values().stream().filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        return randomKeyByWeight(map, totalWeights);
    }
}
