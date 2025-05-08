package com.wegame.capi.api.domain.res.promotion;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FreeSpinDetailRes implements Serializable {
    private static final long serialVersionUID = -1162816241475197005L;
    /** refId */
    private String refId;
    /** Details of the campaign  */
    private Config config;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Config implements Serializable {
        private static final long serialVersionUID = -7197968786315211753L;
        /** Campaign id of the free spin */
        private String campaignId;
        /** Campaign name of the free spin */
        private String name;
        /** Request pId */
        private String pId;
        /** Request channel id */
        private String channelId;
        /** Status of campaign */
        private Integer status;
        /** Max player of campaign */
        private Integer maxPlayer;
        /** Bet value of campaign */
        private BigDecimal betValue;
        /** Total spin times */
        private Integer totalFreeSpin;
        /** Campaign start date [Unix timestamps in seconds]  */
        private Integer startDate;
        /** Campaign end date [Unix timestamps in seconds]  */
        private Integer endDate;
        /** Campaign create date [Unix timestamps in seconds]  */
        private Integer createTime;
    }
}
