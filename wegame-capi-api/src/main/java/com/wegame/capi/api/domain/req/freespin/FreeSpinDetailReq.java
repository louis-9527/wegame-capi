package com.wegame.capi.api.domain.req.freespin;

import com.wegame.capi.api.domain.req.BaseReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class FreeSpinDetailReq extends BaseReq {
    private static final long serialVersionUID = 947294475306054791L;
    /** Request id of the request */
    private String campaignId;
}
