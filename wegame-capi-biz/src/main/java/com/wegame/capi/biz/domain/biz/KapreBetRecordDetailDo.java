package com.wegame.capi.biz.domain.biz;

import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.game.KapreGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.client.domain.vo.kapre.KapreBetResultDetailVo;
import com.wegame.capi.client.domain.vo.kapre.KapreGameRecordVo;
import com.wegame.capi.client.domain.vo.malakas.MalakasBetResultDetailVo;
import com.wegame.capi.client.domain.vo.malakas.MalakasGameRecordVo;
import com.wegame.capi.common.enums.RewardSubTypeEnum;
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
public class KapreBetRecordDetailDo implements ToVoConverter<KapreBetResultDetailVo, KapreGameRecordVo> {

    /**
     * 整轮中奖子类型: NONE:未中奖,other,nice,mega,super,sensational
     */
    private RewardSubTypeEnum allWheelRewardSubType;
    /**
     * 游戏记录列表
     */
    private List<KapreGameRecordDo> gameRecordList;

    @Override
    public KapreBetResultDetailVo toResultVo() {
        return KapreBetResultDetailVo.builder()
                .allWheelRewardSubType(Objects.nonNull(allWheelRewardSubType) ? allWheelRewardSubType.getCode() : "")
                .resultList(Optional.ofNullable(gameRecordList)
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ToVoConverter::toResultVo)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public KapreGameRecordVo toRecordVo() {
        return null;
    }

}
