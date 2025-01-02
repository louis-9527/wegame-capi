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
public class GameBetResultDo<R> {
    /**
     * 游戏Id
     */
    private String gameId;
    private GameBetTypeEnum betType;
    private GameBetSubTypeEnum betSubType;
    private Long gameRecordId;
    public R resultDetail;
}
