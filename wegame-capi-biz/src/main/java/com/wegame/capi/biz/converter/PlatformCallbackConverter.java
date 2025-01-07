package com.wegame.capi.biz.converter;

import com.wegame.capi.client.domain.dto.BetReconciliationDto;
import com.wegame.capi.client.domain.dto.CreditChangeNotifyDto;
import com.wegame.capi.client.domain.request.BetReconciliationRequest;
import com.wegame.capi.client.domain.request.CreditChangeNotifyRequest;

import java.util.Objects;

public class PlatformCallbackConverter {

        public static CreditChangeNotifyDto requestToDto(CreditChangeNotifyRequest request) {
            if (Objects.isNull(request)) {
                return null;
            }

            return CreditChangeNotifyDto.builder()
                    .channelId(request.getChannelId())
                    .playerName(request.getPlayerName())
                    .hash(request.getHash())
                    .refId(request.getRefId())
                    .createTime(request.getCreateTime())
                    .credit(request.getCredit())
                    .sId(request.getSId())
                    .build();
        }

        public static BetReconciliationDto requestToDto(BetReconciliationRequest request) {
            if (Objects.isNull(request)) {
                return null;
            }

            return BetReconciliationDto.builder()
                    .channelId(request.getChannelId())
                    .hash(request.getHash())
                    .refId(request.getRefId())
                    .createTime(request.getCreateTime())
                    .timeType(request.getTimeType())
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .build();
        }
}

