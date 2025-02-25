package com.wegame.capi.dao.mapper.domain.business;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DynamicOperationQuery implements Serializable {
    private static final long serialVersionUID = 6879761477910223464L;
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

    private Long createTime;


    /**
     * 过期时间(大于)
     */
    private Long expiredTimeLt;

    private List<Integer> eventStatusList;

    private Integer reportStatus;

    private Integer limit;

    private Long createTimeLt;

    /**
     * 生效开始时间
     */
    private Long effectedStartTime;

    /**
     * 生效结束时间
     */
    private Long effectedEndTime;

    /**
     * 事件列表
     */
    private List<String> eventIdList;

    /**
     * 上一页最后一条的Id
     */
    private Long lastId;

    /**
     * 排序语句
     */
    private String orderByClause;
    /**
     * 排序语句
     */
    private Sort.Direction direction;

}
