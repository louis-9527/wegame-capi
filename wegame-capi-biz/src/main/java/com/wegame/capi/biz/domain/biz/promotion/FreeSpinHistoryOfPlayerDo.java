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
public class FreeSpinHistoryOfPlayerDo implements Serializable {
    private static final long serialVersionUID = 9080271375211862557L;
    /** playerName */
    private String playerName;
    /** refId */
    private String refId;
    /** history */
    private List<FreeSpin> history;
    /** totalPages */
    private Integer totalPages;
    /** page */
    private Integer page;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class FreeSpin implements Serializable {
        private static final long serialVersionUID = -9039570199142455681L;
        /** campaignId */
        private String campaignId;
        /** initCount */
        private Integer initCount;
        /** remainingCount */
        private Integer remainingCount;
        /** gameId */
        private String gameId;
        /** gameName */
        private String gameName;
        /** totalWin */
        private BigDecimal totalWin;
        /** firstUsedTime */
        private Long firstUsedTime;
        /** freeBet */
        private BigDecimal freeBet;
    }
}
