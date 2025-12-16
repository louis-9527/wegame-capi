package com.wegame.capi.biz.domain.biz.promotion.level;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "等级配置响应模型")
public class LevelConfigDo implements Serializable {

    private static final long serialVersionUID = -8967662891053165172L;

    @ApiModelProperty(value = "levelConfig", required = true)
    private List<LevelConfigDetail> levelConfig;
    @ApiModelProperty(value = "refId", required = true)
    private String refId;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class LevelConfigDetail implements Serializable {
        private static final long serialVersionUID = -6892372154929715562L;
        @ApiModelProperty(value = "name", required = true)
        private String name;
        @ApiModelProperty(value = "level", required = true)
        private int level;
        @ApiModelProperty(value = "experience", required = true)
        private BigDecimal experience;
    }
}
