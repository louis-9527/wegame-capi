package com.wegame.capi.biz.module.malakas.multiple;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component("malakasMultipleCalculateStrategy")
public class MalakasMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<MalakasSymbolDo> {

    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<MalakasSymbolDo> rewardHandle, List<RewardLineDo<MalakasSymbolDo>> rewardLineList) {
        if (Objects.equals(rewardHandle.getBetSubType(), BetSubTypeEnum.BUY_BET) && rewardHandle.getWheel().getIndex() > 0) {
            return Objects.isNull(rewardHandle.getCurrentMultiple()) ? BigDecimal.ONE : rewardHandle.getCurrentMultiple().add(BigDecimal.ONE);
        }
        return rewardHandle.getCurrentMultiple();
    }
}
