package com.wegame.capi.biz.domain.biz.reward;

import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.bizevent.GameBizEventDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.MultiplePieTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
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
     * 游戏Play类型
     */
    private GamePlayTypeEnum gamePlayType;
    /**
     * 投注金额
     */
    private Long betAmount;
    /**
     * 支付金额
     */
    private Long payAmount;
    /**
     * 滚轮
     */
    private WheelDo<T> wheel;
    /**
     * 当前倍率
     */
    private BigDecimal currentMultiple;
    /**
     * 当前累计中奖金额
     */
    private Long currentTotalRewardAmount;
    /**
     * 倍率盘类型
     */
    private MultiplePieTypeEnum multiplePieType;

    /**
     * 累计中奖次数
     * 用于计算对应业务事件的index
     *
     * @since {@link GameTypeEnum#ATLANTIS}
     */
    private Integer totalRewardCount;
    /**
     * 固定中业务事件
     *
     * @since {@link GameTypeEnum#ATLANTIS}
     */
    private List<GameBizEventDo> fixedBizEventList;

    /**
     * 实际中的免费轮次数-bayanihan中用于防止重新随机免费轮次数
     */
    private Integer actualRewardFreeWheelTimes;

}
