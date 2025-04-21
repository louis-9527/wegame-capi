package com.wegame.capi.dao.mapper.domain.backend;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * game_maintain_config：游戏维护配置
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameMaintainConfigPo implements Serializable {
    private static final long serialVersionUID = -4751713411466769408L;
    /**
     * 主键ID/配置id
     */
    private Long id;
    /**
     * 游戏ID
     */
    private String gameId;
    /**
     * 维护开始时间
     */
    private Date maintainStartTime;
    /**
     * 维护结束时间
     */
    private Date maintainEndTime;
    /**
     * 启用状态 0:禁用 1:启用
     */
    private Integer enableStatus;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新人
     */
    private String updater;
    /**
     * 更新时间
     */
    private Date updateTime;

   ///////////////  above auto generated ///////////////
}

