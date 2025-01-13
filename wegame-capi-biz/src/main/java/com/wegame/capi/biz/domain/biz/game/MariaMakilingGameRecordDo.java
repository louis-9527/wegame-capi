package com.wegame.capi.biz.domain.biz.game;

import com.wegame.capi.biz.builder.SymbolBuilder;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.client.domain.vo.MariaMakilingBetResultVo;
import com.wegame.capi.client.domain.vo.SymbolIndexVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 玩游戏结果模型
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MariaMakilingGameRecordDo implements Serializable, ToVoConverter<MariaMakilingBetResultVo> {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 序号: 默认1
     */
    private Integer index;
    /**
     * 滚轮
     */
    private WheelDo wheel;
    /**
     * 中奖结果
     */
    private RewardResultDo rewardResult;
    /**
     * 免费轮id列表
     */
    private List<Integer> freeSpinIds;

    /**
     * 是否中奖
     *
     * @return
     */
    public boolean hasReward() {
        return Objects.nonNull(rewardResult) && CollectionUtils.isNotEmpty(rewardResult.getRewardLineDetailList());
    }

    /**
     * 是否中奖免费轮
     *
     * @return
     */
    public boolean hasRewardFreeSpins() {
        return Objects.nonNull(rewardResult) && CollectionUtils.isNotEmpty(rewardResult.getRewardLineDetailList()) && rewardResult.isHasRewardFreeSpin();
    }


    @Override
    public MariaMakilingBetResultVo toVo() {
        if (Objects.isNull(rewardResult)) {
            return MariaMakilingBetResultVo.builder()
                    .spinsResult(SymbolBuilder.build(wheel.getSymbolArr()))
                    .build();
        } else {
            return MariaMakilingBetResultVo.builder()
                    .rewardType(rewardResult.getRewardType() == null ? null : rewardResult.getRewardType().getCode())
                    .rewardSubType(rewardResult.getRewardSubType() == null ? null : rewardResult.getRewardSubType().getCode())
                    .totalRewardAmount(rewardResult.getTotalRewardAmount())
                    .totalRewardMultiple(rewardResult.getTotalMultiple())
                    .spinsResult(SymbolBuilder.build(wheel.getSymbolArr()))
                    .rewardLineList(Optional.ofNullable(rewardResult.getRewardLineDetailList())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(item -> MariaMakilingBetResultVo.MariaMakilingRewardLineVo.builder()
                                    .multiple(item.getMultiple())
                                    .odds(item.getOdds())
                                    .rewardLineType(Objects.nonNull(item.getRewardLineType()) ? item.getRewardLineType().getCode() : "")
                                    .rewardLineSubType(Objects.nonNull(item.getRewardLineSubType()) ? item.getRewardLineSubType().getCode() : "")
                                    .symbolIdList(Optional.ofNullable(item.getSymbolList())
                                            .orElse(Collections.emptyList())
                                            .stream()
                                            .map(symbol -> symbol.getId())
                                            .collect(Collectors.toList()))
                                    .symbolIndexList(Optional.ofNullable(item.getSymbolIndexList())
                                            .orElse(Collections.emptyList())
                                            .stream()
                                            .map(symbolIndex -> SymbolIndexVo.builder()
                                                    .rowIndex(symbolIndex.getRowIndex())
                                                    .columIndex(symbolIndex.getColumIndex())
                                                    .build()
                                            ).collect(Collectors.toList()))
                                    .betAmount(item.getBetAmount())
                                    .rewardAmount(item.getRewardAmount())
                                    .build()).collect(Collectors.toList()))
                    .freeSpinFlag(rewardResult.isHasRewardFreeSpin() ? 1 : 0)
                    .freeSpinIds(freeSpinIds)
                    .build();
        }
    }
}

