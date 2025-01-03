package com.wegame.capi.biz.domain.biz.bet;

import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
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
public class BetResultDo<R> implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 游戏记录id
     */
    private Long gameRecordId;
    /**
     * 游戏Id
     */
    private String gameId;
    /**
     * 投注类型
     */
    private BetTypeEnum betType;
    /**
     * 投注子类型
     */
    private BetSubTypeEnum betSubType;

    /**
     * 余额
     */
    private Long credit;
    /**
     * 投注结果
     */
    private R resultDetail;

    //    @Data
    //    @NoArgsConstructor
    //    @AllArgsConstructor
    //    public class MysteriousNormalBetResultDo implements Validate, Serializable {
    //        @ApiModelProperty(value = "投注金额", required = true)
    //        private Long betAmount;
    //    }
    //
    //    /**
    //     * Mysterious购买免费轮投注结果
    //     */
    //    @Data
    //    @NoArgsConstructor
    //    @AllArgsConstructor
    //    public class MysteriousBuyFreeSpinsBetResultDo implements Validate, Serializable {
    //        @ApiModelProperty(value = "投注金额", required = true)
    //        private Long betAmount;
    //        @ApiModelProperty(value = "支付金额", required = true)
    //        private Long payAmount;
    //    }
    //
    //    /**
    //     * Mysterious非购买免费轮投注结果
    //     */
    //    @Data
    //    @NoArgsConstructor
    //    @AllArgsConstructor
    //    public class MysteriousNonBuyFreeSpinsBetResultDo implements Validate, Serializable {
    //        /**
    //         * 游戏记录Id
    //         */
    //        private String gameRecordId;
    //    }
}

