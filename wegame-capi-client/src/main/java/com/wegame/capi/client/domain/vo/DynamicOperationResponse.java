package com.wegame.capi.client.domain.vo;

import com.wegame.base.domain.CommonResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("动态运营通用返回")
@EqualsAndHashCode(callSuper = false)
public class DynamicOperationResponse<T> {
    @ApiModelProperty("状态码")
    private String status;
    @ApiModelProperty("状态信息")
    private String message;
    @ApiModelProperty("返回结果")
    private T results;

    private DynamicOperationResponse(T data) {
        this.status = "200";
        this.message = "success";
        this.results = data;
    }


    public static <T> DynamicOperationResponse<T> build(T data) {
        return new DynamicOperationResponse<T>(data);
    }

}
