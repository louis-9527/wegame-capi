package com.wegame.capi.biz.domain.biz.symbol;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class IndexFactory {

    private static final Map<String, IndexDo> INDEX_MAP = new ConcurrentHashMap<>();
    private static final String INDEX_MAP_KEY = "%d-%d";

    public static IndexDo build(int rowIndex, int columIndex) {
        AssertUtil.assertTrue(rowIndex >= 0, ErrorCode.ERR_PARAM, "rowIndex must be greater than or equal 0");
        AssertUtil.assertTrue(columIndex >= 0, ErrorCode.ERR_PARAM, "columIndex must be greater than or equal 0");
        IndexDo existSymbolIndexDo = INDEX_MAP.get(buildKey(rowIndex, columIndex));
        if (Objects.nonNull(existSymbolIndexDo)) {
            return existSymbolIndexDo;
        } else {
            IndexDo symbolIndexDo = IndexDo.builder()
                    .rowIndex(rowIndex)
                    .columIndex(columIndex)
                    .build();
            INDEX_MAP.put(buildKey(rowIndex, columIndex), symbolIndexDo);
            return symbolIndexDo;
        }
    }

    private static String buildKey(int rowIndex, int columIndex) {
        return String.format(INDEX_MAP_KEY, rowIndex, columIndex);
    }
}
