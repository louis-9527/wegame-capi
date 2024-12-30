package com.wegame.capi.biz.handler.maria;

import com.wegame.capi.biz.domain.game.OddsDo;
import com.wegame.capi.biz.domain.game.OddsResultDo;
import com.wegame.capi.biz.handler.OddsHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component("mariaMakilingOddsHandler")
public class MariaOddsHandler implements OddsHandler {
    @Override
    public OddsResultDo handle(OddsDo oddsDo) {
        return null;
    }

    @Override
    public BigDecimal getOdds(OddsDo oddsDo) {
        return null;
    }
}
