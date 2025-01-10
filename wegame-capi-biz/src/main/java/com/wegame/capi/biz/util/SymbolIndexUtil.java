package com.wegame.capi.biz.util;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.symbol.SymbolIndexDo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SymbolIndexUtil {

    private static final Map<String, SymbolIndexDo> SYMBOL_INDEX_DO_MAP = new ConcurrentHashMap<>();
    private static final String SYMBOL_INDEX_DO_MAP_KEY = "%d-%d";

    public static SymbolIndexDo put(int rowIndex, int columIndex, SymbolIndexDo symbolIndexDo) {
        AssertUtil.assertNotNull(symbolIndexDo, ErrorCode.ERR_PARAM, "symbolIndexDo is null");
        return SYMBOL_INDEX_DO_MAP.put(buildKey(rowIndex, columIndex), symbolIndexDo);
    }

    public static SymbolIndexDo get(int rowIndex, int columIndex) {
        return SYMBOL_INDEX_DO_MAP.get(buildKey(rowIndex, columIndex));
    }

    private static String buildKey(int rowIndex, int columIndex) {
        return String.format(SYMBOL_INDEX_DO_MAP_KEY, rowIndex, columIndex);
    }

}
