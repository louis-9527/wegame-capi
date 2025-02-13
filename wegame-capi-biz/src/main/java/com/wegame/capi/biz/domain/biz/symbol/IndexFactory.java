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

    public static IndexDo buildDo(int rowIndex, int columuIndex) {
        AssertUtil.assertTrue(rowIndex >= 0, ErrorCode.ERR_PARAM, "rowIndex must be greater than or equal 0");
        AssertUtil.assertTrue(columuIndex >= 0, ErrorCode.ERR_PARAM, "columuIndex must be greater than or equal 0");
        IndexDo existSymbolIndexDo = INDEX_DO_MAP.get(buildKey(rowIndex, columuIndex));
        if (Objects.nonNull(existSymbolIndexDo)) {
            return existSymbolIndexDo;
        } else {
            IndexDo symbolIndexDo = IndexDo.builder()
                    .rowIndex(rowIndex)
                    .columnIndex(columuIndex)
                    .build();
            INDEX_DO_MAP.put(buildKey(rowIndex, columuIndex), symbolIndexDo);
            return symbolIndexDo;
        }
    }

    public static IndexVo buildVo(IndexDo indexDo) {
        AssertUtil.assertNotNull(indexDo, ErrorCode.ERR_PARAM, "indexDo must not be null");
        AssertUtil.assertTrue(indexDo.getRowIndex() >= 0, ErrorCode.ERR_PARAM, "rowIndex must be greater than or equal 0");
        AssertUtil.assertTrue(indexDo.getColumnIndex() >= 0, ErrorCode.ERR_PARAM, "columuIndex must be greater than or equal 0");
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

    private static String buildKey(int rowIndex, int columuIndex) {
        return String.format(INDEX_MAP_KEY, rowIndex, columuIndex);
    }
}
