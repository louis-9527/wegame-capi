package com.wegame.capi.api.domain.res.singlewallet;

import com.wegame.capi.common.enums.GameBetStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 拳击投注状态响应
 *
 * @author jayni
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BetStatusRes implements Serializable {
    private static final long serialVersionUID = -63633463436518L;

    /**
     * 下注请求ID
     */
    private String refId;

    private String betId;

    private Integer code;

    private String msg;


}
