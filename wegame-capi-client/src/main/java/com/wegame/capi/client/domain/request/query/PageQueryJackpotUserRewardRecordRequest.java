package com.wegame.capi.client.domain.request.query;

import com.wegame.base.domain.PageQueryRequest;
import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;


/**
 * 分页查询用户jackpot中奖记录请求参数
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "分页查询用户jackpot中奖记录请求参数", parent = PageQueryRequest.class)
public class PageQueryJackpotUserRewardRecordRequest extends PageQueryRequest implements Validate {

    @ApiModelProperty(value = "类型列表 ： 类型 1：grand 2:major 3:minor 4:mini 5:betBonus", required = true)
    private List<Integer> rewardPoolTypeList;


    @Override
    public void validateParam() {
        AssertUtil.assertNotEmpty(rewardPoolTypeList, ErrorCode.ERR_PARAM);
    }

    @Override
    public void validateBiz() {

    }
}

