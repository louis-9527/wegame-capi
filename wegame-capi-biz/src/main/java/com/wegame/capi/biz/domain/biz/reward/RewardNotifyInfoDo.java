package com.wegame.capi.biz.domain.biz.reward;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wegame.capi.common.enums.NotifyRewardTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 中大奖结果do
 *
 * @author generate
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RewardNotifyInfoDo implements Serializable {
    private static final long serialVersionUID = -22633876654348L;
    /**
     @JsonIgnoreProperties(ignoreUnknown = true)
     * 游戏id
     *
     * @see com.wegame.capi.client.enums.GameTypeEnum
     */
    private String gameId;
    /**
     * 游戏名称
     */
    private String gameName;
    /**
     * 中奖用户id
     */
    private Long userId;
    /**
     * 中奖用户名称
     */
    private String userName;
    /**
     * 中奖时间
     */
    private Long rewardTime;
    /**
     * 中奖内容
     */
    public String rewardContent;

    public String generatorTipsContent() {
        return null;
    }
}
