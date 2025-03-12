package com.wegame.capi.api.domain.req.promotion;

import com.wegame.capi.api.domain.req.BaseReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RedeemRewardReq extends BaseReq {
    private static final long serialVersionUID = -2819042898670268022L;

    /**
     * 星卡ID
     */
    private String rewardId;

    /**
     * 派奖金额
     */
    private BigDecimal payoutAmount;

    /**
     * 投注ID(三方游戏)
     */
    private String betWIthPayoutRefId;
}
