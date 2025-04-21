package com.wegame.capi.client.domain.request.promotion;

import com.wegame.base.Validate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "历史FreeSpin请求模型")
public class CreateFreeSpinRequest implements Validate, Serializable {
    private static final long serialVersionUID = 6074772096926787533L;
    @ApiModelProperty(value = "此交易的唯一参考ID", required = true)
    private String refId;
    @ApiModelProperty(value = "游戏供应商ID", required = true)
    private String providerId;
    @ApiModelProperty(value = "渠道ID", required = true)
    private String platformId;
    @ApiModelProperty(value = "开始日期（格式：YYYY-MM-DDTHH:mm:ssZ）", required = true)
    private String startDate;
    @ApiModelProperty(value = "结束日期（格式：YYYY-MM-DDTHH:mm:ssZ）", required = true)
    private String endDate;
    @ApiModelProperty(value = "活动名称", required = true)
    private String eventName;
    @ApiModelProperty(value = "游戏编号列表，示例: [\"test\",\"GameTest\"]", required = true)
    private List<String> gameIDList;
    @ApiModelProperty(value = "帐号列表，自动绑定到该活动，示例: [\"plater\",\"p1\",\"p2\"]", required = false)
    private List<String> playerList;
    @ApiModelProperty(value = "免费旋转次数", required = true)
    private Integer freeSpinTimes;
    @ApiModelProperty(value = "指定投注额，未指定时使用provider默认值", required = false)
    private BigDecimal betValue;
    @ApiModelProperty(value = "可参与玩家上限", required = false)
    private Integer maxPlayerCount;
    @ApiModelProperty(value = "查询页码，从1开始", required = true)
    private Integer page;

    @Override
    public void validateParam() {
    }
}
