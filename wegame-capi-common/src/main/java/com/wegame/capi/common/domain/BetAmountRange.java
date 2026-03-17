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
public class BetAmountRange implements Serializable {
    private static final long serialVersionUID = -734841249803450751L;
    private static final String SPLIT = "_";
    private static final int ARR_SIZE = 2;
    /**
     * 库存区间开始
     */
    private BigDecimal start;
    /**
     * 库存区间结束
     */
    private BigDecimal end;

    public static BetAmountRange build(String rangeStr) {
        AssertUtil.assertStrNotEmpty(rangeStr, ErrorCode.ERR_PARAM);
        String[] rangArr = rangeStr.split(SPLIT);
        AssertUtil.assertTrue(rangArr.length == ARR_SIZE, ErrorCode.ERR_PARAM);
        BigDecimal tempStart = new BigDecimal(rangArr[0]);
        BigDecimal tempEnd = new BigDecimal(rangArr[1]);
        AssertUtil.assertTrue(tempStart.compareTo(tempEnd) <= 0, ErrorCode.ERR_PARAM);
        return BetAmountRange.builder()
                .start(tempStart)
                .end(tempEnd)
                .build();
    }

    public boolean inRange(long val) {
        if (start.compareTo(end) == 0) {
            return start.longValue() == val;
        } else {
            return start.multiply(new BigDecimal(1000)).longValue() <= val && val <= end.multiply(new BigDecimal(1000)).longValue();
        }
    }
}