package com.wegame.capi.api.domain.req.singlewallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wegame.capi.api.domain.req.BaseReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 拳击下注请求参数
 *
 * @author jayni
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BoxingBetReq extends BaseReq {
    private static final long serialVersionUID = -6363877653418L;

    /**
     * 投注金额
     * 当前投注金额
     * 必传:true
     */
    private BigDecimal betAmount;
    /**
     * 下注详情
     * 必传：true
     */
    private BoxingBetDetails betDetails;
    /**
     * 赌局的赌场ID属于
     * 看从哪个平台上线：cp,igo
     * 联调暂时传50（cp）
     * 后续讨论如何规划拳击入口，再确定，最终由单钱包定义提供@berkin@bruce@bj
     * 仅用于数据报表
     * 必传:true
     */
    @JsonProperty("cId")
    private String cId;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoxingBetDetails implements Serializable {
        private static final long serialVersionUID = -636332367418L;

        /**
         * 游戏唯一id
         * 根据游戏唯一id:alex那边提供,不区分环境
         * 表达类型的id:拳击
         * 必传：true
         */
        private String gameId;
        /**
         * 游戏记录唯一ID
         * 必传：true
         * roundNo:整局/回合 matchId
         */
        private String roundNo;
        /**
         * 游戏的总赛事ID
         * 必传：true
         * roundNo:整局/回合 boxingId
         */
        private String fullRoundNo;

        /**
         * 打赌时间【Unix时间戳，以秒为单位】
         * 当前时间戳
         * 必传：true
         */
        private Long wagersDate;
        /**
         * 最小数组1的归属玩家名单
         * 必传：true
         * <p>
         * 红方拳手姓名
         */
        private List<String> homePlayersName;
        /**
         * 最小数组1的球员名单
         * 必传：true
         * <p>
         * 蓝方拳手姓名
         */
        private List<String> awayPlayersName;
        /**
         * 投注类型列表
         * 必传：true
         */
        private List<BoxingBetTypeInfo> betMap;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoxingBetTypeInfo implements Serializable {
        private static final long serialVersionUID = -6363346787618L;

        /**
         * 必传：true
         * 1.红方
         * 2.蓝方
         * 3.平
         */
        private Integer betType;

        /**
         * Amount of the bet amount
         * 必传：true
         */
        private BigDecimal betMoney;
    }

}
