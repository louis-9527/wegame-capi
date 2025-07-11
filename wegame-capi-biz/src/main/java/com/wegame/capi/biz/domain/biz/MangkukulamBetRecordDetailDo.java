package com.wegame.capi.biz.domain.biz;

import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.client.domain.vo.mangkukulam.MangkukulamBetResultDetailVo;
import com.wegame.capi.client.domain.vo.mangkukulam.MangkukulamGameRecordVo;
import com.wegame.capi.common.enums.RewardSubTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
public class MangkukulamBetRecordDetailDo implements ToVoConverter<MangkukulamBetResultDetailVo, MangkukulamGameRecordVo> {

    /**
     * 整轮中奖子类型: NONE:未中奖,other,nice,mega,super,sensational
     */
    private RewardSubTypeEnum allWheelRewardSubType;
    /**
     * 游戏记录列表
     */
    private List<MangkukulamGameRecordDo> gameRecordList;
    /**
     * 波动投注游戏记录Id列表
     */
    private List<Long> volatilityGameRecordIds;

    @Override
    public MangkukulamBetResultDetailVo toResultVo() {
        return MangkukulamBetResultDetailVo.builder()
                .allWheelRewardSubType(Objects.nonNull(allWheelRewardSubType) ? allWheelRewardSubType.getCode() : "")
                .resultList(Optional.ofNullable(gameRecordList)
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ToVoConverter::toResultVo)
                        .collect(Collectors.toList()))
                .volatilityGameRecordIds(Optional.ofNullable(volatilityGameRecordIds).orElse(Collections.emptyList()))
                .build();
    }

    @Override
    public MangkukulamGameRecordVo toRecordVo() {
        return null;
    }

}
