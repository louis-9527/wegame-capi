package com.wegame.capi.biz.domain.biz.reward;

import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * maria推送中大奖do
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MariaNotifyDo implements Serializable {
    private static final long serialVersionUID = -22633876654348L;
    /**
     * 游戏类型
     */
    private GameTypeEnum gameType;
    /**
     * 正常玩游戏记录id
     */
    private Long normalGmeRecordId;
    /**
     * 免费轮游戏记录id
     */
    private Long freeSpinsGameRecordId;


}
