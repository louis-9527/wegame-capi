package com.wegame.capi.biz.domain.biz.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wegame.base.enums.BooleanEnum;
import com.wegame.capi.biz.builder.SymbolBuilder;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.DiwataMayariSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexFactory;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.client.domain.vo.IndexVo;
import com.wegame.capi.client.domain.vo.diwatamayari.DiwataMayariBetResultVo;
import com.wegame.capi.client.domain.vo.diwatamayari.DiwataMayariGameRecordVo;
import com.wegame.capi.client.domain.vo.diwatamayari.DiwataMayariRewardLineVo;
import com.wegame.capi.client.domain.vo.malakas.MalakasBetResultVo;
import com.wegame.capi.client.domain.vo.malakas.MalakasGameRecordVo;
import com.wegame.capi.client.domain.vo.malakas.MalakasRewardLineVo;
import com.wegame.capi.common.enums.RewardTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * diwataMayari游戏记录
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Document(collection = "diwataMayari_game_record")
public class DiwataMayariGameRecordDo implements Serializable, ToVoConverter<DiwataMayariBetResultVo, DiwataMayariGameRecordVo> {
    private static final long serialVersionUID = -2299274723642710848L;
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
    private WheelDo<DiwataMayariSymbolDo> wheel;
    /**
     * 中奖结果
     */
    private RewardResultDo<DiwataMayariSymbolDo> rewardResult;
    /**
     * 免费轮游戏记录id列表
     */
    private List<Long> gameRecordIds;
    /**
     * 最后一轮标识
     */
    private boolean lastGameFlag;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新人
     */
    private String updater;
    /**
     * 更新时间
     */
    private Long updateTime;
    /**
     * 过期时间
     */
    private Date expireAt;

    @Transient
    @JsonIgnore
    @Override
    public DiwataMayariBetResultVo toResultVo() {
        if (Objects.isNull(rewardResult)) {
            return DiwataMayariBetResultVo.builder()
                    .wheel(SymbolBuilder.build(wheel.getSymbolArr()))
                    .build();
        } else if (Objects.equals(rewardResult.getRewardType(), RewardTypeEnum.NONE)) {
            return DiwataMayariBetResultVo.builder()
                    .rewardType(Objects.isNull(rewardResult.getRewardType()) ? null : rewardResult.getRewardType().getCode())
                    .rewardSubType(Objects.isNull(rewardResult.getRewardSubType()) ? null : rewardResult.getRewardSubType().getCode())
                    .totalRewardAmount(rewardResult.getTotalRewardAmount())
                    .totalRewardMultiple(rewardResult.getTotalMultiple())
                    .wheel(SymbolBuilder.build(wheel.getSymbolArr()))
                    .freeWheelFlag(rewardResult.isHasRewardFreeWheel() ? BooleanEnum.TRUE.getCode() : BooleanEnum.FALSE.getCode())
                    .gameRecordIds(Optional.ofNullable(gameRecordIds).orElse(Collections.emptyList()).stream().map(String::valueOf).collect(Collectors.toList()))
                    .accumulateReachMaxMultiple(rewardResult.getAccumulateReachMaxMultiple())
                    .build();
        } else {
            return DiwataMayariBetResultVo.builder()
                    .rewardType(Objects.isNull(rewardResult.getRewardType()) ? null : rewardResult.getRewardType().getCode())
                    .rewardSubType(Objects.isNull(rewardResult.getRewardSubType()) ? null : rewardResult.getRewardSubType().getCode())
                    .totalRewardAmount(rewardResult.getTotalRewardAmount())
                    .totalRewardMultiple(rewardResult.getTotalMultiple())
                    .wheel(SymbolBuilder.build(wheel.getSymbolArr()))
                    .rewardSymbolIndexSet(buildRewardSymbolIndexSet())
                    .rewardLineList(buildRewardLineList())
                    .freeWheelFlag(rewardResult.isHasRewardFreeWheel() ? BooleanEnum.TRUE.getCode() : BooleanEnum.FALSE.getCode())
                    .gameRecordIds(Optional.ofNullable(gameRecordIds).orElse(Collections.emptyList()).stream().map(String::valueOf).collect(Collectors.toList()))
                    .accumulateReachMaxMultiple(rewardResult.getAccumulateReachMaxMultiple())
                    .build();
        }
    }

    @Transient
    @JsonIgnore
    @Override
    public DiwataMayariGameRecordVo toRecordVo() {
        return DiwataMayariGameRecordVo.builder()
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

    @Transient
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

    @Transient
    @JsonIgnore
    private List<DiwataMayariRewardLineVo> buildRewardLineList() {
        return Optional.ofNullable(rewardResult.getRewardLineDetailList())
                .orElse(Collections.emptyList())
                .stream()
                .map(subItem -> DiwataMayariRewardLineVo.builder()
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

