package com.wegame.capi.api.domain.req.singlewallet;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.utils.GameAmountUtil;
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
        private  static final BigDecimal zero = BigDecimal.ZERO;
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
            private BigDecimal totalJackpotAmount = zero;
            private GrandPool grandPool = new GrandPool();
            private MajorPool majorPool = new MajorPool();
            private MinorPool minorPool = new MinorPool();
            private MiniPool miniPool = new MiniPool();
            private BetBonusPool betBonusPool = new BetBonusPool();

        }

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class JackpotContributionDetails {
            private BigDecimal grandContributionAmount = zero;
            private BigDecimal majorContributionAmount = zero;
            private BigDecimal minorContributionAmount = zero;
            private BigDecimal miniContributionAmount = zero;
            private BigDecimal betBonusContributionAmount = zero;

        }

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class GrandPool {
            private BigDecimal initAmount = zero;
            private BigDecimal amount = zero;

        }

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MajorPool {
            private BigDecimal initAmount = zero;
            private BigDecimal amount = zero;
        }

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MinorPool {
            private BigDecimal initAmount = zero;
            private BigDecimal amount = zero;

        }

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MiniPool {
            private BigDecimal initAmount = zero;
            private BigDecimal amount = zero;
        }

        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BetBonusPool {
            private BigDecimal initAmount = zero;
            private BigDecimal amount = zero;
        }
    }

    public static void main(String[] args) {
        BetWithPayoutReq payoutReq = BetWithPayoutReq.builder()
                .createTime(DateUtil.toUTC(new Date()))
                .betDetails(BetWithPayoutReq.BetDetails.builder()
                        .gameId("111")
                        .wagersDate(DateUtil.getCurrentTimestamp())
                        .settlementDate(DateUtil.getCurrentTimestamp())
                        .isFreeRound(false)
                        .jackpotDetails(new BetDetails.JackpotDetails())
                        .build())
                .build();

        System.out.println(JSON.toJSONString(payoutReq));
    }

}
