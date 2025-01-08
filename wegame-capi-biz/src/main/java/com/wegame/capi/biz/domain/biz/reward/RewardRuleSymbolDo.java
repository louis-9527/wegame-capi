package com.wegame.capi.biz.domain.biz.reward;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolIndexDo;
import com.wegame.capi.client.enums.GameTypeEnum;
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
     * 中奖元素坐标
     */
    private SymbolDo symbol;
    /**
     * 中奖元素坐标
     */
    private SymbolIndexDo symbolIndex;
}

