package com.wegame.capi.biz.domain.biz.symbol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper;
import com.wegame.capi.common.enums.SymbolSizeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mangkukulam游戏元素
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

    /**
     * 形状
     */
    private SymbolSizeEnum size;

    @JsonIgnore
    public int getLineCount() {
        return size.getColumnSpan();
    }

    @JsonIgnore
    public BigDecimal getMultiple() {
        if (!isSpecialWild() || multiples == null || multiples.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // todo 计算所有倍率的总和
        int length = multiples.size();
        BigDecimal res = BigDecimal.ZERO;
        for (int i = 0; i <= length - remainClearTimes; i++) {
            res = res.add(multiples.get(i));
        }
        return res;
    }


    @JsonIgnore
    public boolean isPlaceholderSymbol() {
        return StringUtils.equals(getId(), MangkukulamGameHelper.MANGKUKULAM_SYMBOL_PLACEHOLDER.getId());
    }

    @JsonIgnore
    public boolean isSpecialWild() {
        return StringUtils.equals(getId(), MangkukulamGameHelper.MANGKUKULAM_SPECIAL_WILD.getId());
    }

    public boolean isNormalWild() {
        return StringUtils.equals(getId(), MangkukulamGameHelper.MANGKUKULAM_Wild.getId());
    }

    /**
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
     * @param symbol 元素
     * @return Mangkukulam  SymbolDo
     */
    public static MangkukulamSymbolDo buildFromSymbol(SymbolDo symbol) {
        return MangkukulamSymbolDo.builder()
                .id(symbol.getId())
                .name(symbol.getName())
                .symbolType(symbol.getSymbolType())
                .size(SymbolSizeEnum.S)
                .symbolFireBorder(false)
                .build();
    }

    /**
     * 从symbol产生一个新元素
     *
     * @param symbol 元素
     * @return Mangkukulam  SymbolDo
     */
    public static MangkukulamSymbolDo buildFromSymbol(SymbolDo symbol, boolean symbolFireBorder) {
        return MangkukulamSymbolDo.builder()
                .id(symbol.getId())
                .name(symbol.getName())
                .symbolType(symbol.getSymbolType())
                .size(SymbolSizeEnum.S)
                .symbolFireBorder(symbolFireBorder)
                .build();
    }

    /**
     * 从symbol产生一个新元素
     *
     * @param remainClearTimes 剩余消除次数
     * @return MangkukulamSymbolDo
     */
    public static MangkukulamSymbolDo buildSpecialWild(int remainClearTimes) {
        return MangkukulamSymbolDo.builder()
                .id(MangkukulamGameHelper.MANGKUKULAM_SPECIAL_WILD.getId())
                .name(MangkukulamGameHelper.MANGKUKULAM_SPECIAL_WILD.getName())
                .symbolType(MangkukulamGameHelper.MANGKUKULAM_SPECIAL_WILD.getSymbolType())
                .symbolFireBorder(false)
                .remainClearTimes(remainClearTimes)
                .size(SymbolSizeEnum.XXL)
                .build();
    }
}

