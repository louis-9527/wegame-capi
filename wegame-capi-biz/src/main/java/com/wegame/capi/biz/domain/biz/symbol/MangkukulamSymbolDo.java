package com.wegame.capi.biz.domain.biz.symbol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * lamang游戏元素
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(of = {"id"}, callSuper = true)
public class MangkukulamSymbolDo extends SymbolDo {
    private static final long serialVersionUID = -226862643663710848L;

    /**
     * 元素火框
     */
    private boolean symbolFireBorder;

    /**
     * 剩余清除次数, 只有特殊wild才有
     */
    private int remainClearTimes;

    /**
     * 倍率列表， 只有特殊wild才有
     */
    private List<BigDecimal> multiples;

    @JsonIgnore
    public BigDecimal getMultiple() {
        // todo 根据消除次数获取倍率和
        BigDecimal total = BigDecimal.ZERO;

        return total;
    }


    @JsonIgnore
    public boolean isPlaceholderSymbol() {
        return StringUtils.equals(getId(), MangkukulamGameHelper.MANGKUKULAM_SYMBOL_PLACEHOLDER.getId());
    }

    public boolean isSpecialWild() {
        return StringUtils.equals(getId(), MangkukulamGameHelper.MANGKUKULAM_SPECIAL_WILD.getId());
    }

    /**
     * 复制元素，将newSymbol置为false
     * 新回合复制元素使用该方法
     *
     * @return MangkukulamSymbolDo
     */
    @Override
    public MangkukulamSymbolDo copy() {
        return MangkukulamSymbolDo.builder()
                .id(getId())
                .name(getName())
                .symbolType(getSymbolType())
                .symbolFireBorder(isSymbolFireBorder())
                .remainClearTimes(remainClearTimes)
                .multiples(multiples)
                .build();
    }

    /**
     * 从symbol产生一个新元素
     *
     * @param symbol     元素
     * @param fireBorder 是否有火框
     * @return MangkukulamSymbolDo
     */
    public static MangkukulamSymbolDo buildFromSymbol(SymbolDo symbol, boolean fireBorder) {
        return MangkukulamSymbolDo.builder()
                .id(symbol.getId())
                .name(symbol.getName())
                .symbolType(symbol.getSymbolType())
                .symbolFireBorder(fireBorder)
                .build();
    }

    /**
     * 从symbol产生一个新元素
     *
     * @param multiples        倍率列表
     * @param remainClearTimes 剩余消除次数
     * @return MangkukulamSymbolDo
     */
    public static MangkukulamSymbolDo buildSpecialWild(List<BigDecimal> multiples, int remainClearTimes) {
        return MangkukulamSymbolDo.builder()
                .id(MangkukulamGameHelper.MANGKUKULAM_SPECIAL_WILD.getId())
                .name(MangkukulamGameHelper.MANGKUKULAM_SPECIAL_WILD.getName())
                .symbolType(MangkukulamGameHelper.MANGKUKULAM_SPECIAL_WILD.getSymbolType())
                .symbolFireBorder(false)
                .multiples(multiples)
                .remainClearTimes(remainClearTimes)
                .build();
    }
}

