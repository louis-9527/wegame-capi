package com.wegame.capi.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 游戏元素
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SymbolDo implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 游戏Id
     */
    private Long gameId;
    /**
     * 投注类型: @see GameBetTypeEnum
     */
    private Integer id;
    /**
     * 投注详情
     */
    private String name;
}

