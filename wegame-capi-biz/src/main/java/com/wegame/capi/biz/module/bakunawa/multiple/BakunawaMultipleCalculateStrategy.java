package com.wegame.capi.biz.module.bakunawa.multiple;

import cn.hutool.core.util.NumberUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component("bakunawaMultipleCalculateStrategy")
public class BakunawaMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<SymbolDo> {

    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<SymbolDo> rewardHandle, List<RewardLineDo<SymbolDo>> rewardLineList) {
        if (CollectionUtils.isEmpty(rewardLineList)) {
            return BigDecimal.ZERO;
        }
        // 中奖元素id字符串拼接得到滚盘倍率
        String multiple = rewardLineList.stream()
                .map(RewardLineDo::getRewardSymbolList)
                .flatMap(List::stream)
                .map(RewardSymbolDo::getSymbol)
                .map(SymbolDo::getId)
                .collect(Collectors.joining(""));
        AssertUtil.assertTrue(NumberUtil.isNumber(multiple), ErrorCode.ERR_PARAM, "multiples is error");
        return new BigDecimal(multiple);
    }
}
