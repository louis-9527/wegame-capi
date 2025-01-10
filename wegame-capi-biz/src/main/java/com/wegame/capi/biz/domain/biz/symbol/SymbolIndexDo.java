package com.wegame.capi.biz.domain.biz.symbol;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

import static com.wegame.capi.biz.module.reward.SymbolIndexContext.*;

/**
 * 元素坐标
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class SymbolIndexDo implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 行号
     */
    private int rowIndex;
    /**
     * 列号
     */
    private int columIndex;

    public static SymbolIndexDo build(int rowIndex, int columIndex) {
        AssertUtil.assertTrue(rowIndex >= 0, ErrorCode.ERR_PARAM, "rowIndex must be greater than or equal 0");
        AssertUtil.assertTrue(columIndex >= 0, ErrorCode.ERR_PARAM, "columIndex must be greater than or equal 0");
        if (Objects.nonNull(get(rowIndex, columIndex))) {
            return get(rowIndex, columIndex);
        } else {
            SymbolIndexDo indexDo = new SymbolIndexDo(rowIndex, columIndex);
            put(rowIndex, columIndex, indexDo);
            return indexDo;
        }
    }

}

