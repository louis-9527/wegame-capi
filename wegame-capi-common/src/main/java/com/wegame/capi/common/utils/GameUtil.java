package com.wegame.capi.common.utils;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Map;
import java.util.TreeMap;

public class GameUtil {

    /**
     * 随机一个map key
     *
     * @param sourceTreeMap source value：为概率
     * @param randomNumber  随机数
     * @return key
     */
    public static <T, R extends Number> T randomKey(TreeMap<T, R> sourceTreeMap, int randomNumber) {
        AssertUtil.assertNotNull(sourceTreeMap, ErrorCode.ERR_PARAM, "sourceTreeMap is null");
        AssertUtil.assertTrue(randomNumber > 0, ErrorCode.ERR_PARAM, "randomNumber is less than 0");
        int startVal = 0;
        for (Map.Entry<T, R> entry : CollectionUtils.emptyIfNull(sourceTreeMap.entrySet())) {
            if (randomNumber > startVal && randomNumber <= startVal + entry.getValue().intValue()) {
                return entry.getKey();
            } else {
                startVal += entry.getValue().intValue();
            }
        }
        throw new BusinessException(ErrorCode.ERR_INTER, "GameUtil randomKey,no match randomKey");
    }


}
