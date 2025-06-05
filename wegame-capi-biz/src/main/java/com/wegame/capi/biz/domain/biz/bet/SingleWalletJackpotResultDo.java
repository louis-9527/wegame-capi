package com.wegame.capi.biz.domain.biz.bet;

import com.wegame.base.enums.ErrorCode;
import com.wegame.capi.common.enums.BetStatusEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SingleWalletJackpotResultDo implements Serializable {
    private Boolean success;
    /**
     * status
     */
    private Integer status;
    /**
     * message
     */
    private String message;

    private resultData resultData;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class resultData{
        private String  playerName;
        private Long credit;
        private String refId;
    }
}
