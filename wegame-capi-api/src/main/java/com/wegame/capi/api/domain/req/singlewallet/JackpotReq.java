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
 * jackpot（派奖）请求参数
 *
 * @author jayni
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JackpotReq extends SingleWalletBaseReq {
    private static final long serialVersionUID = -636334553418L;

    /**
     * 通过从HTTP下注API响应中获取下注的赌注ID支付赌注的头奖
     * 必传：true
     * todo 拳击用不到这个字段，需要和单钱包沟通@micheal
     */
    private String betId;
    /**
     * 游戏记录唯一ID
     * 必传：true
     * todo 拳击用不到这个字段，需要和单钱包沟通@micheal
     */
    private String roundNo;
    /**
     * 总头奖金额
     * 必传：true
     */
    private BigDecimal jackpotAmount;

    /**
     * 头奖详情
     * 必传：true
     */
    private JackpotDetails jackpotDetails;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JackpotDetails implements Serializable {
        private static final long serialVersionUID = -63976553418L;
        /**
         * List of jackpot code win
         * 拳击：grand
         * todo 需要知道grand对应的int值@micheal
         */
        private List<Integer> jackpotTypes;
        /**
         * Jackpot win details
         * 必传：true
         */
        private List<JackpotWinDetails> jackpotWinDetails;

        /**
         * Jackpot pool details
         * 必传：true
         */
        private JackpotPool jackpotPool;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JackpotWinDetails implements Serializable {
        private static final long serialVersionUID = -634235346463418L;
        /**
         * jackpot code win
         * 必传：true
         */
        private Integer jackpotType;

        /**
         * Jackpot win amount
         * 必传：true
         */
        private BigDecimal winAmount;

        /**
         * Result codes of game (For Colorgame[code] & Pula-puti[code] only)
         * 必传：false
         */
        private List<Integer> jackpotResult;

    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JackpotPool implements Serializable {
        private static final long serialVersionUID = -634235346463418L;
        /**
         * Total jackpot pool amount
         * 必传：true
         */
        private BigDecimal totalJackpotAmount;
        /**
         * Jackpot grand jackpot details
         * 必传：true
         */
        private JackpotPoolInfo grandPool;
        /**
         * Jackpot major jackpot details
         * 必传：true
         */
        private JackpotPoolInfo majorPool;
        /**
         * Jackpot minor jackpot details
         * 必传：true
         */
        private JackpotPoolInfo minorPool;
        /**
         * Jackpot mini jackpot details
         * 必传：true
         */
        private JackpotPoolInfo miniPool;
        /**
         * Jackpot bonus jackpot details
         * 必传：true
         */
        private JackpotPoolInfo betBonusPool;

    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JackpotPoolInfo implements Serializable {
        private static final long serialVersionUID = -634235346463418L;
        /**
         * 奖池初始金额
         * 必传：true
         */
        private BigDecimal initAmount;
        /**
         * 中奖池当前金额
         * 必传：true
         */
        private BigDecimal amount;

    }


}
