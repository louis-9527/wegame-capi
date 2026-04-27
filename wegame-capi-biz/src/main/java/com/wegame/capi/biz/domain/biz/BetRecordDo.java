package com.wegame.capi.biz.domain.biz;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wegame.capi.client.enums.FreeWheelHandleStatusEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class BetRecordDo<T> implements Serializable {
    private static final long serialVersionUID = -3514324297343525888L;
    /**
     * 主键ID
     */
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
     * 业务干预类型
     *
     * @see com.wegame.capi.common.enums.BizInterfereTypeEnum
     */
    private Integer bizInterfereType;
    /**
     * 业务干预子类型
     *
     * @see com.wegame.capi.common.enums.BizInterfereSubTypeEnum
     */
    private Integer bizInterfereSubType;
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
     * 库存信息
     */
    private Inventory inventory;
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
    /**
     * 免费轮处理状态
     *
     * @see FreeWheelHandleStatusEnum
     */
    private String freeWheelHandleStatus;

    /**
     * 模板类型
     */
    private String templateType;

    /**
     * 下一局模板id
     *
     */
    private Long nextTemplateId;

    ///////////////  above auto generated ///////////////
    /**
     * 游戏记录列表
     */
    private List<T> gameRecordList;
    /**
     * 投注后用户余额（厘）
     */
    private Long credit;

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
         * grand预留奖池贡献金额
         */
        private Long grandSupContributionAmount;


        /**
         * major奖池贡献金额
         */
        private Long majorContributionAmount;

        /**
         * major预留奖池贡献金额
         */
        private Long majorSupContributionAmount;


        /**
         * minor奖池贡献金额
         */
        private Long minorContributionAmount;

        /**
         * minor预留奖池贡献金额
         */
        private Long minorSupContributionAmount;


        /**
         * mini奖池贡献金额
         */
        private Long miniContributionAmount;

        /**
         * mini预留奖池贡献金额
         */
        private Long miniSupContributionAmount;

        /**
         * 根据奖池类型和是否进入jackpot池设置贡献金额
         *
         * @param type      奖池类型
         * @param amount    贡献金额
         * @param toJackpot true: 进入jackpot池, false: 进入reserved池
         */
        public void setContribution(JackpotRewardPoolTypeEnum type, Long amount, boolean toJackpot) {
            if (Objects.isNull(type) || Objects.isNull(amount)) {
                return;
            }
            switch (type) {
                case grand:
                    if (toJackpot) {
                        this.grandContributionAmount = amount;
                    } else {
                        this.grandSupContributionAmount = amount;
                    }
                    break;
                case major:
                    if (toJackpot) {
                        this.majorContributionAmount = amount;
                    } else {
                        this.majorSupContributionAmount = amount;
                    }
                    break;
                case minor:
                    if (toJackpot) {
                        this.minorContributionAmount = amount;
                    } else {
                        this.minorSupContributionAmount = amount;
                    }
                    break;
                case mini:
                    if (toJackpot) {
                        this.miniContributionAmount = amount;
                    } else {
                        this.miniSupContributionAmount = amount;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class Inventory implements Serializable {
        /**
         * 库存类型
         */
        private Integer inventoryType;
        /**
         * 库存池名称
         */
        private String inventoryPoolName;
        /**
         * 库存贡献金额
         */
        private Long inventoryContributionAmount;
    }

}

