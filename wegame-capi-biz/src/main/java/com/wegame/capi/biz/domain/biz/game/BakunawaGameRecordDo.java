package com.wegame.capi.biz.domain.biz.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wegame.capi.biz.builder.SymbolBuilder;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexFactory;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.client.domain.vo.bakunawa.BakunawaBetResultVo;
import com.wegame.capi.client.domain.vo.bakunawa.BakunawaGameRecordVo;
import com.wegame.capi.client.domain.vo.bakunawa.BakunawaRewardLineVo;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Bakunawa游戏记录
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document(collection = "bakunawa_game_record")
public class BakunawaGameRecordDo implements Serializable, ToVoConverter<BakunawaBetResultVo, BakunawaGameRecordVo> {
    private static final long serialVersionUID = -8472383043410674809L;
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
    private WheelDo<SymbolDo> wheel;
    /**
     * 中奖结果
     */
    private RewardResultDo<SymbolDo> rewardResult;
    /**
     * 累计中奖总金额
     */
    private Long overallRewardAmount;
    /**
     * 达到触发最大中奖倍数
     */
    private Boolean accumulateReachMaxMultiple;
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
    public BakunawaBetResultVo toResultVo() {
        if (Objects.isNull(rewardResult)) {
            return BakunawaBetResultVo.builder()
                    .wheel(SymbolBuilder.build(wheel.getSymbolArr()))
                    .build();
        } else if (Objects.equals(rewardResult.getRewardType(), RewardTypeEnum.NONE)) {
            return BakunawaBetResultVo.builder()
                    .rewardType(Objects.isNull(rewardResult.getRewardType()) ? null : rewardResult.getRewardType().getCode())
                    .rewardSubType(Objects.isNull(rewardResult.getRewardSubType()) ? null : rewardResult.getRewardSubType().getCode())
                    .totalRewardAmount(rewardResult.getTotalRewardAmount())
                    .totalRewardMultiple(rewardResult.getTotalMultiple())
                    .wheel(SymbolBuilder.build(wheel.getSymbolArr()))
                    .accumulateReachMaxMultiple(accumulateReachMaxMultiple)
                    .build();
        } else {
            return BakunawaBetResultVo.builder()
                    .rewardType(Objects.isNull(rewardResult.getRewardType()) ? null : rewardResult.getRewardType().getCode())
                    .rewardSubType(Objects.isNull(rewardResult.getRewardSubType()) ? null : rewardResult.getRewardSubType().getCode())
                    .totalRewardAmount(rewardResult.getTotalRewardAmount())
                    .totalRewardMultiple(rewardResult.getTotalMultiple())
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
                    .rewardLine(buildRewardLine())
                    .accumulateReachMaxMultiple(accumulateReachMaxMultiple)
                    .build();
        }
    }

    @Transient
    @Override
    public BakunawaGameRecordVo toRecordVo() {
        return BakunawaGameRecordVo.builder()
                .id(String.valueOf(id))
                .betAmount(Objects.nonNull(rewardResult) ? rewardResult.getBetAmount() : null)
                .rewardAmount(Objects.nonNull(rewardResult) ? rewardResult.getTotalRewardAmount() : null)
                .multiple(Objects.nonNull(rewardResult) ? rewardResult.getTotalMultiple() : null)
                .overallRewardAmount(this.overallRewardAmount)
                .accumulateReachMaxMultiple(this.accumulateReachMaxMultiple)
                .wheel(SymbolBuilder.build(wheel.getSymbolArr()))
                .rewardLine(buildRewardLine())
                .build();
    }

    private BakunawaRewardLineVo buildRewardLine() {
        if (CollectionUtils.isNotEmpty(rewardResult.getRewardLineDetailList())) {
            RewardLineDetailDo<SymbolDo> subItem = rewardResult.getRewardLineDetailList().get(0);
            return BakunawaRewardLineVo.builder()
                    .symbolId(Objects.nonNull(subItem.getRewardSymbol()) ? subItem.getRewardSymbol().getId() : "")
                    .multiple(subItem.getMultiple())
                    .multiplePieType(subItem.getMultiplePieType().getCode())
                    .multiplePie(subItem.getMultiplePie())
                    .betAmount(subItem.getBetAmount())
                    .rewardAmount(subItem.getRewardAmount())
                    .build();
        } else {
            return null;
        }
    }
}

