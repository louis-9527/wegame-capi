package com.wegame.capi.biz.domain.biz.operation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BetRecordTemplateDo<T> implements Serializable {
    private static final long serialVersionUID = -4933961979247796069L;
    /**
     * id
     */
    private Long id;
    /**
     * 游戏ID
     */
    private String gameId;
    /**
     * 投注类型
     */
    private String betType;
    /**
     * 投注子类型
     */
    private String betSubType;
    /**
     * 倍数
     */
    private Double multiple;
    /**
     * 模版类型 common
     */
    private String templateType;
    /**
     * hash
     */
    private String hash;

    ///////////////  above auto generated ///////////////
    /**
     * 游戏记录列表
     */
    private List<T> gameRecordList;
}
