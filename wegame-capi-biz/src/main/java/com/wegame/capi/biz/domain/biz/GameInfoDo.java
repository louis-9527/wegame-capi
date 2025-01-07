package com.wegame.capi.biz.domain.biz;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Map;

/**
 * 游戏信息模型
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameInfoDo implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 游戏类型
     */
    private GameTypeEnum gameType;
    /**
     * 游戏id
     */
    private String id;
    /**
     * 游戏名称
     */
    private String name;
    /**
     * 行
     */
    private Integer row;
    /**
     * 列
     */
    private Integer column;
    /**
     * 元素Map<元素id，元素>
     */
    private Map<String, SymbolDo> symbolMap;

    public SymbolDo getSymbolById(String symbolId) {
        return symbolMap.get(symbolId);
    }
}

