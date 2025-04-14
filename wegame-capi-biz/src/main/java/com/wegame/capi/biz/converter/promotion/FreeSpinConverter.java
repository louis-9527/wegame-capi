package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.FreeSpinRes;
import com.wegame.capi.biz.domain.biz.promotion.FreeSpinDo;
import com.wegame.capi.client.domain.vo.promotion.FreeSpinVo;

public class FreeSpinConverter {

    // FreeSpinRes to FreeSpinDo
    public static FreeSpinDo toDo(FreeSpinRes freeSpinRes) {
        return FreeSpinDo.builder()
                .playerName(freeSpinRes.getPlayerName())
                .betId(freeSpinRes.getBetId())
                .credit(freeSpinRes.getCredit())
                .refId(freeSpinRes.getRefId())
                .remainingCount(freeSpinRes.getRemainingCount())
                .campaignId(freeSpinRes.getCampaignId())
                .build();
    }

    // FreeSpinDo to FreeSpinVo
    public static FreeSpinVo toVo(FreeSpinDo freeSpinDo) {
        return FreeSpinVo.builder()
                .playerName(freeSpinDo.getPlayerName())
                .betId(freeSpinDo.getBetId())
                .credit(freeSpinDo.getCredit())
                .refId(freeSpinDo.getRefId())
                .remainingCount(freeSpinDo.getRemainingCount())
                .campaignId(freeSpinDo.getCampaignId())
                .build();
    }

}
