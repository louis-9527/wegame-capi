package com.wegame.capi.biz.module.mangkukulam.multiple;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component("mangkukulamMultipleCalculateStrategy")
public class MangkukulamMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<MangkukulamSymbolDo> {
    
    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<MangkukulamSymbolDo> rewardHandle, List<RewardLineDo<MangkukulamSymbolDo>> rewardLineList) {

        throw new BusinessException(ErrorCode.ERR_INTER, "todo");
    }
}
