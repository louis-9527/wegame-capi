package com.wegame.capi.biz.module.lamang.multiple;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component("malakasMultipleCalculateStrategy")
public class MalakasMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<MalakasSymbolDo> {

    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<MalakasSymbolDo> rewardHandle, List<RewardLineDo<MalakasSymbolDo>> rewardLineList) {
        if (rewardHandle.getWheel().isFreeFlag() && CollectionUtils.isNotEmpty(rewardLineList)) {
            return Objects.isNull(rewardHandle.getCurrentMultiple()) ? BigDecimal.ONE : rewardHandle.getCurrentMultiple().add(BigDecimal.ONE);
        }
        return rewardHandle.getCurrentMultiple();
    }
}
