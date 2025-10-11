package com.wegame.capi.client.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@ApiModel("jackpot查询返回")
@EqualsAndHashCode(callSuper = false)
public class JackpotResponse<T> {
    private static final String SUC_CODE = "0";
    private static final String SUC_MESSAGE = "success";

    private static final String ERROR_CODE = "-1";
    private static final String ERROR_MESSAGE = "error gameId";

    @ApiModelProperty("状态码")
    @JsonProperty("ErrorCode")
    private String ErrorCode;


    @ApiModelProperty("状态信息")
    @JsonProperty("Message")
    private String Message;


    @ApiModelProperty("返回结果")
    @JsonProperty("Data")
    private T Data;

    public static <T> JackpotResponse<T> build(T results) {
        return JackpotResponse.<T>builder()
                .ErrorCode(SUC_CODE)
                .Message(SUC_MESSAGE)
                .Data(results)
                .build();
    }

    public static <T> JackpotResponse<T> buildFail() {
        return JackpotResponse.<T>builder()
                .ErrorCode(ERROR_CODE)
                .Message(ERROR_MESSAGE)
                .build();
    }
}
