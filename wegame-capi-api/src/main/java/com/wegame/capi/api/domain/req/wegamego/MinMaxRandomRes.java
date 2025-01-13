package com.wegame.capi.api.domain.req.wegamego;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.PropertyValue;

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
    private String error;
    private String omitempty;
}
