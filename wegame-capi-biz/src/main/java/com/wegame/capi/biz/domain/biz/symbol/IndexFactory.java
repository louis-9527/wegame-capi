package com.wegame.capi.biz.domain.biz.symbol;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.domain.vo.IndexVo;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class IndexFactory {

    private static final Map<String, IndexDo> INDEX_DO_MAP = new ConcurrentHashMap<>();
    private static final Map<String, IndexVo> INDEX_VO_MAP = new ConcurrentHashMap<>();

    private static final String INDEX_MAP_KEY = "%d-%d";

    static {
        for (int rowIndex = 0; rowIndex < 9; rowIndex++) {
            for (int columnIndex = 0; columnIndex < 9; columnIndex++) {
                IndexDo indexDo = buildDo(rowIndex, columnIndex);
                buildVo(indexDo);
            }
        }
    }

    public static IndexDo buildDo(int rowIndex, int columnIndex) {
        AssertUtil.assertTrue(rowIndex >= 0, ErrorCode.ERR_PARAM, "rowIndex must be greater than or equal 0");
        AssertUtil.assertTrue(columnIndex >= 0, ErrorCode.ERR_PARAM, "columnIndex must be greater than or equal 0");
        IndexDo existSymbolIndexDo = INDEX_DO_MAP.get(buildKey(rowIndex, columnIndex));
        if (Objects.nonNull(existSymbolIndexDo)) {
            return existSymbolIndexDo;
        } else {
            IndexDo symbolIndexDo = IndexDo.builder()
                    .rowIndex(rowIndex)
                    .columnIndex(columnIndex)
                    .build();
            INDEX_DO_MAP.put(buildKey(rowIndex, columnIndex), symbolIndexDo);
            return symbolIndexDo;
        }
    }

    public static IndexVo buildVo(IndexDo indexDo) {
        AssertUtil.assertNotNull(indexDo, ErrorCode.ERR_PARAM, "indexDo must not be null");
        AssertUtil.assertTrue(indexDo.getRowIndex() >= 0, ErrorCode.ERR_PARAM, "rowIndex must be greater than or equal 0");
        AssertUtil.assertTrue(indexDo.getColumnIndex() >= 0, ErrorCode.ERR_PARAM, "columnIndex must be greater than or equal 0");
        IndexVo existIndexVo = INDEX_VO_MAP.get(buildKey(indexDo.getRowIndex(), indexDo.getColumnIndex()));
        if (Objects.nonNull(existIndexVo)) {
            return existIndexVo;
        } else {
            IndexVo indexVo = IndexVo.builder()
                    .rowIndex(indexDo.getRowIndex())
                    .columnIndex(indexDo.getColumnIndex())
                    .build();
            INDEX_VO_MAP.put(buildKey(indexDo.getRowIndex(), indexDo.getColumnIndex()), indexVo);
            return indexVo;
        }
    }

    private static String buildKey(int rowIndex, int columnIndex) {
        return String.format(INDEX_MAP_KEY, rowIndex, columnIndex);
    }
}
