package com.wegame.capi.biz.domain.biz;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * bet_record：投注记录
 *
 * @author generate
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BetRecordDo<T> implements Serializable {
    private static final long serialVersionUID = -3514324297343525888L;
    /**
     * 主键ID
     */
    private Long id;
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
     *
     * @see com.wegame.capi.client.enums.BetTypeEnum
     */
    private String betType;
    /**
     * 投注子类型
     *
     * @see com.wegame.capi.client.enums.BetSubTypeEnum
     */
    private String betSubType;
    /**
     * 投注营销类型
     *
     * @see com.wegame.capi.common.enums.BetPromotionTypeEnum
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
     * 派彩总金额 单位：分
     */
    private Long payOutAmount;
    /**
     * 是否达到最大倍数
     */
    private Boolean reachMaxMultiple;
    /**
     * 投注状态 0进行中 1已完结 2无效
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
    /**
     * 游戏记录列表
     */
    private List<T> gameRecordList;
    /**
     * 过期时间
     */
    private Date expireAt;

    /**
     * 扩展信息
     */
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
        // ------------------------------------------------------------------------------------------------------------
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

