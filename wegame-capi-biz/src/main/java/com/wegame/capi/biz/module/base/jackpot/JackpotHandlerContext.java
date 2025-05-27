package com.wegame.capi.biz.module.base.jackpot;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JackpotHandlerContext {

private final Map<JackpotTypeEnum, AbsJackpotHandler> jackpotManagerMap = new HashMap<>();
    // 通过构造器注入所有策略实现
    @Autowired
    public JackpotHandlerContext(List<AbsJackpotHandler> jackpotManagerList) {
        for (AbsJackpotHandler strategy : jackpotManagerList) {
            jackpotManagerMap.put(strategy.getJackpotType(), strategy);
        }
    }
    public AbsJackpotHandler getHandler(JackpotTypeEnum jackpotTypeEnum){
        AssertUtil.assertNotNull(jackpotTypeEnum, ErrorCode.ERR_PARAM);
        return jackpotManagerMap.get(jackpotTypeEnum);
    }
 }
