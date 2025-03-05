package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "奖励派发使用记录")
public class BagRewardRecordMsgVo {

    private String msg;
}
