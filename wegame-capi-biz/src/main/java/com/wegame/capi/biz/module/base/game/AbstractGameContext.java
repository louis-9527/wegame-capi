package com.wegame.capi.biz.module.base.game;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public List<R> play(BetDo betDo, GamePlayDo gamePlayDo) {
        AssertUtil.assertNotNull(gamePlayDo, ErrorCode.ERR_INTER);
        String strategyName = playStrategyNameMap().get(gamePlayDo.getGamePlayType());
        AssertUtil.assertStrNotEmpty(strategyName, ErrorCode.ERR_PARAM, "Not Support GamePlayType");
        Game<R> game = playStrategyMap().get(strategyName);
        AssertUtil.assertNotNull(game, ErrorCode.ERR_PARAM, "Not Support GamePlayType");
        List<R> play = game.play(betDo, gamePlayDo);
        // play结果验证
        this.playResultValidate(gamePlayDo);
        return play;
    }

    private void playResultValidate(GamePlayDo gamePlayDo) {
        try {
            if (Objects.isNull(gamePlayDo.getBetPromotionType()) || Objects.equals(gamePlayDo.getBetPromotionType(), BetPromotionTypeEnum.STAR_CARD)) {
                // log.info("AbstractGameContext#playResultValidate,promotionType is null or start card continue process, gamePlay:{}", JSON.toJSONString(gamePlayDo));
                return;
            }

            Long overallRewardAmount = gamePlayDo.getOverallRewardAmount();
            TemplateMultipleRange templateMultipleRange = gamePlayDo.getTemplateMultipleRange();
            // 上限和下限
            Long payAmount = gamePlayDo.getPayAmount();
            Long betAmount = gamePlayDo.getBetAmount();
            switch (gamePlayDo.getGamePlayType()) {
                case TEMPLATE_PLAY:
                    AssertUtil.assertNotNull(overallRewardAmount, ErrorCode.ERR_INTER);
                    AssertUtil.assertNotNull(templateMultipleRange, ErrorCode.ERR_INTER);
                    if (Objects.equals(gamePlayDo.getGameType(), GameTypeEnum.MANGKUKULAM)) {
                        //todo 添加mangkukulam的校验
                        mangkukulamTemplatePlayValidate(gamePlayDo);
                        return;
                    }
                    Long byDivideAmount = payAmount > 0 ? payAmount : betAmount;
                    BigDecimal divideValue = BigDecimal.valueOf(overallRewardAmount).divide(BigDecimal.valueOf(byDivideAmount), 2, RoundingMode.FLOOR);
                    if (divideValue.compareTo(templateMultipleRange.getEnd()) > 0) {
                        log.error("playResultValidate,overallRewardAmount more than multiple up limit,stop process.gameplay:{}", gamePlayDo);
                        throw new BusinessException(ErrorCode.ERR_INTER);
                    } else if (divideValue.compareTo(templateMultipleRange.getStart()) < 0) {
                        log.error("playResultValidate,overallRewardAmount more than multiple low limit,continue process.gameplay:{}", gamePlayDo);
                        return;
                    }
                    return;
                case EVENT_PLAY:
                    AssertUtil.assertNotNull(overallRewardAmount, ErrorCode.ERR_INTER);
                    // 上限和下限
                    Long availableBonus = gamePlayDo.getAvailableBonus();
                    if (overallRewardAmount - payAmount > availableBonus) {
                        log.error("playResultValidate,overallRewardAmount more than event up limit,stop process.gameplay:{}", gamePlayDo);
                        throw new BusinessException(ErrorCode.ERR_INTER);
                    }
            }
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            log.error("playResultValidate error,gamePlay:{}", gamePlayDo, e);
        }
    }

    private void mangkukulamTemplatePlayValidate(GamePlayDo gamePlayDo) {
        if (!Objects.equals(gamePlayDo.getBetPromotionType(), BetPromotionTypeEnum.NONE)) {
            return;
        }
        //用于计算倍率的金额
        Long actualCalcAmount = null;
        if (BetSubTypeEnum.isFirstStep(gamePlayDo.getBetSubType().getCode())) {
            // 首轮用支付金额计算
            actualCalcAmount = gamePlayDo.getPayAmount();
        } else if (BetSubTypeEnum.isSecondStep(gamePlayDo.getBetSubType().getCode())){
            // 第二轮生成用投注金额计算
            actualCalcAmount = gamePlayDo.getBetAmount();
        }
        AssertUtil.assertNotNull(actualCalcAmount, ErrorCode.ERR_INTER);
        // 第二轮 || 连续生成两轮 (因为只要生成免费轮，首轮的赔付金额都为0)都按照投注金额计算倍率
        BigDecimal divideValue = BigDecimal.valueOf(gamePlayDo.getOverallRewardAmount()).divide(BigDecimal.valueOf(actualCalcAmount), 2, RoundingMode.FLOOR);
        TemplateMultipleRange range = gamePlayDo.getTemplateMultipleRange();
        AssertUtil.assertTrue(range.getEnd().compareTo(divideValue) >= 0, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(range.getStart().compareTo(divideValue) <= 0, ErrorCode.ERR_INTER);
    }
}
