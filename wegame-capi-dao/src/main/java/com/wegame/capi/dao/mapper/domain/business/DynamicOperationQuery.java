package com.wegame.capi.dao.mapper.domain.business;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DynamicOperationQuery implements Serializable {
    private static final long serialVersionUID = 6879761477910223464L;
    @Id
    @Field("_id")
    private Long id;

    /**
     * 干预事件ID
     */
    private String eventId;

    /**
     * 事件干预类型
     */
    private Integer eventKind;
    /**
     * 玩家名称
     */
    private String playerName;

    /**
     * 游戏ID
     */
    private String operatorGameId;

    /**
     * 事件状态
     */
    private Integer eventStatus;

}
