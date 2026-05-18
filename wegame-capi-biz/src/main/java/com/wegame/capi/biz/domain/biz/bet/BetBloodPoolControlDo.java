package com.wegame.capi.biz.domain.biz.bet;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BetBloodPoolControlDo implements Serializable {
    private static final long serialVersionUID = -226630893710848L;
    ///**
    // * 匹配的血池种类
    // */
    //private int matchBloodPoolCategory;
    ///**
    // * 匹配的血池等级
    // */
    //private String matchBloodPoolLevel;
    /**
     * 是否走血池控制（控制单钱包）
     */
    private boolean enableFundPool;
}

