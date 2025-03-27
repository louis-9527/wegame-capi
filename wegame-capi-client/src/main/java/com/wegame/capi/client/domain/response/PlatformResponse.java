package com.wegame.capi.client.domain.response;

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
@ApiModel("中台通用返回")
@EqualsAndHashCode(callSuper = false)
public class PlatformResponse<T> {
    private static final String SUC_CODE = "200";
    private static final String SUC_MESSAGE = "success";

    @ApiModelProperty("状态码")
    private String status;
    @ApiModelProperty("状态信息")
    private String message;
    @ApiModelProperty("返回结果")
    private T results;

    public static <T> PlatformResponse<T> build(T results) {
        return PlatformResponse.<T>builder()
                .status(SUC_CODE)
                .message(null)
                .results(results)
                .build();
    }

}
