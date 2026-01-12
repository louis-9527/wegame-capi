package com.wegame.capi.biz.builder.jackpot;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

public class JackpotRewardPoolBuilder {
    @Data
    @SuperBuilder
    public static class BuildContext{
        long grandAmount;
        long majorAmount;
        long minorAmount;
        long miniAmount;
        long grandReservedAmount;
        long majorReservedAmount;
        long minorReservedAmount;
        long miniReservedAmount;
    }

    public static List<JackpotRewardPoolDo> buildList(BuildContext context) {
        // 组装奖池对象
        long currentMilli = DateUtil.getCurrentMilli();
        List<JackpotRewardPoolDo> list = new ArrayList<>(4);
        list.add(JackpotRewardPoolDo.builder()
                .type(JackpotRewardPoolTypeEnum.grand)
                .amount(context.getGrandAmount())
                        .reservedAmount(context.getGrandReservedAmount() )
                .updateTime(currentMilli)
                .build());
        list.add(JackpotRewardPoolDo.builder()
                .type(JackpotRewardPoolTypeEnum.major)
                .amount(context.getMajorAmount())
                        .reservedAmount(context.getMajorReservedAmount() )
                .updateTime(currentMilli)
                .build());
        list.add(JackpotRewardPoolDo.builder()
                .type(JackpotRewardPoolTypeEnum.minor)
                .amount(context.getMinorAmount())
                        .reservedAmount(context.getMinorReservedAmount())
                .updateTime(currentMilli)
                .build());
        list.add(JackpotRewardPoolDo.builder()
                .type(JackpotRewardPoolTypeEnum.mini)
                .amount(context.getMinorAmount())
                        .reservedAmount(context.getMiniReservedAmount())
                .updateTime(currentMilli)
                .build());
        return list;
    }
}
