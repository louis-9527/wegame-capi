package com.wegame.capi.biz.module.probabilistic;

import com.wegame.capi.infrastructure.config.nacos.NacosMariaMakilingProbabilisticConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * MariaMakiling概率策略
 */
@Component
public class MariaMakilingProbabilisticStrategy extends AbstractProbabilisticStrategy {

    @Autowired
    NacosMariaMakilingProbabilisticConfig nacosMysteriousProbabilisticConfig;

    @Override
    public Map<String, List<BigDecimal>> getProbabilisticConfigMap() {
        return nacosMysteriousProbabilisticConfig.getProbabilisticConfigMap();
    }
}
