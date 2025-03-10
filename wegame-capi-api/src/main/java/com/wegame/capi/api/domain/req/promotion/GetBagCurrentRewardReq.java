package com.wegame.capi.api.domain.req.promotion;

import com.wegame.capi.api.domain.req.BaseReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GetBagCurrentRewardReq extends BaseReq {

    private static final long serialVersionUID = 377465336780048879L;

    private Integer type;
    /**
     * 游戏code,对应程序游戏ID
     */
    private String gameCode;

}
