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
@ApiModel(description = "用户余额变动请求参数")
public class NotifyCreditChangeRequest implements Validate, Serializable {
    @ApiModelProperty(value = "渠道id", required = true)
    private String channelId;
    @ApiModelProperty(value = "玩家姓名", required = true)
    private String playerName;
    @ApiModelProperty(value = "hash", required = true)
    private String hash;
    @ApiModelProperty(value = "request id", required = true)
    private String refId;
    @ApiModelProperty(value = "时间", required = true)
    private Date createTime;
    @ApiModelProperty(value = "余额", required = true)
    private BigDecimal credit;
    /**
     * 1 The balance comes from deposit
     * 2 The balance comes from super jackpot
     * 3 The balance comes from lucky coin
     * 4 The balance comes from point settlement
     */
    @ApiModelProperty(value = "余额来源", required = true)
    private String sId;

    @Override
    public void validateParam() {
        AssertUtil.assertNotNull(channelId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(playerName, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(hash, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(refId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(createTime, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(credit, ErrorCode.ERR_PARAM);
    }
}
