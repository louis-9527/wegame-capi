package com.wegame.capi.biz.domain;

import com.wegame.capi.biz.domain.game.SymbolIndexDo;
import com.wegame.capi.common.enums.GameTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Set;

/**
 * 游戏模型
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameDo implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 游戏类型
     */
    private GameTypeEnum gameType;
    /**
     * 行
     */
    private Integer row;
    /**
     * 列
     */
    private Integer cell;
    /**
     * 元素集合
     */
    private Set<SymbolDo> symbolSet;

    /**
     * 元素坐标集合
     */
    private Set<SymbolIndexDo> symbolIndexSet;
}

