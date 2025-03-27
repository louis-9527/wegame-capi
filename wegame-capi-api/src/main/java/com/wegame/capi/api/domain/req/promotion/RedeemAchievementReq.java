package com.wegame.capi.api.domain.req.promotion;

import com.wegame.capi.api.domain.req.BaseReq;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RedeemAchievementReq extends BaseReq {

    private static final long serialVersionUID = 377465336800879L;
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
