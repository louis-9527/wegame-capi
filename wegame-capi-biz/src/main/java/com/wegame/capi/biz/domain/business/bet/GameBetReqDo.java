package com.wegame.capi.biz.domain.business.bet;

import com.wegame.capi.common.enums.GameBetSubTypeEnum;
import com.wegame.capi.common.enums.GameBetTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameBetReqDo<T> {
    /**
     * 游戏Id
     */
    private String gameId;

    private GameBetTypeEnum betType;

    private GameBetSubTypeEnum betSubType;

    private T betDetail;

}
