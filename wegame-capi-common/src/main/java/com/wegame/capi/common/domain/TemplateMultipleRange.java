package com.wegame.capi.common.domain;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TemplateMultipleRange implements Serializable {
    private static final long serialVersionUID = -734841249803450751L;
    private static final String SPLIT = "_";
    private static final int ARR_SIZE = 2;
    /**
     * 倍数区间开始
     */
    private BigDecimal start;
    /**
     * 倍数区间结束
     */
    private BigDecimal end;

    public static TemplateMultipleRange build(String multipleRangeStr) {
        AssertUtil.assertStrNotEmpty(multipleRangeStr, ErrorCode.ERR_PARAM);
        String[] multipleRangeArr = multipleRangeStr.split(SPLIT);
        AssertUtil.assertTrue(multipleRangeArr.length == ARR_SIZE, ErrorCode.ERR_PARAM);
        BigDecimal left = new BigDecimal(multipleRangeArr[0]);
        AssertUtil.assertNotNull(left, ErrorCode.ERR_INTER);
        BigDecimal right = new BigDecimal(multipleRangeArr[1]);
        AssertUtil.assertNotNull(right, ErrorCode.ERR_INTER);
        return TemplateMultipleRange.builder()
                .start(left)
                .end(right)
                .build();
    }

    @Override
    public String toString() {
        return String.format("%s_%s", start.toString(), end.toString());
    }
}