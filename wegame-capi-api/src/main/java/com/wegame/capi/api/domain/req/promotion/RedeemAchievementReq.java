package com.wegame.capi.api.domain.req.promotion;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RedeemAchievementReq extends PromotionBaseReq {

    /**
     * achievementId<br/>
     * get from getAchiement Api
     */
    private String achievementId;

    /**
     * redeemId<br/>
     * get from getAchiement Api
     */
    private String redeemId;
}
