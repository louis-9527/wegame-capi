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
public class GetBagCurrentRewardReq extends BaseReq {

    private static final long serialVersionUID = 37746533680048879L;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 游戏code,对应程序游戏ID
     */
    private String gameCode;

}
