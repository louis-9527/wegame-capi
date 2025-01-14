package com.wegame.capi.api.domain.res.wegamego;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 最大最小区间req
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MinMaxRandomRes implements Serializable {
    private static final long serialVersionUID = 4567895678567L;
    private Integer result;
}
