package com.wegame.capi.client.domain.vo;

import com.wegame.base.Validate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * bet_record：投注记录分页vo
 *
 * @author genereate
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("投注记录")
public class BetRecordVo implements Validate, Serializable {
    private static final long serialVersionUID = -5835716727611741184L;
    @ApiModelProperty("主键ID/投注记录id")
    private String id;
    @ApiModelProperty("parentID")
    private String parentId;
    @ApiModelProperty("用户id")
    private String userId;
    @ApiModelProperty("玩家id")
    private String playerId;
    @ApiModelProperty("玩家名称")
    private String playerName;
    @ApiModelProperty("0:无，2使用星卡，3预定投注")
    private Integer betPromotionType;
    @ApiModelProperty("投注金额 单位：分")
    private Long betAmount;
    @ApiModelProperty("支付金额 单位：分")
    private Long payAmount;
    @ApiModelProperty("派彩总金额 单位：分")
    private Long payOutAmount;
    @ApiModelProperty("是否达到最大倍数 true是，false否")
    private Boolean reachMaxMultiple;
    @ApiModelProperty("状态 0进行中 1已完结 2无效")
    private Integer status;
    @ApiModelProperty("扩展信息")
    private ExtendInfo extendInfo;
    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("免费轮处理状态")
    private String freeWheelHandleStatus;

    @ApiModelProperty("游戏记录列表")
    private List<Object> gameRecordList;

    @ApiModelProperty("免费轮信息")
    private FreeGameInfo freeGameInfo;

    /**
     * 免费轮信息
     */
    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class FreeGameInfo implements Serializable {
        private static final long serialVersionUID = 4154273029370246976L;

        @ApiModelProperty("免费轮初始倍率")
        private BigDecimal initialMultiple;

        @ApiModelProperty("免费轮最终倍率")
        private BigDecimal finalMultiple;

        @ApiModelProperty("免费轮初始次数")
        private int initialFreeGameCount;
    }

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
        @ApiModelProperty("（星卡）奖励图片url")
        private String rewardUrl;
        @ApiModelProperty("预定投注Id")
        private String campaignId;
    }
}

