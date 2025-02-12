package com.wegame.capi.biz.module.malakas.multiple;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

import static com.wegame.capi.client.enums.BetSubTypeEnum.*;

@Slf4j
@Component("malakasMultipleCalculateStrategy")
public class MalakasMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<MalakasSymbolDo> {

    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<MalakasSymbolDo> rewardHandle) {
        if (Objects.equals(NORMAL_BET, rewardHandle.getBetSubType())) {
            return BigDecimal.ZERO;
        } else if (Objects.equals(FREE_SPINS_BUY_BET, rewardHandle.getBetSubType()) && BooleanUtils.isTrue(rewardHandle.getBuyBetFirst())) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }
}
