package com.wegame.capi.client.domain.vo;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SymbolIndexVo {
    /**
     * 行号
     */
    private int rowIndex;
    /**
     * 列号
     */
    private int columIndex;

    public static SymbolIndexVo build(int rowIndex, int columIndex) {
        AssertUtil.assertTrue(rowIndex >= 0, ErrorCode.ERR_PARAM, "rowIndex must be greater than or equal 0");
        AssertUtil.assertTrue(columIndex >= 0, ErrorCode.ERR_PARAM, "columIndex must be greater than or equal 0");
        SymbolIndexVo indexDo = new SymbolIndexVo();
        indexDo.setRowIndex(rowIndex);
        indexDo.setColumIndex(columIndex);
        return indexDo;
    }
}
