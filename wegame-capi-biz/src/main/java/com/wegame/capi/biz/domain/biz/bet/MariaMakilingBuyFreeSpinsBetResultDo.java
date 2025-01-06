package com.wegame.capi.biz.domain.biz.bet;

import com.wegame.capi.biz.converter.Converter;
import com.wegame.capi.biz.domain.biz.MariaMakilingGameRecordDetailDo;
import com.wegame.capi.biz.builder.SymbolBuilder;
import com.wegame.capi.client.domain.vo.GameBetPayOutDetailVo;
import com.wegame.capi.client.domain.vo.MariaMakilingNonBuyFreeSpinsBetResultVo;
import com.wegame.capi.client.domain.vo.SymbolIndexVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MariaMakilingBuyFreeSpinsBetResultDo implements Converter<Object> {
    private List<Integer> freeSpinIds;
    private MariaMakilingGameRecordDetailDo resultDetail;

    @Override
    public Object toVo() {
        MariaMakilingNonBuyFreeSpinsBetResultVo vo = new MariaMakilingNonBuyFreeSpinsBetResultVo();
        vo.setPrizeType(resultDetail.getPrizeType() == null ? null : resultDetail.getPrizeType().getCode());
        vo.setPayOutTotalAmount(resultDetail.getRewardAmount());
        vo.setSpinsResult(SymbolBuilder.build(resultDetail.getGameResult()));
        List<GameBetPayOutDetailVo> detailVos = new ArrayList<>();
        List<MariaMakilingGameRecordDetailDo.RewardLineResultDo> rewardResults = resultDetail.getRewardResults();
        if (CollectionUtils.isNotEmpty(rewardResults)) {
            for (MariaMakilingGameRecordDetailDo.RewardLineResultDo rewardResult : rewardResults) {
                GameBetPayOutDetailVo detailVo = new GameBetPayOutDetailVo();
                List<String> rewardSymbolIdList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(rewardResult.getRewardSymbolList())) {
                    rewardResult.getRewardSymbolList().forEach(symbol -> rewardSymbolIdList.add(symbol.getId()));
                }
                detailVo.setRewardSymbolIdList(rewardSymbolIdList);
                detailVo.setMultiple(rewardResult.getMultiple());
                detailVo.setOdds(rewardResult.getOdds());
                detailVo.setPayOutAmount(rewardResult.getRewardAmount());
                List<SymbolIndexVo> symbolIndexVo = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(rewardResult.getRewardIndexList())) {
                    rewardResult.getRewardIndexList().forEach(indexDo -> {
                        symbolIndexVo.add(SymbolIndexVo.build(indexDo.getRowIndex(), indexDo.getColumIndex()));
                    });
                }
                detailVo.setIndex(symbolIndexVo);
                detailVos.add(detailVo);
            }
        }
        vo.setPayOutResult(detailVos);
        vo.setFreeSpinIds(freeSpinIds);
        return vo;
    }
}
