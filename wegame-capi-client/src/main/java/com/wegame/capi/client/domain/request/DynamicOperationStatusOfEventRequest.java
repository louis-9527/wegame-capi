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
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "获取干预事件结果")
public class DynamicOperationStatusOfEventRequest implements Validate,Serializable {
    private static final long serialVersionUID = 4774663284527334032L;

    /**
     * 干预事件IDs (最多为10)
     */
    @ApiModelProperty(value = "干预事件IDs (最多为10)", required = true)
    private List<String> eventIds;

    /**
     * 游戏ID
     */
    @ApiModelProperty(value = "游戏ID", required = true)
    private String gameId;

    @ApiModelProperty(value = "开始时间", required = true)
    private Date startTime;

    @ApiModelProperty(value = "结束时间", required = true)
    private Date endTime;

    @Override
    public void validateParam() {
        AssertUtil.assertNotEmpty(eventIds, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(gameId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(startTime, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(endTime, ErrorCode.ERR_PARAM);
    }


}
