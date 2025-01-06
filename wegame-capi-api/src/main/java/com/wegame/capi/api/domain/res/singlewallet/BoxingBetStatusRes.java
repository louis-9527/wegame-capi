package com.wegame.capi.api.domain.res.singlewallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 拳击投注状态响应
 *
 * @author jayni
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BoxingBetStatusRes implements Serializable {
    private static final long serialVersionUID = -63633463436518L;

    /**
     * 下注请求ID
     */
    private String refId;

    private String betId;

    /**
     * 注单状态码??
     */
    private Integer code;

    /**
     * Bet status message. See Bet Status Code
     * 每一个状态码进行解释??
     * code
     * Description
     * 1
     * received bet request
     * 2
     * completed bet request
     * 3
     * received payout request
     * 4
     * completed payout request
     * 5
     * received cancel request
     * 6
     * completed cancel request
     * 7
     * data not found
     */
    private String msg;


}
