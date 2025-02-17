package com.wegame.capi.api.domain.req.singlewallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 拳击投注状态req
 *
 * @author jayni
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BetStatusReq extends SingleWalletBaseReq {
    private static final long serialVersionUID = -63638322353418L;

    public static final int BET_WITH_PAYOUT_BET_TYPE = 2;
    /**
     * 赌注的唯一ID
     * 必传：false
     */
    private String betId;

    /**
     * 注单时间[Unix时间戳，单位为秒]
     * 必传：true
     */
    private Long wagersDate;

    /**
     * 1: Other Bet Types; 2: betWithPayout
     * 必传：true
     */
    private Integer betType;
}
