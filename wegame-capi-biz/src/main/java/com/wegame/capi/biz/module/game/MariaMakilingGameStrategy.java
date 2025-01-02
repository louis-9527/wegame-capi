package com.wegame.capi.biz.module.game;

import com.google.common.collect.Sets;
import com.wegame.base.annotation.Authority;
import com.wegame.capi.biz.builder.GameInfoBuilder;
import com.wegame.capi.biz.domain.GameInfoDo;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.biz.module.probabilistic.strategy.MariaMakilingProbabilisticStrategy;
import com.wegame.capi.common.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolRuleTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.wegame.capi.common.enums.SymbolEnum.*;

@Slf4j
@Component("mariaMakilingGameStrategy")
public class MariaMakilingGameStrategy extends AbstractGameStrategy implements InitializingBean {

    @Autowired
    MariaMakilingProbabilisticStrategy mariaMakilingProbabilisticStrategy;

    @Override
    public GameInfoDo getGameInfo() {
        return GameInfoBuilder.build(GameTypeEnum.MARIA_MAKILING);
    }

    @Override
    public SymbolDo[][] generate() {
        // todo
        return new SymbolDo[0][];
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }


}
