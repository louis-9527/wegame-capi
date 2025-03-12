package com.wegame.capi.api.domain.res.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GetDailyMissionRes implements Serializable {
    private static final long serialVersionUID = -63633463436518L;
    /**
     * pId
     */
    private String pId;
    /**
     * channelId
     */
    private String channelId;
    /**
     * refId
     */
    private String refId;
    /**
     * playerName
     */
    private String playerName;
    /**
     * tierName
     */
    private String tierName;
    /**
     * tierId
     */
    private String tierId;
    /**
     * tierValue
     */
    private String tierValue;
    /**
     * refreshTime
     */
    private Date refreshTime;
    /**
     * 任务列表
     */
    private List<DailyMission> dailyMissionList;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyMission implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * dailyMissionId
         */
        private String dailyMissionId;
        /**
         * target
         */
        private String target;
        /**
         * reward
         */
        private String reward;
        /**
         * description
         */
        private String description;
        /**
         * Img Url
         */
        private String imgUrl;
        /**
         * experience
         */
        private String experience;
        /**
         * status
         */
        private String status;
        /**
         * starLevel
         */
        private Integer starLevel;
    }
}
