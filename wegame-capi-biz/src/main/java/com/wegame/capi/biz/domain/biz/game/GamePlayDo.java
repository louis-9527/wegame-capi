package com.wegame.capi.biz.domain.biz.game;

import com.wegame.capi.client.enums.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 游戏模型
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GamePlayDo implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 游戏Play类型
     */
    private GamePlayTypeEnum gamePlayType;
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
     * 支付金额
     */
    private Long payAmount;
    /**
     * 投注记录id
     */
    private Long betRecordId;
    ///////////////
    /**
     * 干预事件配置-投注记录ID
     */
    private Long operationEventConfigBetRecordId;
    /**
     * 干预时间
     */
    private GameOperationEventDo gameEventDo;
    /**
     * 波动性选择
     */
    private VolatilityTypeEnum volatility;
    /**
     * 继续游戏参数
     */
    private ContinuePlayContext continuePlayContext;

    @Data
    @SuperBuilder
    public static class ContinuePlayContext implements Serializable {
        private static final long serialVersionUID = -6172959648927527375L;
        /**
         * 父投注记录id
         */
        private Long parentBetRecordId;
        /**
         * 父游戏记录id
         */
        private Long parentGameRecordId;
        /**
         * 已派奖金额
         */
        private Long totalPayoutAmount;
        /**
         * 免费轮次数
         */
        private int freeWheelTimes;
    }
}

