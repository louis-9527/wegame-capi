package com.wegame.capi.biz.module.base.jackpot;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JackpotHandlerContext {
    @Autowired
    private Map<String, AbsJackpotHandler> jackpotManagerMap;

    public static final Map<JackpotTypeEnum, String> JACKPOT_MANAGER_NAME_MAP = new ImmutableMap.Builder<JackpotTypeEnum, String>()
            .put(JackpotTypeEnum.LAM_ANG, "lamangJackpotHandler")
            .build();
    public AbsJackpotHandler getHandler(JackpotTypeEnum jackpotTypeEnum){
        AssertUtil.assertNotNull(jackpotTypeEnum, ErrorCode.ERR_PARAM);
        return jackpotManagerMap.get(JACKPOT_MANAGER_NAME_MAP.get(jackpotTypeEnum));
    }
 }
