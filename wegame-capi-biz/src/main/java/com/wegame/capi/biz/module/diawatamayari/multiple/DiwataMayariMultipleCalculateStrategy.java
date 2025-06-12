package com.wegame.capi.biz.module.diawatamayari.multiple;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.DiwataMayariSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component("diwataMayariMultipleCalculateStrategy")
public class DiwataMayariMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<DiwataMayariSymbolDo> {

    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<DiwataMayariSymbolDo> rewardHandle, List<RewardLineDo<DiwataMayariSymbolDo>> rewardLineList) {

        // todo
       throw new RuntimeException("todo");
    }
}
