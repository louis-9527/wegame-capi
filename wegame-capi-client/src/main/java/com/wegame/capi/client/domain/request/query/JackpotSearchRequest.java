package com.wegame.capi.client.domain.request.query;

import com.wegame.base.Validate;
import com.wegame.base.domain.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "获取干预事件结果")
public class JackpotSearchRequest extends BaseRequest implements Validate, Serializable {
    private static final long serialVersionUID = 8671666439105202109L;

    @ApiModelProperty("游戏id: zhumeng004")
    private String gameId;
}
