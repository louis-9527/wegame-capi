package com.wegame.capi.dao.mapper.domain.business;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@ToString
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
     * 投注类型
     */
    private String betType;
    /**
     * 倍数
     */
    private Double multiple;
}
