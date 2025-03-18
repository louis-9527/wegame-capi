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
public class RedeemTreasureReq extends BaseReq {
    private static final long serialVersionUID = 3746533648879L;
    /**
     * 宝箱ID
     */
    private String treasureId;
    /**
     * 宝箱等级
     */
    private Integer level;
}
