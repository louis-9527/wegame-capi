package com.wegame.capi.biz.service.probabilistic.strategy;

import com.wegame.capi.infrastructure.config.nacos.NacosMariaMakilingProbabilisticConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 元素概率服务
 */
@Component("mariaMakilingProbabilisticService")
public class MariaMakilingProbabilisticStrategy extends AbstractProbabilisticStrategy {

    @Autowired
    NacosMariaMakilingProbabilisticConfig nacosMysteriousProbabilisticConfig;

    @Override
    public Map<String, List<BigDecimal>> getProbabilisticConfigMap() {
        return nacosMysteriousProbabilisticConfig.getProbabilisticConfigMap();
    }
}
