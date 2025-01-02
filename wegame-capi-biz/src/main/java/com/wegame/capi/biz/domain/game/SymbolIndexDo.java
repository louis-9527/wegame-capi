package com.wegame.capi.biz.domain.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 元素坐标
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class SymbolIndexDo implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 行号 todo final
     */
    private int rowIndex;
    /**
     * 列号  todo final
     */
    private int columIndex;

    public static SymbolIndexDo build(int rowIndex, int columIndex) {
        AssertUtil.assertTrue(rowIndex >= 0, ErrorCode.ERR_PARAM, "rowIndex must be greater than or equal 0");
        AssertUtil.assertTrue(columIndex >= 0, ErrorCode.ERR_PARAM, "columIndex must be greater than or equal 0");
        SymbolIndexDo indexDo = new SymbolIndexDo();
        indexDo.setRowIndex(rowIndex);
        indexDo.setColumIndex(columIndex);
        return indexDo;
    }

}

