package com.wegame.capi.biz.module.sirena.multiple;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.IbongAdarnaSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SirenaSymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.MultipleTypeEnum;
import com.wegame.capi.common.utils.GameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

@Slf4j
@Component("sirenaMultipleCalculateStrategy")
public class SirenaMultipleCalculateStrategy
        extends AbstractMultipleCalculateStrategy<SirenaSymbolDo> {

    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<SirenaSymbolDo> rewardHandle, List<RewardLineDo<SirenaSymbolDo>> rewardLineList) {
        throw new BusinessException(ErrorCode.ERR_INTER, "can not do this function");
    }

    @Override
    public BigDecimal calculateMultipleOfRewardLine(RewardHandleDo<SirenaSymbolDo> rewardHandle, RewardLineDo<SirenaSymbolDo> rewardLine) {
        if (CollectionUtil.isEmpty(rewardLine.getRewardSymbolList())) {
            return BigDecimal.ONE;
        }
        // 中奖线维度的倍率计算
        BigDecimal rewardLineMultiple = rewardLine.getRewardSymbolList().stream().filter(r -> Objects.nonNull(r.getSymbol().getSymbolOdd()))
                .map(r -> r.getSymbol().getSymbolOdd()).reduce(BigDecimal.ZERO, BigDecimal::add);
        return rewardLineMultiple.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ONE : rewardLineMultiple;
    }
}
