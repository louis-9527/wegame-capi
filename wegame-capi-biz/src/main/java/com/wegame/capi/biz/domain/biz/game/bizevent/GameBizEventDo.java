package com.wegame.capi.biz.domain.biz.game.bizevent;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.symbol.IndexDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexFactory;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.domain.vo.GameBizEventVo;
import com.wegame.capi.common.enums.BizEventEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 游戏业务事件模型
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameBizEventDo implements Serializable, ToVoConverter<GameBizEventVo, GameBizEventVo> {
    private static final long serialVersionUID = -2678234710848L;
    /**
     * 事件排序
     */
    private Integer sort;
    /**
     * 事件类型
     *
     * @see BizEventEnum
     */
    private BizEventEnum bizEventType;
    /**
     * 事件影响的倍率
     */
    private BigDecimal eventMultiple;
    /**
     * 变动元素
     */
    private SymbolDo changeToSymbol;
    /**
     * 坐标
     */
    private List<IndexDo> indexDoList;
    /**
     * ext
     */
    private Object extInfo;

    public boolean hasSymbolChange() {
        return CollectionUtil.isNotEmpty(this.indexDoList);
    }

    @Override
    public GameBizEventVo toResultVo() {
        return GameBizEventVo.builder()
                .sort(this.sort)
                .bizEventType(this.bizEventType.getType())
                .eventMultiple(this.eventMultiple)
                .changeToSymbol(this.changeToSymbol != null ? this.changeToSymbol.getId() : null)
                .indexVoList(CollectionUtil.isEmpty(this.indexDoList) ? null :
                        this.indexDoList.stream().map(IndexFactory::buildVo).collect(Collectors.toList()))
                .extInfo(this.extInfo)
                .build();
    }

    @Override
    public GameBizEventVo toRecordVo() {
        return this.toResultVo();
    }
}

