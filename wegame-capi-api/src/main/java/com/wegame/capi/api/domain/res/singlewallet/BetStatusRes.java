package com.wegame.capi.api.domain.res.singlewallet;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 投注状态查询响应
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BetStatusRes implements Serializable {
    private static final long serialVersionUID = -63633463436518L;
    /**
     * 下注请求ID
     */
    private String refId;
    /**
     * 投注id
     */
    private String betId;
    /**
     * code
     */
    private Integer code;
    /**
     * msg
     */
    private String msg;
}
