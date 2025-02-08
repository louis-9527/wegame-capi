package com.wegame.capi.biz.domain.biz;

import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
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
public class MalakasBetRecordDetailDo {

    /**
     * 游戏记录列表
     */
    private List<MalakasGameRecordDo> gameRecordList;

    public boolean isLastFreeSpins(int freeSpinsId) {
        MalakasGameRecordDo recordDo = CollectionUtils.isEmpty(gameRecordList) ? null : gameRecordList.get(gameRecordList.size() - 1);
        return Objects.isNull(recordDo) ? false : Objects.equals(recordDo.getIndex(), freeSpinsId);
    }

    public long freeSpinCount() {
        return Optional.ofNullable(gameRecordList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(item -> item.getIndex() > 0)
                .count();
    }
}
