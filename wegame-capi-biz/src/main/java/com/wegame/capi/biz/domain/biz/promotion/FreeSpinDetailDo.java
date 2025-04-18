package com.wegame.capi.biz.domain.biz.promotion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FreeSpinDetailDo implements Serializable {
    private static final long serialVersionUID = -1752686518866030869L;
    /** refId */
    private String refId;
    /** Details of the campaign  */
    private List<Config> config;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Config implements Serializable {
        private static final long serialVersionUID = 3759213520989199112L;
        /** Campaign id of the free spin */
        private String campaignId;
        /** Campaign name of the free spin */
        private String name;
        /** Request pId */
        @JsonProperty("pId")
        private String pId;
        /** Request channel id */
        private String channelId;
        /** Status of campaign */
        private String status;
        /** Max player of campaign */
        private Integer maxPlayer;
        /** Bet value of campaign */
        private Integer betValue;
        /** Total spin times */
        private Integer totalFreeSpin;
        /** Campaign start date [Unix timestamps in seconds]  */
        private Date startDate;
        /** Campaign end date [Unix timestamps in seconds]  */
        private Date endDate;
        /** Campaign create date [Unix timestamps in seconds]  */
        private Date createTime;
    }
}
