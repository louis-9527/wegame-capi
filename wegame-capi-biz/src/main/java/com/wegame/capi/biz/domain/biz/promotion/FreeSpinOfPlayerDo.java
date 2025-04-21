package com.wegame.capi.biz.domain.biz.promotion;

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
@EqualsAndHashCode
public class FreeSpinOfPlayerDo implements Serializable {
    private static final long serialVersionUID = 6993114950391446361L;
    /** playerName */
    private String playerName;
    /** refId */
    private String refId;
    /** current */
    private List<FreeSpin> current;
    /** others */
    private List<FreeSpin> others;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class FreeSpin implements Serializable {
        private static final long serialVersionUID = 85667361882570293L;
        /** channelId */
        private String channelId;
        /** campaignId */
        private String campaignId;
        /** initCount */
        private Integer initCount;
        /** remainingCount */
        private Integer remainingCount;
        /** expireDate */
        private Integer expireDate;
        /** gameId */
        private String gameId;
        /** gameIdRef */
        private String gameIdRef;
        /** gameName */
        private String gameName;
        /** totalWin */
        private Integer totalWin;
        /** freeBet */
        private BigDecimal freeBet;
    }
}
