package com.wegame.capi.dao.mapper.domain.business;

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
public class DynamicOperationConfigPo implements Serializable {
    private static final long serialVersionUID = 3508760048909770673L;
    /**
     * 主键ID
     */
    @Id
    @Field("_id")
    private Long id;

    /**
     * 游戏ID
     */
    private String gameId;

    /**
     * 0:正常游戏
     * 1:购买免费游戏
     */
    private Integer configType;

    /**
     * 倍数
     */
    private Integer multiple;

    /**
     * 配置明细
     */
    private String detail;
}
