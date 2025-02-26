package com.wegame.capi.biz.domain.biz.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wegame.capi.biz.builder.SymbolBuilder;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.IbongAdarnaSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexFactory;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.client.domain.vo.ibongadarna.IbongAdarnaBetResultVo;
import com.wegame.capi.client.domain.vo.ibongadarna.IbongAdarnaGameRecordVo;
import com.wegame.capi.client.domain.vo.ibongadarna.IbongAdarnaRewardLineVo;
import com.wegame.capi.common.enums.RewardTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * IbongAdarna游戏记录
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document(collection = "ibongadarna_game_record")
public class IbongAdarnaGameRecordDo implements Serializable, ToVoConverter<IbongAdarnaBetResultVo, IbongAdarnaGameRecordVo> {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * id
     */
    @Id
    @Field("_id")
    private Long id;
    /**
     * 投注记录id
     */
    private Long betRecordId;
    /**
     * 玩家id
     */
    private String playerId;
    /**
     * 玩家名称
     */
    private String playerName;
    /**
     * 滚轮
     */
    private WheelDo<IbongAdarnaSymbolDo> wheel;
    /**
     * 中奖结果
     */
    private RewardResultDo<IbongAdarnaSymbolDo> rewardResult;
    /**
     * 累计中奖总金额
     */
    private Long overallRewardAmount;
    /**
     * 达到触发最大中奖倍数
     */
    private Boolean accumulateReachMaxMultiple;

    /**
     * 是否中奖免费轮
     *
     * @return
     */
    @Transient
    @JsonIgnore
    public boolean hasRewardFreeSpins() {
        return Objects.nonNull(rewardResult) && CollectionUtils.isNotEmpty(rewardResult.getRewardLineDetailList()) && rewardResult.isHasRewardFreeWheel();
    }

    @Transient
    @Override
    public IbongAdarnaBetResultVo toResultVo() {
        if (Objects.isNull(rewardResult)) {
            return IbongAdarnaBetResultVo.builder()
                    .accumulateReachMaxMultiple(this.accumulateReachMaxMultiple)
                    .wheel(SymbolBuilder.build(wheel.getSymbolArr()))
                    .build();
        } else if (Objects.equals(rewardResult.getRewardType(), RewardTypeEnum.NONE)) {
            return IbongAdarnaBetResultVo.builder()
                    .rewardType(Objects.isNull(rewardResult.getRewardType()) ? null : rewardResult.getRewardType().getCode())
                    .rewardSubType(Objects.isNull(rewardResult.getRewardSubType()) ? null : rewardResult.getRewardSubType().getCode())
                    .totalRewardAmount(rewardResult.getTotalRewardAmount())
                    .totalRewardMultiple(rewardResult.getTotalMultiple())
                    .accumulateReachMaxMultiple(this.accumulateReachMaxMultiple)
                    .wheel(SymbolBuilder.build(wheel.getSymbolArr()))
                    .freeWheelFlag(0)
                    .build();
        } else {
            return IbongAdarnaBetResultVo.builder()
                    .rewardType(Objects.isNull(rewardResult.getRewardType()) ? null : rewardResult.getRewardType().getCode())
                    .rewardSubType(Objects.isNull(rewardResult.getRewardSubType()) ? null : rewardResult.getRewardSubType().getCode())
                    .totalRewardAmount(rewardResult.getTotalRewardAmount())
                    .totalRewardMultiple(rewardResult.getTotalMultiple())
                    .accumulateReachMaxMultiple(this.accumulateReachMaxMultiple)
                    .wheel(SymbolBuilder.build(wheel.getSymbolArr()))
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
                    .rewardLineList(buildRewardLineList())
                    .freeWheelFlag(rewardResult.isHasRewardFreeWheel() ? 1 : 0)
                    .build();
        }
    }

    @Transient
    @Override
    public IbongAdarnaGameRecordVo toRecordVo() {
        return IbongAdarnaGameRecordVo.builder()
                .id(String.valueOf(id))
                .betAmount(Objects.nonNull(rewardResult) ? rewardResult.getBetAmount() : null)
                .rewardAmount(Objects.nonNull(rewardResult) ? rewardResult.getTotalRewardAmount() : null)
                .multiple(Objects.nonNull(rewardResult) ? rewardResult.getTotalMultiple() : null)
                .overallRewardAmount(this.overallRewardAmount)
                .accumulateReachMaxMultiple(this.accumulateReachMaxMultiple)
                .wheel(SymbolBuilder.build(wheel.getSymbolArr()))
                .rewardLineList(buildRewardLineList())
                .build();
    }

    private List<IbongAdarnaRewardLineVo> buildRewardLineList() {
        return Optional.ofNullable(rewardResult.getRewardLineDetailList())
                .orElse(Collections.emptyList())
                .stream()
                .map(subItem -> IbongAdarnaRewardLineVo.builder()
                        .symbolId(Objects.nonNull(subItem.getRewardSymbol()) ? subItem.getRewardSymbol().getId() : "")
                        .multiple(subItem.getMultiple())
                        .odds(subItem.getOdds())
                        .rewardLineType(Objects.nonNull(subItem.getRewardLineType()) ? subItem.getRewardLineType().getCode() : "")
                        .rewardLineSubType(Objects.nonNull(subItem.getRewardLineSubType()) ? subItem.getRewardLineSubType().getCode() : "")
                        .rewardLineCount(subItem.getRewardLineCount())
                        .betAmount(subItem.getBetAmount())
                        .rewardAmount(subItem.getRewardAmount())
                        .build()
                ).collect(Collectors.toList());
    }
}

