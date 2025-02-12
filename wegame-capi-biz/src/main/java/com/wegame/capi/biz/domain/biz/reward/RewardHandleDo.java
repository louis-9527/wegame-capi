package com.wegame.capi.biz.domain.biz.reward;

import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 中奖do
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RewardHandleDo<T extends SymbolDo> implements Serializable {
    private static final long serialVersionUID = -226232434848L;
    /**
     * 游戏类型
     */
    private GameTypeEnum gameType;
    /**
     * 游戏信息
     */
    private GameInfoDo gameInfo;
    /**
     * 投注类型
     */
    private BetTypeEnum betType;
    /**
     * 投注子类型
     */
    private BetSubTypeEnum betSubType;
    /**
     * 投注金额
     */
    private Long betAmount;
    /**
     * 滚轮
     */
    private WheelDo<T> wheel;
    /**
     * 当前倍率
     */
    private BigDecimal currentMultiple;
}
