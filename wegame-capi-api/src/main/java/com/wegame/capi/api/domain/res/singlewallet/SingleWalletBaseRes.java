package com.wegame.capi.api.domain.res.singlewallet;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SingleWalletBaseRes implements Serializable {
    static final long serialVersionUID = -8519013691660936626L;

    @ApiModelProperty("refId")
    private String refId;

}
