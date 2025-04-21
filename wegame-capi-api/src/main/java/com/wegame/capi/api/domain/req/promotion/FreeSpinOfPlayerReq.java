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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FreeSpinOfPlayerReq extends BaseReq {
    private static final long serialVersionUID = 6923927574495233856L;
    /** gameCode */
    private String gameCode;
}
