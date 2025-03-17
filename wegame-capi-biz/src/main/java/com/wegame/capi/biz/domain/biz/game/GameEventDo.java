package com.wegame.capi.biz.domain.biz.game;

import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GameEventDo<WD extends SymbolDo> {
    /**
     * 是否命中干预事件
     */
    private Boolean eventResult;

    private List<WheelDo<WD>> wheelDos;

    /**
     * 命中的干预事件
     */
    private DynamicOperationEventDo dynamicOperationEvent;

    /**
     * 渠道ID
     */
    private String channelId;

    /**
     * 游戏ID
     */
    private String gameId;

//    /**
//     * 干预事件记录ID(系统)
//     */
//    // private Long operatorRecordId;
}
