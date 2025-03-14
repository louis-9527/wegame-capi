package com.wegame.capi.biz.domain.biz;

import com.wegame.capi.biz.domain.biz.game.IbongAdarnaGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class IbongAdarnaBetRecordDetailDo {

    /**
     * 游戏记录列表
     */
    private List<IbongAdarnaGameRecordDo> gameRecordList;

    public boolean isLastFreeSpins(long freeWheelGameRecordId) {
        IbongAdarnaGameRecordDo recordDo = CollectionUtils.isEmpty(gameRecordList) ? null : gameRecordList.get(gameRecordList.size() - 1);
        return Objects.isNull(recordDo) ? false : Objects.equals(recordDo.getId(), freeWheelGameRecordId);
    }
}
