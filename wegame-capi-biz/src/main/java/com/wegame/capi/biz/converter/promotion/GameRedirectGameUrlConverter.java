package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.GetRedirectGameUrlRes;
import com.wegame.capi.biz.domain.biz.promotion.GetRedirectGameUrlDo;
import com.wegame.capi.client.domain.vo.promotion.GetRedirectGameUrlVo;

import java.util.Objects;

public class GameRedirectGameUrlConverter {

    public static GetRedirectGameUrlDo resToDo(GetRedirectGameUrlRes res) {
        if (Objects.isNull(res)) {
            return null;
        }
        return GetRedirectGameUrlDo.builder()
                .url(res.getUrl())
                .playerName(res.getPlayerName())
                .refId(res.getRefId())
                .build();
    }


    public static GetRedirectGameUrlVo doToVo(GetRedirectGameUrlDo getRedirectGameUrlDo) {
        if (Objects.isNull(getRedirectGameUrlDo)) {
            return null;
        }
        return GetRedirectGameUrlVo.builder()
                .refId(getRedirectGameUrlDo.getRefId())
                .url(getRedirectGameUrlDo.getUrl())
                .playerName(getRedirectGameUrlDo.getPlayerName())
                .build();
    }

}
