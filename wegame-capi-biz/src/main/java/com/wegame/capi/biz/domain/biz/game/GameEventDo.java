package com.wegame.capi.biz.domain.biz.game;

import com.wegame.capi.biz.domain.biz.operation.OperationEventDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
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

    private List<GameMultipleRecordDo<WD>> recordDoList;

    /**
     * 命中的干预事件
     */
    private OperationEventDo dynamicOperationEvent;
//
//    /**
//     * 渠道ID
//     */
//    private String channelId;
//
//    /**
//     * 游戏ID
//     */
//    private String gameId;

//    /**
//     * 干预事件记录ID(系统)
//     */
//    // private Long operatorRecordId;
}
