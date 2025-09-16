package com.wegame.capi.api.domain.res.promotion;

import io.swagger.annotations.ApiModel;
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
public class GetHistoryMessageRes implements Serializable {
    private static final long serialVersionUID = -125659990255750255L;
    /**
     * refId
     */
    private String refId;
    /**
     * totalCount
     */
    private Integer totalCount;
    /**
     * totalPages
     */
    private Integer totalPages;
    /**
     * page
     */
    private Integer page;
    /**
     * limit
     */
    private Integer limit;
    /**
     * messages
     */
    private List<MessageRes> messages;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class MessageRes implements Serializable {

        private static final long serialVersionUID = 2046614827967424757L;
        /**
         * playerName
         */
        private String  playerName;
        /**
         * 渠道id
         */
        private String channelId;
        /**
         * 任务id
         */
        private String missionId;
        /**
         * 任务名称
         */
        private String missionName;
        /**
         * 任务描述
         */
        private String missionDescription;
        /**
         * 奖品id
         */
        private String rewardId;
        /**
         * 奖品名称
         */
        private String rewardName;
        /**
         * 奖品描述
         */
        private String rewardDescription;
        /**
         * 平台id
         */
        private String pId;
        /**
         * 类型
         */
        private Integer type;
        /**
         * 创建时间
         */
        private Date createTime;
        /**
         * 领取详情
         */
        private RewardDetailsRes rewardDetails;
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ApiModel(description = "领取详情")
    public static class RewardDetailsRes implements Serializable {
        private static final long serialVersionUID = -2515942190714418574L;
        /**
         * 领取数量
         */
        private Integer count;
        /**
         * 星级
         */
        private Integer startLevel;
    }
}
