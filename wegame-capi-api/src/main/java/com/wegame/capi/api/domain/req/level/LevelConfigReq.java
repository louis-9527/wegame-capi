package com.wegame.capi.api.domain.req.level;

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
public class LevelConfigReq extends BaseReq {

    private static final long serialVersionUID = -4005108507168883448L;
}
