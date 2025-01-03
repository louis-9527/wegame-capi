package com.wegame.capi.biz.domain.biz.odds;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 赔率do
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
//@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OddsResultDo implements Serializable {
    private static final long serialVersionUID = -223245454848L;


}
