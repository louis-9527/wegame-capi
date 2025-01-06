package com.wegame.capi.biz.domain.biz;

import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MariaMakilingBetRecordDetailDo {

    /**
     * 游戏记录列表
     */
    private List<MariaMakilingGameRecordDo> gameRecordList;

    public boolean isLastFreeSpins(int freeSpinCount) {
        int size = CollectionUtils.isEmpty(gameRecordList) ? 0 : gameRecordList.size();
        return freeSpinCount == size - 1;
    }

    public long freeSpinCount() {
        return Optional.ofNullable(gameRecordList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(item -> item.getIndex() > 0)
                .count();
    }
}
