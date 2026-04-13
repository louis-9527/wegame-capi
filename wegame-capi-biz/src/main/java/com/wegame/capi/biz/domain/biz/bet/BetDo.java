package com.wegame.capi.biz.domain.biz.bet;

import com.wegame.capi.biz.domain.biz.bizinterfere.BizInterfereContextDo;
import com.wegame.capi.biz.domain.biz.game.GameOperationEventDo;
import com.wegame.capi.biz.domain.biz.promotion.RewardDetailDo;
import com.wegame.capi.client.enums.*;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BetDo implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 游戏Id
     */
    private String gameId;
    /**
     * 游戏类型
     */
    private GameTypeEnum gameType;
    /**
     * 投注类型
     */
    private BetTypeEnum betType;
    /**
     * 投注子类型
     */
    private BetSubTypeEnum betSubType;
    /////////////////
    /**
     * 投注金额
     */
    private Long betAmount;
    /**
     * 支付金额
     */
    private Long payAmount;
    /**
     * parent投注记录id：用于正常投注获得的免费轮投注
     */
    private Long parentBetRecordId;
    /**
     * 投注记录id
     */
    private Long betRecordId;
    /**
     * 免费轮游戏记录id
     */
    private Long gameRecordId;
    /**
     * 投注营销类型：默认无
     */
    BetPromotionTypeEnum betPromotionType;
    /////////////// 干预事件投注 /////////////
    /**
     * 干预事件投注
     */
    private GameOperationEventDo gameOperationEvent;
    /////////////// 星卡投注 ///////////////
    /**
     * 使用星卡
     */
    private boolean useStarCard;
    /**
     * 奖励配置id
     */
    private String rewardConfigId;
    /**
     * Reward id
     */
    private String rewardId;
    /**
     * （星卡）奖励详情
     */
    private RewardDetailDo rewardDetail;
    /////////////// 预定投注 ///////////////
    /**
     * 使用预定投注
     */
    private boolean useFreeSpin;
    /**
     * 预定投注Id
     */
    private String campaignId;
    /**
     * gamePlayType
     */
    private GamePlayTypeEnum gamePlayType;
    /////////////// 波动选择 ///////////////
    private VolatilityTypeEnum volatility;

    /**
     * 业务干预上下文
     * 参数和结果接受判断
     */
    private BizInterfereContextDo bizInterfereContext;

    /**
     * sirena 下一局模板id
     */
    private Long nextTemplateId;

    /**
     * sirena 下一局使用esIndex
     */
    private String templateType;
}

