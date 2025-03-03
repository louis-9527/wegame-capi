package com.wegame.capi.api.domain.req.promotion;

import com.wegame.capi.api.domain.req.BaseReq;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RedeemAchievementReq extends BaseReq {

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
