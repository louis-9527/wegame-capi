package com.wegame.capi.biz.domain.game;

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
     * 游戏Id
     */
    private String gameId;
    /**
     * 投注类型: @see GameBetTypeEnum
     */
    private Integer id;
    /**
     * 坐标
     */
    private int[] coordinate;
    /**
     * 上一个
     */
    private RewardRuleSymbolDo last;

    /**
     * 下一个
     */
    private RewardRuleSymbolDo next;

}

