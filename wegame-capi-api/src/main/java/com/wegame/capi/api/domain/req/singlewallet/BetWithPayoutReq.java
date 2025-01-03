package com.wegame.capi.api.domain.req.singlewallet;

import cn.hutool.core.util.NumberUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.common.context.UserInfoContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.slf4j.MDC;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BetWithPayoutReq extends SingleWalletBaseReq {
    private static final long serialVersionUID = 2474046915423606033L;

    private BigDecimal betAmount;

    private BigDecimal payoutAmount;

    private BetDetails betDetails;


    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BetDetails {
        private String gameId;
        private String roundNo;
        private Long wagersDate;
        private Long settlementDate;
        private Boolean isFreeRound;
        private JackpotDetails jackpotDetails;

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class JackpotDetails {
            private JackpotPool jackpotPool = new JackpotPool();
            private JackpotContributionDetails jackpotContributionDetails = new JackpotContributionDetails();

        }

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class JackpotPool {
            private BigDecimal totalJackpotAmount = BigDecimal.ZERO;
            private GrandPool grandPool;
            private MajorPool majorPool;
            private MinorPool minorPool;
            private MiniPool miniPool;
            private BetBonusPool betBonusPool;

        }

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class JackpotContributionDetails {
            private BigDecimal grandContributionAmount = BigDecimal.ZERO;
            private BigDecimal majorContributionAmount = BigDecimal.ZERO;
            private BigDecimal minorContributionAmount = BigDecimal.ZERO;
            private BigDecimal miniContributionAmount = BigDecimal.ZERO;
            private BigDecimal betBonusContributionAmount = BigDecimal.ZERO;

        }

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class GrandPool {
            private BigDecimal initAmount = BigDecimal.ZERO;
            private BigDecimal amount = BigDecimal.ZERO;

        }

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MajorPool {
            private BigDecimal initAmount = BigDecimal.ZERO;
            private BigDecimal amount = BigDecimal.ZERO;
        }

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MinorPool {
            private BigDecimal initAmount = BigDecimal.ZERO;
            private BigDecimal amount = BigDecimal.ZERO;

        }

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MiniPool {
            private BigDecimal initAmount = BigDecimal.ZERO;
            private BigDecimal amount = BigDecimal.ZERO;

        }

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BetBonusPool {
            private BigDecimal initAmount = BigDecimal.ZERO;
            private BigDecimal amount = BigDecimal.ZERO;
        }
    }

}
