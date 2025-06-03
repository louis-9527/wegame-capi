package com.wegame.capi.biz.builder.jackpot;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;

import java.util.ArrayList;
import java.util.List;

public class JackpotRewardPoolBuilder {

    public static List<JackpotRewardPoolDo> buildList(Long grandAmount,
                                                      Long majorAmount,
                                                      Long minorAmount,
                                                      Long miniAmount) {
        // 组装奖池对象
        long currentMilli = DateUtil.getCurrentMilli();
        List<JackpotRewardPoolDo> list = new ArrayList<>(4);
        list.add(JackpotRewardPoolDo.builder()
                .type(JackpotRewardPoolTypeEnum.grand)
                .amount(grandAmount)
                .updateTime(currentMilli)
                .build());
        list.add(JackpotRewardPoolDo.builder()
                .type(JackpotRewardPoolTypeEnum.major)
                .amount(majorAmount)
                .updateTime(currentMilli)
                .build());
        list.add(JackpotRewardPoolDo.builder()
                .type(JackpotRewardPoolTypeEnum.minor)
                .amount(minorAmount)
                .updateTime(currentMilli)
                .build());
        list.add(JackpotRewardPoolDo.builder()
                .type(JackpotRewardPoolTypeEnum.mini)
                .amount(miniAmount)
                .updateTime(currentMilli)
                .build());
        return list;
    }
}
