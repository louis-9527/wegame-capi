package com.wegame.capi.biz.domain.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * 坐标do
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CoordinateDo implements Serializable {
    private static final long serialVersionUID = -2098749866555848L;

    /**
     * 行号
     */
    private Integer rowIndex;
    /**
     * 列号
     */
    private Integer columnIndex;

}
