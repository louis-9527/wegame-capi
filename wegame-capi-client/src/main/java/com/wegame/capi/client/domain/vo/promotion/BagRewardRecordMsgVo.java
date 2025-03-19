package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "奖励派发使用记录")
public class BagRewardRecordMsgVo implements Serializable {

    private String msg;
}
