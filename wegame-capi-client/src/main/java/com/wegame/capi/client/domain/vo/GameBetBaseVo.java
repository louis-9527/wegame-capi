package com.wegame.capi.client.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("正常投注返回")
@EqualsAndHashCode(callSuper = false)
public class GameBetBaseVo<T> implements Serializable {
    private static final long serialVersionUID = -3682602438304369095L;
    private String gameRecordId;
    private String gameId;
    private Integer betType;
    private Integer betSubType;
    private Long credit;
    private T resultDetail;
}
