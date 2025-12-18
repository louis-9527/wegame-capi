package com.wegame.capi.biz.domain.biz.promotion.level;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PlayerLevelDo implements Serializable {

    private static final long serialVersionUID = 1804882586472683413L;

    private String playerName;
    private int playerLevel;
    private BigDecimal playerExperience;
    private String refId;
}
