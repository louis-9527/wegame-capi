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
public class GetBagRewardByIdReq extends BaseReq {
    private static final long serialVersionUID = -281904288670268022L;
    /**
     * rewardConfigId
     */
    private String rewardConfigId;
}
