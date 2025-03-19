package com.wegame.capi.biz.domain.biz.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wegame.capi.biz.builder.SymbolBuilder;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexFactory;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.client.domain.vo.mariamaking.MariaMakilingBetResultVo;
import com.wegame.capi.client.domain.vo.mariamaking.MariaMakilingGameRecordVo;
import com.wegame.capi.client.domain.vo.mariamaking.MariaMakilingRewardLineVo;
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
 * MariaMakiling游戏记录
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document(collection = "mariamakiling_game_record")
public class MariaMakilingGameRecordDo implements Serializable, ToVoConverter<MariaMakilingBetResultVo, MariaMakilingGameRecordVo> {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * id
     */
    @Id
    @Field("_id")
    private Long id;
    /**
     * parentId: 无，则为0
     */
    private Long parentId;
    /**
     * 批次id
     */
    private Long batchId;
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
     * 序号
     */
    private Integer index;
    /**
     * 免费轮标识 false否 true是
     */
    private Boolean freeWheelFlag;
    /**
     * 滚轮
     */
    private WheelDo<SymbolDo> wheel;
    /**
     * 中奖结果
     */
    private RewardResultDo<SymbolDo> rewardResult;
    /**
     * 免费轮id列表
     */
    private List<Long> gameRecordIds;
    /**
     * 累计中奖总金额
     */
    private Long overallRewardAmount;
    /**
     * 达到触发最大中奖倍数
     */
    private Boolean accumulateReachMaxMultiple;
    /**
     * 最后一轮标识
     */
    private Boolean lastGameFlag;

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
    public MariaMakilingBetResultVo toResultVo() {
        if (Objects.isNull(rewardResult)) {
            return MariaMakilingBetResultVo.builder()
                    .wheel(SymbolBuilder.build(wheel.getSymbolArr()))
                    .build();
        } else if (Objects.equals(rewardResult.getRewardType(), RewardTypeEnum.NONE)) {
            return MariaMakilingBetResultVo.builder()
                    .rewardType(Objects.isNull(rewardResult.getRewardType()) ? null : rewardResult.getRewardType().getCode())
                    .rewardSubType(Objects.isNull(rewardResult.getRewardSubType()) ? null : rewardResult.getRewardSubType().getCode())
                    .totalRewardAmount(rewardResult.getTotalRewardAmount())
                    .totalRewardMultiple(rewardResult.getTotalMultiple())
                    .accumulateReachMaxMultiple(accumulateReachMaxMultiple)
                    .wheel(SymbolBuilder.build(wheel.getSymbolArr()))
                    .freeWheelFlag(0)
                    .build();
        } else {
            return MariaMakilingBetResultVo.builder()
                    .rewardType(Objects.isNull(rewardResult.getRewardType()) ? null : rewardResult.getRewardType().getCode())
                    .rewardSubType(Objects.isNull(rewardResult.getRewardSubType()) ? null : rewardResult.getRewardSubType().getCode())
                    .totalRewardAmount(rewardResult.getTotalRewardAmount())
                    .totalRewardMultiple(rewardResult.getTotalMultiple())
                    .accumulateReachMaxMultiple(accumulateReachMaxMultiple)
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
                    .gameRecordIds(Optional.ofNullable(gameRecordIds)
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(String::valueOf)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Transient
    @Override
    public MariaMakilingGameRecordVo toRecordVo() {
        return MariaMakilingGameRecordVo.builder()
                .id(String.valueOf(id))
                .index(index)
                .betAmount(Objects.nonNull(rewardResult) ? rewardResult.getBetAmount() : null)
                .rewardAmount(Objects.nonNull(rewardResult) ? rewardResult.getTotalRewardAmount() : null)
                .multiple(Objects.nonNull(rewardResult) ? rewardResult.getTotalMultiple() : null)
                .wheel(SymbolBuilder.build(wheel.getSymbolArr()))
                .accumulateReachMaxMultiple(accumulateReachMaxMultiple)
                .rewardLineList(buildRewardLineList())
                .build();
    }

    private List<MariaMakilingRewardLineVo> buildRewardLineList() {
        return Optional.ofNullable(rewardResult.getRewardLineDetailList())
                .orElse(Collections.emptyList())
                .stream()
                .map(subItem -> MariaMakilingRewardLineVo.builder()
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

