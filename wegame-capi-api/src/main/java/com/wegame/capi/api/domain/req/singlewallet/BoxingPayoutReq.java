package com.wegame.capi.api.domain.req.singlewallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 拳击派彩请求参数
 *
 * @author jayni
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BoxingPayoutReq extends SingleWalletBaseReq {
    private static final long serialVersionUID = -6363345345418L;
    /**
     * 最小长度为1的付款列表
     * 必传：true
     */
    private List<BoxingPayoutInfo> payoutList;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoxingPayoutInfo implements Serializable {
        private static final long serialVersionUID = -63887603418L;

        /**
         * 总支付金额
         * 必传：true
         */
        private BigDecimal payoutAmount;

        /**
         * 通过从HTTP下注API响应中获取下注ID来支付下注
         * 必传：true
         */
        private String betId;

        /**
         * 支出明细
         * 必传：true
         */
        private BoxingPayoutDetails payoutDetails;

    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoxingPayoutDetails implements Serializable {
        private static final long serialVersionUID = -63636789898L;

        /**
         * 打赌结算时间[Unix时间戳，单位为秒]
         * 必传：true
         * 整局/回合的派彩时间(派彩记录创建时间)
         */
        private Long settlementDate;

        /**
         * 必传：true
         * <p>
         * 1.红
         * 2.蓝
         * 3.平
         */
        private Integer result;

        /**
         * 每种投注类型的支付明细
         * 必传：true
         */
        private List<BoxingPayoutMapInfo> payoutMap;

        /**
         * Jackpot details
         */
        private BoxingJackpotDetails jackpotDetails;

    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoxingJackpotDetails implements Serializable {

        /**
         * Jackpot pool details
         * <p>
         * 必传：true
         */
        private BoxingJackpotPool jackpotPool;

        /**
         * Jacpot contribution details
         * <p>
         * 必传：true
         */
        private BoxingJackpotContributionDetails jackpotContributionDetails;
    }


    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoxingPayoutMapInfo implements Serializable {
        /**
         * Boxing bet type code
         * 1.红
         * 2.蓝
         * 3.平
         */
        private Integer betType;

        /**
         * Amount of the bet type payout
         * 必传：true
         */
        private BigDecimal payoutAmount;

        /**
         * Bet amount of the bet type
         * 必传：true
         */
        private BigDecimal betAmount;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoxingJackpotPool implements Serializable {

        /**
         * Total jackpot pool amount
         * 当前回合/整局产生的所有奖池的金额。包括周/月/季奖池所有和
         * 必传：true
         */
        private BigDecimal totalJackpotAmount;

        /**
         * Jackpot grand jackpot details
         * 公共奖池传此值
         * 必传：true
         */
        private BoxingJackpotPoolCell grandPool;

        /**
         * Jackpot major jackpot details
         * 如果没有内部值传0
         * 必传：true
         */
        private BoxingJackpotPoolCell majorPool;

        /**
         * Jackpot minor jackpot details
         * 如果没有内部值传0
         * 必传：true
         */
        private BoxingJackpotPoolCell minorPool;

        /**
         * Jackpot mini jackpot details
         * 如果没有内部值传0
         * 必传：true
         */
        private BoxingJackpotPoolCell miniPool;

        /**
         * Jackpot grand jackpot details
         * 如果没有内部值传0
         * 必传：true
         */
        private BoxingJackpotPoolCell betBonusPool;

    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoxingJackpotPoolCell implements Serializable {

        /**
         * Jackpot pool initial amount
         * 必传：true
         */
        private BigDecimal initAmount;

        /**
         * Jackpot pool current amount
         * 必传：true
         */
        private BigDecimal amount;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoxingJackpotContributionDetails implements Serializable {
        /**
         * Jackpot grand contribution amount
         * 公共奖池传此值
         * 必传：true
         */
        private BigDecimal grandContributionAmount;
        /**
         * Jackpot major contribution amount
         * 如果没有传0
         * 必传：true
         */
        private BigDecimal majorContributionAmount;

        /**
         * Jackpot minor contribution amount
         * 如果没有传0
         * 必传：true
         */
        private BigDecimal minorContributionAmount;
        /**
         * Jackpot mini contribution amount
         * 如果没有传0
         * 必传：true
         */
        private BigDecimal miniContributionAmount;
        /**
         * bet bonus contribution amount
         * 如果没有传0
         * 必传：true
         */
        private BigDecimal betBonusContributionAmount;
    }


}
