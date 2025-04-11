package com.wegame.capi.api.domain.res.promotion;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FreeSpinHistoryOfPlayerRes implements Serializable {
    private static final long serialVersionUID = -755778182692192148L;
    /** playerName */
    private String playerName;
    /** refId */
    private String refId;
    /** history */
    private List<FreeSpin> freeSpin;
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
        private static final long serialVersionUID = -2474675533659644295L;
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
        private Integer totalWin;
        /** firstUsedTime */
        private Integer firstUsedTime;
    }
}
