package com.wegame.capi.client.domain.vo;

import com.wegame.capi.client.domain.vo.jackpot.JackpotRewardPoolVo;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("jackpot奖池")
@EqualsAndHashCode(callSuper = false)
public class JackpotResultVo {

    private String gameId;

    private String currency ="PHP";

    private BigDecimal miniAmount;

    private BigDecimal minorAmount;

    private BigDecimal majorAmount;

    private BigDecimal grandAmount;
}
