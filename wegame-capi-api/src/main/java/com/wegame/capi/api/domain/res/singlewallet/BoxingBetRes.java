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
 * 拳击投注响应
 *
 * @author jayni
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BoxingBetRes implements Serializable {

    private static final long serialVersionUID = -63633467303418L;
    /**
     * 玩家名称
     */
    private String playerName;
    /**
     * 下注id
     */
    private String betId;
    /**
     * 玩家钱包余额
     */
    private BigDecimal credit;

    /**
     * 请求的请求ID
     */
    private String refId;

    /**
     * 错误请求数据
     */
    private List<ErrorMessages> errorMessages;

    @Data
    static class ErrorMessages{
        private String property;
        private String message;
    }

    /**
     * credit 转换成分
     *
     * @return 分
     */
    public Long getCreditToCent() {
        BigDecimal credit = Optional.ofNullable(this.credit).orElse(BigDecimal.ZERO);
        return credit.multiply(new BigDecimal("100")).longValue();
    }
}
