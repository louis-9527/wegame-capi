package com.wegame.capi.client.domain.request.promotion;

import com.wegame.base.Validate;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "获取宝箱列表请求")
public class QueryTreasureRequest implements Validate, Serializable {
    private static final long serialVersionUID = -4568260918349559605L;
}
