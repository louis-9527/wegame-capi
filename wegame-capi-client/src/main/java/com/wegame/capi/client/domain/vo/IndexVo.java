package com.wegame.capi.client.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"rowIndex", "columnIndex"}, callSuper = false)
public class IndexVo {
    /**
     * 行号
     */
    private int rowIndex;
    /**
     * 列号
     */
    private int columnIndex;
}
