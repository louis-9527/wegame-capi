package com.wegame.capi.biz.domain.biz.game;

import com.wegame.capi.biz.builder.SymbolBuilder;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexFactory;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.client.domain.vo.MariaMakilingBetResultVo;
import com.wegame.capi.common.enums.RewardTypeEnum;
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
 * Malakas游戏记录
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MalakasGameRecordDo implements Serializable, ToVoConverter<MariaMakilingBetResultVo> {
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
        } else if (Objects.equals(rewardResult.getRewardType(), RewardTypeEnum.NONE)) {
            return MariaMakilingBetResultVo.builder()
                    .rewardType(Objects.isNull(rewardResult.getRewardType()) ? null : rewardResult.getRewardType().getCode())
                    .rewardSubType(Objects.isNull(rewardResult.getRewardSubType()) ? null : rewardResult.getRewardSubType().getCode())
                    .totalRewardAmount(rewardResult.getTotalRewardAmount())
                    .totalRewardMultiple(rewardResult.getTotalMultiple())
                    .spinsResult(SymbolBuilder.build(wheel.getSymbolArr()))
                    .freeSpinFlag(0)
                    .build();
        } else {
            return MariaMakilingBetResultVo.builder()
                    .rewardType(Objects.isNull(rewardResult.getRewardType()) ? null : rewardResult.getRewardType().getCode())
                    .rewardSubType(Objects.isNull(rewardResult.getRewardSubType()) ? null : rewardResult.getRewardSubType().getCode())
                    .totalRewardAmount(rewardResult.getTotalRewardAmount())
                    .totalRewardMultiple(rewardResult.getTotalMultiple())
                    .spinsResult(SymbolBuilder.build(wheel.getSymbolArr()))
                    .rewardSymbolIndexSet(Optional.ofNullable(rewardResult.getRewardLineDetailList())
                            .orElse(Collections.emptyList())
                            .stream()
                            .filter(o -> CollectionUtils.isNotEmpty(o.getRewardSymbolList()))
                            .map(detail -> detail.getRewardSymbolList()
                                    .stream()
                                    .map(rewardSymbol -> IndexFactory.buildVo(rewardSymbol.getIndex()))
                                    .collect(Collectors.toList())
                            ).flatMap(List::stream)
                            .collect(Collectors.toSet()))
                    .rewardLineList(Optional.ofNullable(rewardResult.getRewardLineDetailList())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(subItem -> MariaMakilingBetResultVo.MariaMakilingRewardLineVo.builder()
                                    .symbolId(Objects.nonNull(subItem.getRewardSymbol()) ? subItem.getRewardSymbol().getId() : "")
                                    .multiple(subItem.getMultiple())
                                    .odds(subItem.getOdds())
                                    .rewardLineType(Objects.nonNull(subItem.getRewardLineType()) ? subItem.getRewardLineType().getCode() : "")
                                    .rewardLineSubType(Objects.nonNull(subItem.getRewardLineSubType()) ? subItem.getRewardLineSubType().getCode() : "")
                                    .rewardLineCount(subItem.getRewardLineCount())
                                    .betAmount(subItem.getBetAmount())
                                    .rewardAmount(subItem.getRewardAmount())
                                    .build()
                            ).collect(Collectors.toList()))
                    .freeSpinFlag(rewardResult.isHasRewardFreeSpin() ? 1 : 0)
                    .freeSpinIds(freeSpinIds)
                    .build();
        }
    }
}

