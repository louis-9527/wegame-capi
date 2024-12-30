package com.wegame.capi.biz.service;

import com.wegame.capi.biz.domain.game.OddsDo;
import com.wegame.capi.biz.handler.context.OddsHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 赔率服务
 */
@Slf4j
@Service
public class OddsService {

    @Autowired
    OddsHandlerContext oddsHandlerContext;

    /**
     * 获取赔率
     *
     * @param oddsDo 入参
     * @return 赔率
     */
    public BigDecimal getOdds(OddsDo oddsDo) {
        return oddsHandlerContext.getOdds(oddsDo);
    }
}
