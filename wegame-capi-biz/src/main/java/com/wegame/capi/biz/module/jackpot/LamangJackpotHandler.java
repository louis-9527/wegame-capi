package com.wegame.capi.biz.module.jackpot;

import com.wegame.capi.biz.manager.jackpot.AbstractJackpotManager;
import com.wegame.capi.biz.manager.jackpot.LamangJackpotManager;
import com.wegame.capi.biz.module.base.jackpot.AbsJackpotHandler;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LamangJackpotHandler extends AbsJackpotHandler {
    @Autowired
    private LamangJackpotManager lamangJackpotManager;
    @Override
    public AbstractJackpotManager jackpotManager() {
        return lamangJackpotManager;
    }

    @Override
    public JackpotTypeEnum getJackpotType() {
        return JackpotTypeEnum.LAM_ANG;
    }
}
