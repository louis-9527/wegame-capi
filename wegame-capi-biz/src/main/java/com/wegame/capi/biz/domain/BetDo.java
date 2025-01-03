package com.wegame.capi.biz.domain;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
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
@EqualsAndHashCode(callSuper = false)
public class BetDo<T> implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 游戏Id
     */
    private String gameId;
    /**
     * 游戏类型
     */
    private GameTypeEnum gameType;
    /**
     * 投注类型
     */
    private BetTypeEnum betType;
    /**
     * 投注子类型
     */
    private BetSubTypeEnum betSubType;
    /**
     * 投注详情
     */
    private T betDetail;


    /**
     * Mysterious正常投注请求参数
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "")
    public class MysteriousNormalBetDo implements Validate, Serializable {
        @ApiModelProperty(value = "投注金额", required = true)
        private Long betAmount;

        @Override
        public void validateParam() {
            AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(betAmount > 0L, ErrorCode.ERR_PARAM);
        }
    }

    /**
     * Mysterious购买免费轮投注请求参数
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "")
    public class MysteriousBuyFreeSpinsBetDo implements Validate, Serializable {
        @ApiModelProperty(value = "投注金额", required = true)
        private Long betAmount;
        @ApiModelProperty(value = "支付金额", required = true)
        private Long payAmount;

        @Override
        public void validateParam() {
            AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(betAmount > 0L, ErrorCode.ERR_PARAM);
            AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(payAmount > 0L, ErrorCode.ERR_PARAM);
        }
    }

    /**
     * Mysterious非购买免费轮投注请求参数
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "")
    public class MysteriousNonBuyFreeSpinsBetDo implements Validate, Serializable {
        @ApiModelProperty(value = "游戏记录Id", required = true)
        private String gameRecordId;

        @Override
        public void validateParam() {
            AssertUtil.assertStrNotEmpty(gameRecordId, ErrorCode.ERR_PARAM);
        }
    }

}

