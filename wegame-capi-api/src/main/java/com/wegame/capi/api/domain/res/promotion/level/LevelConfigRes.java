package com.wegame.capi.api.domain.res.promotion.level;

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
public class LevelConfigRes implements Serializable {

    private static final long serialVersionUID = -8967662891053165172L;

    /**
     * levelConfig
     */
    private List<LevelConfigDetail> levelConfig;
    /**
     * refId
     */
    private String refId;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class LevelConfigDetail implements Serializable {
        private static final long serialVersionUID = -6892372154929715562L;
        /**
         * name
         */
        private String name;
        /**
         * level
         */
        private int level;
        /**
         * experience
         */
        private BigDecimal experience;
    }
}
