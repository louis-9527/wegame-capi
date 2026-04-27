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
    /**
     * 干预滚轮命中的配置倍数（与 {@link com.wegame.capi.biz.manager.operation.AbstractOperationEventManager#getWheelId} 解析的档位一致），
     * 用于 DOS 干预间隔规则；仅存于本局内存上下文，不入库。
     */
    private Double configMultiple;
}
