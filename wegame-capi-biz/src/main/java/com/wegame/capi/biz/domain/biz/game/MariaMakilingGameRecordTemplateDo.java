package com.wegame.capi.biz.domain.biz.game;

import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

/**
 * MariaMakilingTemplate游戏记录
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document(collection = "mariamakiling_game_record_template")
public class MariaMakilingGameRecordTemplateDo implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * id
     */
    private Long id;
    /**
     * parentId: 无，则为0
     */
    private Long parentId;
    /**
     * 批次id
     */
    private Long batchId;
    /**
     * 投注记录id
     */
    private Long betRecordId;
    /**
     * 玩家id
     */
    private String playerId;
    /**
     * 玩家名称
     */
    private String playerName;
    /**
     * 序号
     */
    private Integer index;
    /**
     * 免费轮标识 false否 true是
     */
    private Boolean freeWheelFlag;
    /**
     * 滚轮
     */
    private WheelDo<SymbolDo> wheel;
    /**
     * 中奖结果
     */
    private RewardResultDo<SymbolDo> rewardResult;
    /**
     * 免费轮id列表
     */
    private List<Long> gameRecordIds;
    /**
     * 累计中奖总金额
     */
    private Long overallRewardAmount;
    /**
     * 达到触发最大中奖倍数
     */
    private Boolean accumulateReachMaxMultiple;
    /**
     * 最后一轮标识
     */
    private Boolean lastGameFlag;

}