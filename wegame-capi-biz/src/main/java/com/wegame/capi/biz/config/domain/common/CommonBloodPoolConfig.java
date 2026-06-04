package com.wegame.capi.biz.config.domain.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommonBloodPoolConfig implements Serializable {
    /**
     * 启用/禁用
     */
    private boolean enable = false;

}
