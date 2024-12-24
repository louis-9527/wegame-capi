package com.wegame.capi.api.domain.res.singlewallet;

import cn.hutool.core.util.NumberUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * 奖券派奖
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AwardRes extends SingleWalletBaseRes {
    /**
     * 玩家名称
     */
    private String playerName;
    /**
     * 下注id
     */
    private String betId;
    /**
     * 玩家钱包余额
     */
    private BigDecimal credit;
    /**
     * 请求ID
     */
    private String refId;

    public Long getCreditLong() {
        if (credit == null) {
            return 0L;
        }
        return NumberUtil.mul(credit, 100).longValue();
    }
}
