package com.wegame.capi.api.domain.res.singlewallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * jackpot(派奖)响应
 *
 * @author jayni
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class JackpotRes implements Serializable {

    private static final long serialVersionUID = -63639765443418L;
    /**
     * 玩家名称
     */
    private String playerName;
    /**
     * 玩家钱包余额
     */
    private BigDecimal credit;

    /**
     * 请求的请求ID
     */
    private String refId;

}
