package com.wegame.capi.biz.domain.biz;

import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.game.DiwataMayariGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.client.domain.vo.diwatamayari.DiwataMayariBetResultDetailVo;
import com.wegame.capi.client.domain.vo.diwatamayari.DiwataMayariGameRecordVo;
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
public class DiwataMayariBetRecordDetailDo implements ToVoConverter<DiwataMayariBetResultDetailVo, DiwataMayariGameRecordVo> {

    /**
     * 整轮中奖子类型: NONE:未中奖,other,nice,mega,super,sensational
     */
    private RewardSubTypeEnum allWheelRewardSubType;
    /**
     * 游戏记录列表
     */
    private List<DiwataMayariGameRecordDo> gameRecordList;

    @Override
    public DiwataMayariBetResultDetailVo toResultVo() {
        return DiwataMayariBetResultDetailVo.builder()
                .allWheelRewardSubType(Objects.nonNull(allWheelRewardSubType) ? allWheelRewardSubType.getCode() : "")
                .resultList(Optional.ofNullable(gameRecordList)
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ToVoConverter::toResultVo)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public DiwataMayariGameRecordVo toRecordVo() {
        return null;
    }

}
