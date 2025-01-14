package com.wegame.capi.api.domain.req.wegamego;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 一次性生成多个最大最小区间req
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MultiMinMaxRandomReq implements Serializable {
    private static final long serialVersionUID = 45678554444567L;
    /**
     * 期望生成数量
     */
    private Integer count;
    /**
     * 区间最小
     */
    private Integer min;
    /**
     * 区间最大
     */
    private Integer max;
}
