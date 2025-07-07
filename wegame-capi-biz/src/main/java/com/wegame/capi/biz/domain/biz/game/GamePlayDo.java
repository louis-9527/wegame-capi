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
    private GameOperationEventDo gameEventDo;

    private VolatilityEnum volatility;

    private ContinuPlayContext continuPlayContext;

    @Data
    @SuperBuilder
    private static class ContinuPlayContext {

        /**
         * 父投注记录id
         */
        private Long parentBetRecordId;

        /**
         * 已派奖金额
         */
        private long totalPayoutAmount;

        /**
         * 免费轮次数
         */
        private int freeWheelTimes;
    }
}

