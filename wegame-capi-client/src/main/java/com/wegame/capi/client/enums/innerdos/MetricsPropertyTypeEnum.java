package com.wegame.capi.client.enums.innerdos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;


/**
 * 指标类型枚举
 */
@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum MetricsPropertyTypeEnum {
    FIXED,
    EXTEND;
}
