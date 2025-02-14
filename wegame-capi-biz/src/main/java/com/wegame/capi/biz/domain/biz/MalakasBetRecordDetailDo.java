package com.wegame.capi.biz.domain.biz;

import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.client.domain.vo.malakas.MalakasBetResultDetailVo;
import com.wegame.capi.client.domain.vo.malakas.MalakasGameRecordVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MalakasBetRecordDetailDo implements ToVoConverter<MalakasBetResultDetailVo, MalakasGameRecordVo> {

    /**
     * 游戏记录列表
     */
    private List<MalakasGameRecordDo> gameRecordList;

    public boolean isLastFreeSpins(Long freeSpinsId) {
        MalakasGameRecordDo recordDo = CollectionUtils.isEmpty(gameRecordList) ? null : gameRecordList.get(gameRecordList.size() - 1);
        return Objects.isNull(recordDo) ? false : Objects.equals(recordDo.getId(), freeSpinsId);
    }

    public long freeSpinCount() {
        return Optional.ofNullable(gameRecordList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(item -> item.getId() > 0)
                .count();
    }

    @Override
    public MalakasBetResultDetailVo toResultVo() {
        return MalakasBetResultDetailVo.builder()
                .resultList(Optional.ofNullable(gameRecordList)
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ToVoConverter::toResultVo)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public MalakasGameRecordVo toRecordVo() {
        return null;
    }


    //    private MalakasBetResultVo toVo(MalakasGameRecordDo gameRecord) {
    //        RewardResultDo<MalakasSymbolDo> rewardResult = gameRecord.getRewardResult();
    //        if (Objects.isNull(rewardResult)) {
    //            return MalakasBetResultVo.builder().wheel(SymbolBuilder.build(gameRecord.getWheel().getSymbolArr())).build();
    //        } else if (Objects.equals(rewardResult.getRewardType(), RewardTypeEnum.NONE)) {
    //            return MalakasBetResultVo.builder()
    //                    .rewardType(Objects.isNull(rewardResult.getRewardType()) ? null : rewardResult.getRewardType().getCode())
    //                    .rewardSubType(Objects.isNull(rewardResult.getRewardSubType()) ? null : rewardResult.getRewardSubType().getCode())
    //                    .totalRewardAmount(rewardResult.getTotalRewardAmount())
    //                    .totalRewardMultiple(rewardResult.getTotalMultiple())
    //                    .wheel(SymbolBuilder.build(gameRecord.getWheel().getSymbolArr()))
    //                    .freeSpinFlag(0)
    //                    .gameRecordIds(Optional.ofNullable(gameRecord.getGameRecordIds()).orElse(Collections.emptyList()).stream().map(String::valueOf).collect(Collectors.toList()))
    //                    .build();
    //        } else {
    //            return MalakasBetResultVo.builder()
    //                    .rewardType(Objects.isNull(rewardResult.getRewardType()) ? null : rewardResult.getRewardType().getCode())
    //                    .rewardSubType(Objects.isNull(rewardResult.getRewardSubType()) ? null : rewardResult.getRewardSubType().getCode())
    //                    .totalRewardAmount(rewardResult.getTotalRewardAmount())
    //                    .totalRewardMultiple(rewardResult.getTotalMultiple())
    //                    .wheel(SymbolBuilder.build(gameRecord.getWheel().getSymbolArr()))
    //                    .rewardSymbolIndexSet(Optional.ofNullable(rewardResult.getRewardLineDetailList())
    //                            .orElse(Collections.emptyList())
    //                            .stream()
    //                            .filter(o -> CollectionUtils.isNotEmpty(o.getRewardSymbolList()))
    //                            .map(detail -> detail.getRewardSymbolList()
    //                                    .stream()
    //                                    .map(rewardSymbol -> IndexFactory.buildVo(rewardSymbol.getIndex()))
    //                                    .collect(Collectors.toList())
    //                            ).flatMap(List::stream)
    //                            .collect(Collectors.toSet()))
    //
    //                    .rewardLineList(Optional.ofNullable(rewardResult.getRewardLineDetailList())
    //                            .orElse(Collections.emptyList())
    //                            .stream()
    //                            .map(subItem -> MalakasRewardLineVo.builder()
    //                                    .symbolId(Objects.nonNull(subItem.getRewardSymbol()) ? subItem.getRewardSymbol().getId() : "")
    //                                    .multiple(subItem.getMultiple())
    //                                    .odds(subItem.getOdds())
    //                                    .rewardLineType(Objects.nonNull(subItem.getRewardLineType()) ? subItem.getRewardLineType().getCode() : "")
    //                                    .rewardLineSubType(Objects.nonNull(subItem.getRewardLineSubType()) ? subItem.getRewardLineSubType().getCode() : "")
    //                                    .rewardLineCount(subItem.getRewardLineCount())
    //                                    .betAmount(subItem.getBetAmount())
    //                                    .rewardAmount(subItem.getRewardAmount())
    //                                    .build()
    //                            ).collect(Collectors.toList()))
    //                    .freeSpinFlag(rewardResult.isHasRewardFreeWheel() ? 1 : 0)
    //                    .gameRecordIds(Optional.ofNullable(gameRecord.getGameRecordIds()).orElse(Collections.emptyList()).stream().map(String::valueOf).collect(Collectors.toList()))
    //                    .build();
    //        }
    //    }
}
