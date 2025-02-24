package com.wegame.capi.api.domain.res.singlewallet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("获取用户余额响应模型")
public class GetCreditRes extends SingleWalletBaseRes {
    @ApiModelProperty("玩家姓名")
    private String playerName;
    @ApiModelProperty("玩家余额")
    private BigDecimal credit;
}

