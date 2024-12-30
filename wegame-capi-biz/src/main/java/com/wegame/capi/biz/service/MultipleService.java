package com.wegame.capi.biz.service;

import com.wegame.capi.biz.handler.context.MultipleHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 倍数服务
 */
@Slf4j
@Service
public class MultipleService {

    @Autowired
    MultipleHandlerContext multipleHandlerContext;
    public BigDecimal getMultiple(Long gameId, List<Long> wildSymbolIdList) {
        return multipleHandlerContext.getMultiple(gameId, wildSymbolIdList);
    }
}
