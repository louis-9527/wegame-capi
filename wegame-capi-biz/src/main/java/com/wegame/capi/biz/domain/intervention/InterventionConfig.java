package com.wegame.capi.biz.domain.intervention;


import java.math.BigDecimal;

/**
 * 统一干预config共性行文
 */
public interface InterventionConfig {
   default BigDecimal getTriggerProbability(){
        return null;
    }

    default String getRepeatType(){
        return null;
    }

    default String getRtpName(){
        return null;
    }

    default String getBackstopRtpName(){
        return null;
    }
}
