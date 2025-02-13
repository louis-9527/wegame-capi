package com.wegame.capi.biz.module.mariamakiling.multiple;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.wegame.capi.common.enums.SymbolEnum.*;

@Slf4j
@Component("mariaMakilingMultipleCalculateStrategy")
public class MariaMakilingMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<SymbolDo> {

    private static final Map<String, BigDecimal> WILD_MAP = new ImmutableMap.Builder<String, BigDecimal>()
            .put(Wild.getId(), BigDecimal.ZERO)
            .put(WildX2.getId(), new BigDecimal("2"))
            .put(WildX3.getId(), new BigDecimal("3"))
            .put(WildX5.getId(), new BigDecimal("5"))
            .put(WildX10.getId(), new BigDecimal("10"))
            .build();

    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<SymbolDo> rewardHandle, List<RewardLineDo<SymbolDo>> rewardLineList) {
        BigDecimal multiple = new BigDecimal(0);
        for (SymbolDo[] row : rewardHandle.getWheel().getSymbolArr()) {
            for (SymbolDo element : row) {
                AssertUtil.assertNotNull(element, ErrorCode.ERR_INTER);
                if (!Objects.equals(element.getSymbolType(), SymbolTypeEnum.WILD)) {
                    continue;
                }
                BigDecimal val = WILD_MAP.get(element.getId());
                if (Objects.nonNull(val)) {
                    multiple = multiple.add(val);
                }
            }
        }
        return multiple;
    }
}
