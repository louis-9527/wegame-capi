package com.wegame.capi.biz.domain.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 倍数do
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MultipleDo implements Serializable {
    private static final long serialVersionUID = -2263345345345648L;

    /**
     * 游戏id
     */
    private String gameId;

}
