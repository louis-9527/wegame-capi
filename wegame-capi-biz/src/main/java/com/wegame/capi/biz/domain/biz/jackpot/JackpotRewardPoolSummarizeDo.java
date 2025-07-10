package com.wegame.capi.biz.domain.biz.jackpot;

import com.wegame.base.Validate;
import com.wegame.capi.client.domain.vo.jackpot.JackpotRewardPoolVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("jackpot奖池摘要信息")
@EqualsAndHashCode(callSuper = false)
public class JackpotRewardPoolSummarizeDo implements Validate, Serializable {
    /**
     * 奖池总金额
     */
    private Long totalJackpotAmount;

    /**
     * grand奖池信息
     */
    private JackpotRewardPoolDo grand;

    /**
     * major奖池信息
     */
    private JackpotRewardPoolDo major;

    /**
     * minor奖池信息
     */
    private JackpotRewardPoolDo minor;

    /**
     * mini奖池信息
     */
    private JackpotRewardPoolDo mini;
}
