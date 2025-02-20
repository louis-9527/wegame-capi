package com.wegame.capi.biz.domain.biz.game;

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
import java.util.List;

/**
 * 玩游戏模型
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GamePlayDo<WD extends SymbolDo> implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 游戏类型
     */
    private GameTypeEnum gameType;
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
     * 投注记录id
     */
    private Long betRecordId;

    private GameEventDo<WD> gameEventDo;
    //    /**
    //     * 免费轮次标识：是则生产10-15次滚轮结果
    //     */
    //    private boolean freeSpinsFlag = false;
}

