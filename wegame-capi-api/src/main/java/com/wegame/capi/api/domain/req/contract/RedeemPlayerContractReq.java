package com.wegame.capi.api.domain.req.contract;

import com.wegame.capi.api.domain.req.BaseReq;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RedeemPlayerContractReq extends BaseReq {

    private static final long serialVersionUID = 34567892379L;

}
