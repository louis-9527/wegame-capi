package com.wegame.capi.dao.mapper.domain.business;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DynamicOperationConfigQuery implements Serializable {
    private Long id;
    /**
     * 玩家id
     */
    private String playerId;
    /**
     * 游戏ID
     */
    private String gameId;
    /**
     * 投注类型
     */
    private String betType;
    /**
     * 倍数
     */
    private Double multiple;
}
