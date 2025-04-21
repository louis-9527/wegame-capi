package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.CreateFreeSpinRes;
import com.wegame.capi.biz.domain.biz.promotion.CreateFreeSpinDo;
import com.wegame.capi.client.domain.vo.promotion.CreateFreeSpinVo;

public class CreateFreeSpinConverter {

    // CreateFreeSpinRes to CreateFreeSpinDo
    public static CreateFreeSpinDo toDo(CreateFreeSpinRes createFreeSpinRes) {
        return CreateFreeSpinDo.builder()
                .result(createFreeSpinRes.getResult())
                .eventId(createFreeSpinRes.getEventId())
                .build();
    }

    // CreateFreeSpinDo to CreateFreeSpinVo
    public static CreateFreeSpinVo toVo(CreateFreeSpinDo createFreeSpinDo) {
        return CreateFreeSpinVo.builder()
                .result(createFreeSpinDo.getResult())
                .eventId(createFreeSpinDo.getEventId())
                .build();
    }
}
