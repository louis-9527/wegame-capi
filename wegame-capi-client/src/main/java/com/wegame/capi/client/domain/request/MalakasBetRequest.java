package com.wegame.capi.client.domain.request;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Malakas正常投注请求参数")
public class MalakasBetRequest  implements BetRequestValidate, Serializable {
    private static final long serialVersionUID = 8816381026140374720L;
}
