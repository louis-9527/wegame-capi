package com.wegame.capi.client.domain.request.promotion;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "历史FreeSpin请求模型")
public class FreeSpinHistoryOfPlayerRequest implements Validate, Serializable {
    private static final long serialVersionUID = 249902377416104598L;
    @ApiModelProperty(value = "Query page starting from 1", required = true)
    private Integer page;
    @ApiModelProperty(value = "Max limit of data return not more than 100", required = true)
    private Integer limit;
    @ApiModelProperty(value = "Query start time based on player acquire free spin time [Unix timestamps in seconds]", required = true)
    private Integer startTime;
    @ApiModelProperty(value = "Query end time based on player acquire free spin time [Unix timestamps in seconds]", required = true)
    private Integer endTime;

    @Override
    public void validateParam() {
        AssertUtil.assertNotNull(page, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(limit, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(startTime, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(endTime, ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(startTime < endTime, ErrorCode.ERR_PARAM);
    }
}
