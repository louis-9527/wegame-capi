package com.wegame.capi.biz.domain.business.bet;

import com.wegame.base.enums.BooleanEnum;
import com.wegame.capi.common.enums.BetSubTypeEnum;
import com.wegame.capi.common.enums.BetTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameBetRecordDo {
    private Long id;
    private String pId;
    private String channelId;
    /**
     * 游戏Id
     */
    private String gameId;

    private String betId;
    /**
     * 投注流水号（对应单钱包refId）
     */
    private String betSerialNo;

    private Long betAmount;
    /**
     * 派彩金额 单位：分
     */
    private Long payOutAmount;

    private BetTypeEnum betType;

    private BetSubTypeEnum betSubType;
    /**
     * 营销标识 0否 1是
     */
    private BooleanEnum marketFlag;
    /**
     * 状态 0进行中 1已完结 2无效
     */
    private Integer status;


    private List<GameBetResultDetailDo> resultDetails;

    /**
     * 玩家余额
     */
    private Long credit;






    
}
