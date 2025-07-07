package com.wegame.capi.biz.module.mangkukulam.multiple;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component("mangkukulamMultipleCalculateStrategy")
public class MangkukulamMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<MangkukulamSymbolDo> {

    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<MangkukulamSymbolDo> rewardHandle, List<RewardLineDo<MangkukulamSymbolDo>> rewardLineList) {
        if (rewardLineList == null || rewardLineList.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // 将rewardLineList转换成Map<IndexDo, MangkukulamSymbolDo>
        Map<IndexDo, MangkukulamSymbolDo> indexSymbolMap = rewardLineList.stream()
                .map(RewardLineDo::getRewardSymbolList)
                .flatMap(List::stream)
                .collect(Collectors.toMap(
                        RewardSymbolDo::getIndex,
                        RewardSymbolDo::getSymbol,
                        (existing, replacement) -> existing // 如果有重复的IndexDo，保留第一个
                ));

        // 筛选出特殊wild并按坐标去重
        BigDecimal totalMultiple = indexSymbolMap.entrySet().stream()
                .filter(entry -> entry.getValue().isSpecialWild())
                .map(entry -> entry.getValue().getMultiple())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Found {} special wild symbols in {} total symbols, total multiple: {}", 
                indexSymbolMap.values().stream().filter(MangkukulamSymbolDo::isSpecialWild).count(),
                indexSymbolMap.size(), totalMultiple);

        return totalMultiple;
    }
}
