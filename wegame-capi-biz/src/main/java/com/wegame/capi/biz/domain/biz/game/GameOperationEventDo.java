package com.wegame.capi.biz.domain.biz.game;

import com.wegame.capi.biz.domain.biz.operation.OperationEventDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GameOperationEventDo implements Serializable {
    /**
     * 是否命中干预事件
     */
    private boolean eventResult;
    /**
     * 命中的干预事件
     */
    private OperationEventDo operationEvent;
    /**
     * 命中那条倍数ID
     */
    private Long configId;
}
