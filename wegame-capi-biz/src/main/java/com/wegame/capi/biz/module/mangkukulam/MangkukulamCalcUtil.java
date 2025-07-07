package com.wegame.capi.biz.module.mangkukulam;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
public class MangkukulamCalcUtil {
    public static long calcTotalRewardAmount(List<MangkukulamGameRecordDo> gameRecordList) {
        AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_PARAM);
        return gameRecordList.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
    }
}
