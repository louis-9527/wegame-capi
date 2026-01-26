package com.wegame.capi.biz.domain.biz;

import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.game.BayanihanGameRecordDo;
import com.wegame.capi.client.domain.vo.bayanihan.BayanihanBetResultDetailVo;
import com.wegame.capi.client.domain.vo.bayanihan.BayanihanGameRecordVo;
import com.wegame.capi.common.enums.RewardSubTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class BayanihanBetRecordDetailDo implements ToVoConverter<BayanihanBetResultDetailVo, BayanihanGameRecordVo> {

    /**
     * 整轮中奖子类型: NONE:未中奖,other,nice,mega,super,sensational
     */
    private RewardSubTypeEnum allWheelRewardSubType;
    /**
     * 游戏记录列表
     */
    private List<BayanihanGameRecordDo> gameRecordList;

    /**
     * 免费轮信息
     */
    private FreeGameInfoDo freeGameInfo;

    @Override
    public BayanihanBetResultDetailVo toResultVo() {
        return BayanihanBetResultDetailVo.builder()
                .allWheelRewardSubType(Objects.nonNull(allWheelRewardSubType) ? allWheelRewardSubType.getCode() : "")
                .resultList(Optional.ofNullable(gameRecordList)
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ToVoConverter::toResultVo)
                        .collect(Collectors.toList()))
                .freeGameInfo(Objects.nonNull(freeGameInfo) ?
                        BayanihanBetResultDetailVo.FreeGameInfoVo.builder()
                                .finalMultiple(freeGameInfo.getFinalMultiple())
                                .initialMultiple(freeGameInfo.getInitialMultiple())
                                .freeWheelCount(freeGameInfo.getFreeWheelCount())
                                .build() : null)
                .build();
    }

    @Override
    public BayanihanGameRecordVo toRecordVo() {
        return null;
    }

    @Data
    @SuperBuilder
    public static class FreeGameInfoDo{
        private BigDecimal initialMultiple;
        private BigDecimal finalMultiple;
        private int freeWheelCount;
    }

}
