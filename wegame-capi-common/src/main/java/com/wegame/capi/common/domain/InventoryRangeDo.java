package com.wegame.capi.common.domain;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InventoryRangeDo implements Serializable {
    private static final long serialVersionUID = -734841249803450751L;
    private static final String SPLIT = "_";
    private static final int ARR_SIZE = 2;
    /**
     * 库存区间开始
     */
    private long start;
    /**
     * 库存区间结束
     */
    private long end;

    public static InventoryRangeDo build(String rangeStr) {
        AssertUtil.assertStrNotEmpty(rangeStr, ErrorCode.ERR_PARAM);
        String[] rangArr = rangeStr.split(SPLIT);
        AssertUtil.assertTrue(rangArr.length == ARR_SIZE, ErrorCode.ERR_PARAM);
        Long tempStart = Long.valueOf(rangArr[0]);
        Long tempEnd = Long.valueOf(rangArr[1]);
        AssertUtil.assertTrue(tempStart <= tempEnd, ErrorCode.ERR_PARAM);
        return InventoryRangeDo.builder()
                .start(tempStart)
                .end(tempEnd)
                .build();
    }

    /**
     * 库存范围：左开右闭
     */
    public boolean inRange(Long currentInventory) {
        if (start == end) {
            return start == currentInventory;
        } else {
            return start < currentInventory && currentInventory <= end;
        }
    }
}