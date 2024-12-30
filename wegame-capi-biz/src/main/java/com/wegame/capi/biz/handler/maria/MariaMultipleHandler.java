package com.wegame.capi.biz.handler.maria;

import com.wegame.capi.biz.domain.game.MultipleDo;
import com.wegame.capi.biz.domain.game.MultipleResultDo;
import com.wegame.capi.biz.handler.MultipleHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component("mariaMakilingMultipleHandler")
public class MariaMultipleHandler implements MultipleHandler {
    @Override
    public MultipleResultDo handle(MultipleDo multipleDo) {


        return null;
    }

    @Override
    public BigDecimal getMultiple(List<Long> wildSymbolIdList) {
        return null;
    }
}
