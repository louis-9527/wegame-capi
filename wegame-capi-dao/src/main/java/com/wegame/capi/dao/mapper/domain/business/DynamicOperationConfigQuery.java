package com.wegame.capi.dao.mapper.domain.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicOperationConfigQuery {
    private Long id;

    /**
     * 玩家id
     */
    private String playerId;

    /**
     * 游戏ID
     */
    private String gameId;



    private String betType;

    /**
     * 倍数
     */
    private Double multiple;

}
