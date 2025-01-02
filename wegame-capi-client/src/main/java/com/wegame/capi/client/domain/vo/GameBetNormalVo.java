package com.wegame.capi.client.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("正常投注返回")
@EqualsAndHashCode(callSuper = false)
public class GameBetNormalVo implements Serializable {
    private static final long serialVersionUID = -4792907227811616042L;
    private Integer payOutAmountType;
    private Long payOutTotalAmount;
    private Integer hitFreeSpinFlag;
    private Integer[][] spinsResult;
    private List<GameBetPayOutDetailVo> payOutDetail;

}
