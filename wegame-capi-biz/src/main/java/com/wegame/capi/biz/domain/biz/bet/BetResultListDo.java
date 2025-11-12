package com.wegame.capi.biz.domain.biz.bet;

import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BetResultListDo <R extends ToVoConverter> {
    /**
     * 游戏记录id
     */
    private Long betRecordId;
    /**
     * 游戏Id
     */
    private String gameId;
    /**
     * 投注类型
     */
    private BetTypeEnum betType;
    /**
     * 投注子类型
     */
    private BetSubTypeEnum betSubType;
    /**
     * 余额
     */
    private Long credit;
    /**
     * 合计中奖总金额
     */
    private Long overallRewardAmount;
    /**
     * 投注结果
     */
    private List<R> resultDetail;
}
