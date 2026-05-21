package com.wegame.capi.biz.module.base;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.util.Objects;

import static com.wegame.capi.biz.module.base.SymbolHelper.handleSymbolNullArr;

@Slf4j
public class WheelHelper {

    public static <T extends SymbolDo> WheelDo<T> convertWheel(Object wheelObj) {
        WheelDo<T> wheel = FastJsonUtils.fromJSONString(FastJsonUtils.toJSONString(wheelObj), new TypeReference<WheelDo<T>>() {
        });
        AssertUtil.assertNotNull(wheel, ErrorCode.ERR_INTER);
        return wheel;
    }

    public static <T extends SymbolDo> WheelDo<T> convertWheelAndFillSymbolHolder(Object wheelObj, T symbolHolder, Class<T> clazz) {
        WheelDo<T> wheel = FastJsonUtils.fromJSONString(FastJsonUtils.toJSONString(wheelObj), new TypeReference<WheelDo<T>>() {
        });
        AssertUtil.assertNotNull(wheel, ErrorCode.ERR_INTER);
        wheel.setSymbolArr(handleSymbolNullArr(wheel.getSymbolArr(), symbolHolder, clazz));
        return wheel;
    }
}