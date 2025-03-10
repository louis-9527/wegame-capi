package com.wegame.capi.api.domain.req.promotion;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class RedeemTreasureReq extends BaseReq {
    @JsonProperty("宝箱ID")
    private String treasureId;
    @JsonProperty("宝箱等级")
    private Integer level;
}
