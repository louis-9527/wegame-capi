package com.wegame.capi.api.domain.req.promotion;

import com.wegame.capi.api.domain.req.BaseReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class CreateFreeSpinReq extends BaseReq {
    private static final long serialVersionUID = -5966730127143884402L;
    /** 此交易的唯一参考ID */
    private String refId;
    /** 游戏供应商ID */
    private String providerId;
    /** 渠道ID */
    private String platformId;
    /** 开始日期（YYYY-MM-DDTHH:mm:ssZ） */
    private String startDate;
    /** 结束日期（YYYY-MM-DDTHH:mm:ssZ） */
    private String endDate;
    /** 活动名称 */
    private String eventName;
    /** 游戏编号 ["test","GameTest"] */
    private List<String> gameIDList;
    /** 帐号则自动绑定帐号到该活动 ["plater","p1","p2"] */
    private List<String> playerList;
    /** 免费旋转次数 */
    private Integer freeSpinTimes;
    /** 指定投注额;未指定时,使用provider默认值 */
    private BigDecimal betValue;
    /** 可参与玩家上限 */
    private Integer maxPlayerCount;
    /** Query page starting from 1 */
    private Integer page;
}
