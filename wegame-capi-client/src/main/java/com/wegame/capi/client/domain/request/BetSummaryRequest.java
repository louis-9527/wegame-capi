package com.wegame.capi.client.domain.request;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "对账请求参数")
public class BetSummaryRequest implements Validate, Serializable {
    @ApiModelProperty(value = "渠道id", required = true)
    private String channelId;
    @ApiModelProperty(value = "hash", required = true)
    private String hash;
    @ApiModelProperty(value = "request id", required = true)
    private String refId;
    @ApiModelProperty(value = "时间", required = true)
    private Date createTime;
    @ApiModelProperty(value = "0: Bet time; 1: Settlement time", required = true)
    private Integer timeType;
    @ApiModelProperty(value = "开始时间", required = true)
    private Long startTime;
    @ApiModelProperty(value = "结束时间", required = true)
    private Long endTime;

    @Override
    public void validateParam() {
        AssertUtil.assertNotNull(channelId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(hash, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(refId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(createTime, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(timeType, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(startTime, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(endTime, ErrorCode.ERR_PARAM);
    }
}
