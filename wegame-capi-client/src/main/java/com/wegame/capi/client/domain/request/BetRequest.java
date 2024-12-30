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

/**
 * 投注请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "投注请求参数")
public class BetRequest implements Validate, Serializable {
    @ApiModelProperty(value = "游戏Id", required = true)
    private Long gameId;
    @ApiModelProperty(value = "投注类型：下注类型:0正常投注,1非购买免费轮投注,2:购买免费轮投注", required = true)
    private Integer betType;
    @ApiModelProperty(value = "投注明细", required = false)
    private Object betDetail;

    @Override
    public void validateParam() {
        AssertUtil.assertNotNull(gameId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(betType, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(betDetail, ErrorCode.ERR_PARAM);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "Mysterious正常投注请求参数")
    public class MysteriousNormalBetRequest implements Validate, Serializable {
        @ApiModelProperty(value = "投注金额", required = true)
        private Long betAmount;
        @ApiModelProperty(value = "是否自动投注", required = true)
        private Boolean autoBet;

        @Override
        public void validateParam() {
            AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
            AssertUtil.assertNotNull(autoBet, ErrorCode.ERR_PARAM);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "Mysterious非购买免费轮投注请求参数")
    public class MysteriousNonBuyFreeSpinsBetRequest implements Validate, Serializable {
        @ApiModelProperty(value = "游戏记录Id", required = true)
        private String gameRecordId;

        @Override
        public void validateParam() {
            AssertUtil.assertStrNotEmpty(gameRecordId, ErrorCode.ERR_PARAM);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "Mysterious购买免费轮投注请求参数")
    public class MysteriousBuyFreeSpinsBetRequest implements Validate, Serializable {
        @ApiModelProperty(value = "投注金额/购买金额", required = true)
        private Long betAmount;

        @Override
        public void validateParam() {
            AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
        }
    }
}
