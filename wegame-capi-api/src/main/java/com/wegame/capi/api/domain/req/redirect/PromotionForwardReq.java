package com.wegame.capi.api.domain.req.redirect;

import com.wegame.capi.api.domain.req.BaseReq;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 *
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PromotionForwardReq extends BaseReq {
    private static final long serialVersionUID = 1L;
    private Map<String, Object> data;
}
