package com.wegame.capi.common.utils;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * rtp util
 */
public class RtpUtil {

    private static final BigDecimal DEFAULT_RTP = BigDecimal.valueOf(97).setScale(4, RoundingMode.FLOOR);

    /**
     * 计算rtp
     * 返回rtp百分比,保留4位小数
     *
     * @param betAmount    betAmount
     * @param payoutAmount payoutAmount
     * @return rtp
     */
    public static BigDecimal calculateRtp(Long betAmount, Long payoutAmount) {
        AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM, "betAmount is null");
        AssertUtil.assertNotNull(payoutAmount, ErrorCode.ERR_PARAM, "payoutAmount is null");
        AssertUtil.assertTrue(betAmount >= 0, ErrorCode.ERR_PARAM, "betAmount is must than 0");
        AssertUtil.assertTrue(payoutAmount >= 0, ErrorCode.ERR_PARAM, "payoutAmount is must than or equals 0");
        // 派彩金额0,则返回0.
        if (payoutAmount == 0) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.FLOOR);
        }
        // 投注金额为0，则返回100.特别说明：当派彩金额为0的时候，是刚上线用户刚进来时，给一个稍微合适的值即可
        if (betAmount == 0) {
            return DEFAULT_RTP;
        }

        BigDecimal betAmountDecimal = new BigDecimal(betAmount);
        BigDecimal payoutAmountDecimal = new BigDecimal(payoutAmount);
        return payoutAmountDecimal.divide(betAmountDecimal, 6, RoundingMode.FLOOR)
                .multiply(BigDecimal.valueOf(100)).setScale(4, RoundingMode.FLOOR);

    }


}
