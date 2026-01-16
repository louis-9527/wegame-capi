package com.wegame.capi.api.domain.req.singlewallet;

import com.wegame.capi.api.domain.req.BaseReq;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JackpotReq extends BaseReq {
    private static final BigDecimal zero = BigDecimal.ZERO;
    private String betId;
    private String roundNo;
    private BigDecimal jackpotAmount;
    private JackpotDetails jackpotDetails;


    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JackpotDetails{
        private List<Integer> jackpotTypes;
        private List<JackpotWinDetails> jackpotWinDetails;
        private JackpotPool jackpotPool;
    }
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JackpotWinDetails {
        private Integer jackpotType;
        private BigDecimal winAmount;
        private List<Integer> jackpotResult;
    }


    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JackpotPool {
        private BigDecimal totalJackpotAmount = zero;
        private BigDecimal totalSupJackpotAmount = zero;
        private GrandPool grandPool;
        private MajorPool majorPool;
        private MinorPool minorPool;
        private MiniPool miniPool;
        private BetBonusPool betBonusPool;

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class GrandPool {
            private BigDecimal initAmount = zero;
            private BigDecimal amount = zero;
            private SupplementaryPool supplementaryPool;
        }

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MajorPool {
            private BigDecimal initAmount = zero;
            private BigDecimal amount = zero;
            private SupplementaryPool supplementaryPool;
        }

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MinorPool {
            private BigDecimal initAmount = zero;
            private BigDecimal amount = zero;
            private SupplementaryPool supplementaryPool;
        }

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MiniPool {
            private BigDecimal initAmount = zero;
            private BigDecimal amount = zero;
            private SupplementaryPool supplementaryPool;
        }

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BetBonusPool {
            private BigDecimal initAmount = zero;
            private BigDecimal amount = zero;
        }

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SupplementaryPool {
            private BigDecimal initAmount = zero;
            private BigDecimal amount = zero;
        }
    }


}


