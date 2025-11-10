package com.wegame.capi.biz.module.base.game;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * 抽象游戏上下文
 */
@Slf4j
@Component
public abstract class AbstractGameContext<R> {

    /**
     * 获取GamePlay策略名称Map
     *
     * @return 策略名称Map
     */
    public abstract Map<GamePlayTypeEnum, String> playStrategyNameMap();

    /**
     * 获取GamePlay策略Map
     *
     * @return 策略Map
     */
    public abstract Map<String, Game> playStrategyMap();

    /**
     * 获取游戏信息
     *
     * @return 游戏信息
     */
    public abstract GameInfoDo gameInfo();

    public List<R> play(GamePlayDo gamePlayDo) {
        AssertUtil.assertNotNull(gamePlayDo, ErrorCode.ERR_INTER);
        String strategyName = playStrategyNameMap().get(gamePlayDo.getGamePlayType());
        AssertUtil.assertStrNotEmpty(strategyName, ErrorCode.ERR_PARAM, "Not Support GamePlayType");
        Game<R> game = playStrategyMap().get(strategyName);
        AssertUtil.assertNotNull(game, ErrorCode.ERR_PARAM, "Not Support GamePlayType");
        List<R> play = game.play(gamePlayDo);
        // play结果验证
        this.playResultValidate(gamePlayDo);
        return play;
    }

    private void playResultValidate(GamePlayDo gamePlayDo) {
        Long overallRewardAmount = gamePlayDo.getOverallRewardAmount();
        TemplateMultipleRange templateMultipleRange = gamePlayDo.getTemplateMultipleRange();
        // 上限和下限
        Long payAmount = gamePlayDo.getPayAmount();
        Long betAmount = gamePlayDo.getBetAmount();
        switch (gamePlayDo.getGamePlayType()) {
            case TEMPLATE_PLAY:
                AssertUtil.assertNotNull(overallRewardAmount, ErrorCode.ERR_INTER);
                AssertUtil.assertNotNull(templateMultipleRange, ErrorCode.ERR_INTER);
                Long byDivideAmount = payAmount > 0 ? payAmount : betAmount;
                BigDecimal divideValue = BigDecimal.valueOf(overallRewardAmount).divide(BigDecimal.valueOf(byDivideAmount), 2, RoundingMode.FLOOR);
                if (divideValue.compareTo(templateMultipleRange.getEnd()) > 0) {
                    log.error("AbstractGameContext#playResultValidate,overallRewardAmount more than multiple up limit,stop process.gameplay:{}"
                            , JSON.toJSONString(gamePlayDo));
                    throw new BusinessException(ErrorCode.ERR_INTER);
                } else if (divideValue.compareTo(templateMultipleRange.getStart()) < 0) {
                    log.error("AbstractGameContext#playResultValidate,overallRewardAmount more than multiple low limit,stop process.gameplay:{}"
                            , JSON.toJSONString(gamePlayDo));
                    return;
                }
                return;
            case EVENT_PLAY:
                AssertUtil.assertNotNull(overallRewardAmount, ErrorCode.ERR_INTER);
                // 上限和下限
                Long availableBonus = gamePlayDo.getAvailableBonus();
                if (overallRewardAmount - payAmount > availableBonus) {
                    log.error("AbstractGameContext#playResultValidate,overallRewardAmount more than event up limit,stop process.gameplay:{}"
                            , JSON.toJSONString(gamePlayDo));
                    throw new BusinessException(ErrorCode.ERR_INTER);
                }
        }


    }
}
