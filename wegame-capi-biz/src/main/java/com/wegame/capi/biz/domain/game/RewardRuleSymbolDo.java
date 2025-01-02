package com.wegame.capi.biz.domain.game;

import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.common.enums.GameTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 中奖规则游戏元素
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RewardRuleSymbolDo implements Serializable {
    private static final long serialVersionUID = -22667655543710848L;
    /**
     * 游戏类型
     */
    private GameTypeEnum gameType;
    /**
     * 投注类型: @see GameBetTypeEnum
     */
    private SymbolDo symbol;
    /**
     * 坐标
     */
    private SymbolIndexDo coordinate;

}

