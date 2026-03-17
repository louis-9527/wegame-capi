package com.wegame.capi.common.domain;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.wegame.capi.common.Constants.CONST_1000;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BetAmountRangeDo implements Serializable {
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

    public static BetAmountRangeDo build(String rangeStr) {
        AssertUtil.assertStrNotEmpty(rangeStr, ErrorCode.ERR_PARAM);
        String[] rangArr = rangeStr.split(SPLIT);
        AssertUtil.assertTrue(rangArr.length == ARR_SIZE, ErrorCode.ERR_PARAM);
        BigDecimal tempStart = new BigDecimal(rangArr[0]);
        BigDecimal tempEnd = new BigDecimal(rangArr[1]);
        AssertUtil.assertTrue(tempStart.compareTo(tempEnd) <= 0, ErrorCode.ERR_PARAM);
        return BetAmountRangeDo.builder()
                .start(tempStart)
                .end(tempEnd)
                .build();
    }

    /**
     * 投注金额控制：左闭右闭
     */
    public boolean inRange(long betAmount) {
        if (start.compareTo(end) == 0) {
            return start.longValue() == betAmount;
        } else {
            return start.multiply(new BigDecimal(CONST_1000)).longValue() <= betAmount
                    && betAmount <= end.multiply(new BigDecimal(CONST_1000)).longValue();
        }
    }
}