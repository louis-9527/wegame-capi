package com.wegame.capi.biz.domain.biz.promotion.level;

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
public class LevelConfigDo implements Serializable {

    private static final long serialVersionUID = -8967662891053165172L;

    private List<LevelConfigDetail> levelConfig;
    private String refId;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class LevelConfigDetail implements Serializable {
        private static final long serialVersionUID = -6892372154929715562L;
        private String name;
        private int level;
        private BigDecimal experience;
    }
}
