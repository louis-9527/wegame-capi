package com.wegame.capi.dao.mapper.domain.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

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


    private String betType;

    /**
     * 倍数
     */
    private Double multiple;
}
