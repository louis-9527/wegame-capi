package com.wegame.capi.dao.repository.domain.operation;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;

/**
 * 干预事件查询模型
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OperationEventQuery implements Serializable {
    private static final long serialVersionUID = 6879761477910223464L;
    /**
     * id
     */
    private Long id;
    /**
     * 事件干预类型
     */
    private Integer eventKind;
    /**
     * 游戏ID
     */
    private String gameId;
    /**
     * 玩家名称
     */
    private String playerName;
    /**
     * 事件状态
     */
    private Integer eventStatus;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 过期时间小于
     */
    private Long expiredTimeLt;
    /**
     * 上报状态
     */
    private Integer reportStatus;
    /**
     * limit
     */
    private Integer limit;
    /**
     * 小于创建时间
     */
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
     * 小于最后一条的Id
     */
    private Long ltLastId;
    /**
     * 排序语句
     */
    private String orderByClause;
    /**
     * 排序方向
     */
    private Sort.Direction direction;
}
