package com.wegame.capi.biz.domain.biz.bet;

import com.wegame.capi.common.enums.InventoryTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BetInventoryControlDo implements Serializable {
    private static final long serialVersionUID = -226630893710848L;

    public static final int DEFAULT_RETRY_TIMES = -1;
    /**
     * 重试次数
     */
    private int retryTimes = -1;
    /**
     * 库存控制类型
     */
    private InventoryTypeEnum inventoryType;
    /**
     * 库存贡献金额
     */
    private Long inventoryContributionAmount;

    public void addRetryTimes() {
        this.retryTimes = this.retryTimes + 1;
    }
}

