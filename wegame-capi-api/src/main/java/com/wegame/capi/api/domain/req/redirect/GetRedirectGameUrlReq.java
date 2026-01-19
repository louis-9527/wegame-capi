package com.wegame.capi.api.domain.req.redirect;

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
public class GetRedirectGameUrlReq extends BaseReq {

    private static final long serialVersionUID = 1L;
    /**
     * 游戏id
     */
    private String gameId;
}
