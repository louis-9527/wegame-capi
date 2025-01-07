package com.wegame.capi.client.domain.vo;

import com.wegame.base.Validate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * bet_record：投注记录分页vo
 *
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("投注记录")
@EqualsAndHashCode(callSuper = false)
public class BetRecordPageVo implements Validate, Serializable {
    private static final long serialVersionUID = -5835716727611741184L;
    @ApiModelProperty("主键ID")
    private String id;
    @ApiModelProperty("平台id")
    private String pId;
    @ApiModelProperty("渠道id")
    private String channelId;
    @ApiModelProperty("用户id")
    private String userId;
    @ApiModelProperty("玩家名称")
    private String playerName;
    @ApiModelProperty(value = "投注类型:NORMAL_BET,免费轮投注:FREE_SPINS_BET", required = true)
    private String betType;
    @ApiModelProperty(value = "投注子类型:NORMAL_BET:正常投注, BUY_FREE_SPINS_BET:购买免费轮投注, NON_BUY_FREE_SPINS_BET:非购买免费轮投注", required = true)
    private String betSubType;
    @ApiModelProperty("投注id（单钱包）")
    private String betId;
    @ApiModelProperty("投注流水号（对应单钱包refId）")
    private String betSerialNo;
    @ApiModelProperty("投注总金额 单位：分")
    private String betAmount;
    @ApiModelProperty("派彩总金额 单位：分")
    private String payOutAmount;
    @ApiModelProperty("状态 0进行中 1已完结 2无效")
    private Integer status;
    @ApiModelProperty("创建时间")
    private Long createTime;
    @ApiModelProperty("详情列表")
    private List<BetRecordDetailVo> detailVoList;
}

