package com.wegame.capi.biz.domain.biz.bet;

import com.wegame.capi.common.enums.InventoryTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Map;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BetInventoryControlDo implements Serializable {
    private static final long serialVersionUID = -226630893710848L;
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
    private Long inventoryContributeAmount;

    public void addRetryTimes() {
        this.retryTimes = this.retryTimes + 1;
    }
}

