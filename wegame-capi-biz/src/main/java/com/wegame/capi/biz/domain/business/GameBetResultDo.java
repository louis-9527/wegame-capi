package com.wegame.capi.biz.domain.business;

import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
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
    private BetTypeEnum betType;
    private BetSubTypeEnum betSubType;
    private Long gameRecordId;
    public R resultDetail;
}
