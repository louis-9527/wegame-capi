package com.wegame.capi.biz.domain.biz.game;

import com.esotericsoftware.kryo.util.Null;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Map;

/**
 * 游戏信息模型
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameInfoDo implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 游戏id
     */
    private String id;
    /**
     * 游戏类型
     */
    private GameTypeEnum gameType;
    /**
     * 游戏名称
     */
    private String name;
    /**
     * 行
     */
    private int row;
    /**
     * 列
     */
    private int column;
    /**
     * 元素Map<元素id，元素>
     */
    private Map<String, SymbolDo> symbolMap;

    public SymbolDo getSymbolById(String symbolId) {
        if (symbolMap == null) {
            return null;
        }
        return symbolMap.get(symbolId);
    }
}

