package com.wegame.capi.client.domain.vo;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 游戏业务事件模型
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameBizEventVo implements Serializable {
    private static final long serialVersionUID = -2678234710848L;
    /**
     * 事件排序
     */
    private Integer sort;
    /**
     * 事件类型
     *
     * @see BizEventEnum
     */
    private Integer bizEventType;
    /**
     * 事件影响的倍率
     */
    private BigDecimal eventMultiple;
    /**
     * 变动元素
     */
    private String changeToSymbol;
    /**
     * 坐标
     */
    private List<IndexVo> indexVoList;
    /**
     * ext
     */
    private Object extInfo;
}

