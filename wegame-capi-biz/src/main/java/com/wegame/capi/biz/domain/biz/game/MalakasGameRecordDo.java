package com.wegame.capi.biz.domain.biz.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wegame.base.enums.BooleanEnum;
import com.wegame.capi.biz.builder.SymbolBuilder;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexFactory;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.client.domain.vo.IndexVo;
import com.wegame.capi.client.domain.vo.malakas.MalakasBetResultVo;
import com.wegame.capi.client.domain.vo.malakas.MalakasGameRecordVo;
import com.wegame.capi.client.domain.vo.malakas.MalakasRewardLineVo;
import com.wegame.capi.common.enums.RewardTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Malakas游戏记录
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document(collection = "malakas_game_record")
public class MalakasGameRecordDo implements Serializable, ToVoConverter<MalakasBetResultVo, MalakasGameRecordVo> {
    private static final long serialVersionUID = -2266308277093710848L;
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
    private WheelDo<MalakasSymbolDo> wheel;
    /**
     * 中奖结果
     */
    private RewardResultDo<MalakasSymbolDo> rewardResult;
    /**
     * 免费轮游戏记录id列表
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
    @JsonIgnore
    public boolean hasRewardFreeSpins() {
        return Objects.nonNull(rewardResult) && CollectionUtils.isNotEmpty(rewardResult.getRewardLineDetailList()) && rewardResult.isHasRewardFreeWheel();
    }

    @JsonIgnore
    @Override
    public MalakasBetResultVo toResultVo() {
        if (Objects.isNull(rewardResult)) {
            return MalakasBetResultVo.builder()
                    .wheel(SymbolBuilder.build(wheel.getSymbolArr()))
                    .build();
        } else if (Objects.equals(rewardResult.getRewardType(), RewardTypeEnum.NONE)) {
            return MalakasBetResultVo.builder()
                    .rewardType(Objects.isNull(rewardResult.getRewardType()) ? null : rewardResult.getRewardType().getCode())
                    .rewardSubType(Objects.isNull(rewardResult.getRewardSubType()) ? null : rewardResult.getRewardSubType().getCode())
                    .totalRewardAmount(rewardResult.getTotalRewardAmount())
                    .totalRewardMultiple(rewardResult.getTotalMultiple())
                    .wheel(SymbolBuilder.build(wheel.getSymbolArr()))
                    .freeWheelFlag(rewardResult.isHasRewardFreeWheel() ? BooleanEnum.TRUE.getCode() : BooleanEnum.FALSE.getCode())
                    .gameRecordIds(Optional.ofNullable(gameRecordIds).orElse(Collections.emptyList()).stream().map(String::valueOf).collect(Collectors.toList()))
                    .build();
        } else {
            return MalakasBetResultVo.builder()
                    .rewardType(Objects.isNull(rewardResult.getRewardType()) ? null : rewardResult.getRewardType().getCode())
                    .rewardSubType(Objects.isNull(rewardResult.getRewardSubType()) ? null : rewardResult.getRewardSubType().getCode())
                    .totalRewardAmount(rewardResult.getTotalRewardAmount())
                    .totalRewardMultiple(rewardResult.getTotalMultiple())
                    .wheel(SymbolBuilder.build(wheel.getSymbolArr()))
                    .rewardSymbolIndexSet(buildRewardSymbolIndexSet())
                    .rewardLineList(buildRewardLineList())
                    .freeWheelFlag(rewardResult.isHasRewardFreeWheel() ? BooleanEnum.TRUE.getCode() : BooleanEnum.FALSE.getCode())
                    .gameRecordIds(Optional.ofNullable(gameRecordIds).orElse(Collections.emptyList()).stream().map(String::valueOf).collect(Collectors.toList()))
                    .build();
        }
    }

    @JsonIgnore
    @Override
    public MalakasGameRecordVo toRecordVo() {
        return MalakasGameRecordVo.builder()
                .id(String.valueOf(id))
                .index(wheel.getIndex())
                .betAmount(Objects.nonNull(rewardResult) ? rewardResult.getBetAmount() : null)
                .rewardAmount(Objects.nonNull(rewardResult) ? rewardResult.getTotalRewardAmount() : null)
                .wheel(SymbolBuilder.build(wheel.getSymbolArr()))
                .multiple(Objects.nonNull(rewardResult) ? rewardResult.getTotalMultiple() : null)
                .rewardLineList(buildRewardLineList())
                .freeWheelFlag(wheel.isFreeFlag())
                .build();
    }

    @JsonIgnore
    private Set<IndexVo> buildRewardSymbolIndexSet() {
        return Optional.ofNullable(rewardResult.getRewardLineDetailList())
                .orElse(Collections.emptyList())
                .stream()
                .filter(o -> CollectionUtils.isNotEmpty(o.getRewardSymbolList()))
                .map(detail -> detail.getRewardSymbolList()
                        .stream()
                        .map(rewardSymbol -> IndexFactory.buildVo(rewardSymbol.getIndex()))
                        .collect(Collectors.toList())
                ).flatMap(List::stream)
                .collect(Collectors.toSet());
    }

    @JsonIgnore
    private List<MalakasRewardLineVo> buildRewardLineList() {
        return Optional.ofNullable(rewardResult.getRewardLineDetailList())
                .orElse(Collections.emptyList())
                .stream()
                .map(subItem -> MalakasRewardLineVo.builder()
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

