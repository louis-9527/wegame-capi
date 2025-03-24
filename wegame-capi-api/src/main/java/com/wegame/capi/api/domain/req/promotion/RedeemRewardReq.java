package com.wegame.capi.api.domain.req.promotion;

import com.wegame.capi.api.domain.req.BaseReq;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RedeemRewardReq extends BaseReq {
    private static final long serialVersionUID = -28042898670268022L;
    /**
     * 星卡ID
     */
    private String rewardId;
    /**
     * 派奖金额
     */
    private String payoutAmount;
    /**
     * 投注ID(三方游戏)
     */
    private String betWithPayoutRefId;
}
