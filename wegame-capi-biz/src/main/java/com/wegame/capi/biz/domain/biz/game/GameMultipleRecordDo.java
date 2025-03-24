package com.wegame.capi.biz.domain.biz.game;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GameMultipleRecordDo<WD extends SymbolDo> {
    private Long id;

    private Long betRecordId;

    private Long batchId;

    private Long parentId;

    private Integer index;

    private WheelDo<WD> wheelDo;
    /**
     * 滚轮总倍数
     */
    private BigDecimal totalMultiple;
    /**
     * 免费轮ID
     */
    private List<Long> gameRecordIds;

}
