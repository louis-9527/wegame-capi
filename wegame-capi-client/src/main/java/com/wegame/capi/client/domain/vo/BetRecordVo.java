package com.wegame.capi.client.domain.vo;

import com.wegame.base.Validate;
import com.wegame.capi.client.domain.vo.ibongadarna.IbongAdarnaGameRecordVo;
import com.wegame.capi.client.domain.vo.mariamaking.MariaMakilingGameRecordVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("投注记录")
@EqualsAndHashCode(callSuper = false)
public class BetRecordVo implements Validate, Serializable {
    private static final long serialVersionUID = -5835716727611741184L;
    @ApiModelProperty("主键ID/投注记录id")
    private String id;
    @ApiModelProperty("用户id")
    private String userId;
    @ApiModelProperty("玩家名称")
    private String playerName;
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
    @ApiModelProperty("创建时间")
    private Long createTime;
    @ApiModelProperty("游戏记录列表")
    private List<Object> gameRecordList;


}

