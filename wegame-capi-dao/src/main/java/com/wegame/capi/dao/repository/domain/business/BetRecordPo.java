package com.wegame.capi.dao.repository.domain.business;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * bet_record：投注记录
 *
 * @author genereate
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BetRecordPo implements Serializable {
    private static final long serialVersionUID = -633389003068449792L;
    /**
     * 主键ID
     */
    @Id
    @Field("_id")
    private Long id;
    /**
     * parentID
     */
    private Long parentId;
    /**
     * 平台id
     */
    private String pId;
    /**
     * 渠道id
     */
    private String channelId;
    /**
     * 游戏id
     */
    private String gameId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 玩家id
     */
    private String playerId;
    /**
     * 玩家名称
     */
    private String playerName;
    /**
     * 投注类型
     */
    private String betType;
    /**
     * 投注子类型
     */
    private String betSubType;
    /**
     * 投注营销类型
     */
    private Integer betPromotionType;
    /**
     * 投注id（单钱包）
     */
    private String betId;
    /**
     * 投注流水号（对应单钱包refId）
     */
    private String betSerialNo;
    /**
     * 投注金额 单位：分
     */
    private Long betAmount;
    /**
     * 支付金额 单位：分
     */
    private Long payAmount;
    /**
     * 是否达到最大倍数
     */
    private Boolean reachMaxMultiple;
    /**
     * 派彩金额 单位：分
     */
    private Long payOutAmount;
    /**
     * 营销标识 0否 1是
     */
    private Integer marketFlag;
    /**
     * 状态 0进行中 1已完结 2无效
     */
    private Integer status;
    /**
     * 投注时间
     */
    private Long betTime;
    /**
     * jackpot
     */
    private Jackpot jackpot;
    /**
     * 扩展信息
     */
    private ExtendInfo extendInfo;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新人
     */
    private String updater;
    /**
     * 更新时间
     */
    private Long updateTime;

    ///////////////  above auto generated ///////////////


    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class ExtendInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * 干预事件Id(当这笔订单属于干预订单时生成,对于 EventId 需要传给单钱包)
         */
        private String ieId;
        /**
         * （星卡）奖励图片url
         */
        private String rewardUrl;
        /**
         * 预定投注Id
         */
        private String campaignId;
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class Jackpot implements Serializable {
        /**
         * grand奖池贡献金额
         */
        private Long grandContributionAmount;
        /**
         * major奖池贡献金额
         */
        private Long majorContributionAmount;

        /**
         * minor奖池贡献金额
         */
        private Long minorContributionAmount;
        /**
         * mini奖池贡献金额
         */
        private Long miniContributionAmount;
    }
}

