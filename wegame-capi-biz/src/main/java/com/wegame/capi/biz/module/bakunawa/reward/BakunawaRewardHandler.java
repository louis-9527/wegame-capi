package com.wegame.capi.biz.module.bakunawa.reward;

import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.BakunawaConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexFactory;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.RewardHelper;
import com.wegame.capi.biz.module.base.multiple.MultipleCalculateStrategy;
import com.wegame.capi.biz.module.base.reward.AbstractRewardHandler;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.MultiplePieTypeEnum;
import com.wegame.capi.common.enums.RewardLineSubTypeEnum;
import com.wegame.capi.common.enums.RewardSubTypeEnum;
import com.wegame.capi.common.enums.RewardTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.wegame.capi.biz.module.bakunawa.BakunawaGameHelper.*;
import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
@Component("bakunawaRewardHandler")
public class BakunawaRewardHandler extends AbstractRewardHandler<SymbolDo> {

    private static final List<SymbolDo> REWARD_SYMBOL_LIST = Lists.newArrayList(BAKUNAWA_Symbol1, BAKUNAWA_Symbol2, BAKUNAWA_Symbol3, BAKUNAWA_Symbol5, BAKUNAWA_Symbol10);

    @Autowired
    BakunawaConfig bakunawaConfig;
    @Autowired
    MultipleCalculateStrategy<SymbolDo> bakunawaMultipleCalculateStrategy;

    public RewardResultDo<SymbolDo> handle(RewardHandleDo<SymbolDo> rewardHandle) {
        // 1、业务校验
        validate(rewardHandle);
        // 2、计算中奖线
        RewardLineDo<SymbolDo> rewardLine = calcRewardLine(rewardHandle.getGameInfo(), rewardHandle.getWheel());
        if (Objects.isNull(rewardLine)) {
            // 未中奖
            return RewardResultDo.<SymbolDo>builder()
                    .betAmount(rewardHandle.getBetAmount())
                    .rewardType(RewardTypeEnum.NONE)
                    .rewardSubType(RewardSubTypeEnum.NONE)
                    .totalRewardAmount(0L)
                    .totalMultiple(BigDecimal.ZERO)
                    .hasRewardFreeWheel(false)
                    .rewardFreeWheelTimes(0)
                    .rewardLineDetailList(null)
                    .build();
        }
        // 3、计算基础倍率-> 根据wheel计算基础倍率
        BigDecimal basicMultiple = bakunawaMultipleCalculateStrategy.calculateMultiple(rewardHandle, Lists.newArrayList(rewardLine));
        // 4、计算倍率盘倍率
        Pair<MultiplePieTypeEnum, BigDecimal> pieMultiple = bakunawaMultipleCalculateStrategy.calculateMultiplePie(rewardHandle);
        // 中奖坐标列表
        List<RewardLineDetailDo<SymbolDo>> rewardLineDetailList = Lists.newArrayList();
        // 计算（中奖线）中奖金额：中奖线中奖金额计算公式 = 投注金额 * 基础倍率 * 倍率盘倍率
        BigDecimal lineRewardAmount = this.calculateLineRewardAmount(rewardLine, rewardHandle.getBetAmount(), basicMultiple, pieMultiple.getRight());
        // 中奖总金额
        long totalRewardAmount = lineRewardAmount.longValue();
        // 中奖线明细列表
        rewardLineDetailList.add(RewardLineDetailDo.<SymbolDo>builder()
                .betAmount(rewardHandle.getBetAmount())
                .rewardAmount(totalRewardAmount)
                .odds(BigDecimal.ZERO)
                .multiple(basicMultiple)
                .multiplePieType(pieMultiple.getKey())
                .multiplePie(pieMultiple.getValue())
                .rewardLineType(rewardLine.getRewardLineType())
                .rewardLineSubType(rewardLine.getRewardLineSubType())
                .rewardLineCount(rewardLine.getRewardLineCount())
                .rewardSymbol(null)
                .rewardSymbolList(rewardLine.getRewardSymbolList())
                .build());
        // 5.构建中奖结果
        return RewardResultDo.<SymbolDo>builder()
                .rewardType(RewardTypeEnum.SINGLE)
                .rewardSubType(RewardHelper.calculateRewardSubType(rewardHandle.getBetAmount(), totalRewardAmount))
                .betAmount(rewardHandle.getBetAmount())
                .totalRewardAmount(totalRewardAmount)
                .hasRewardFreeWheel(false)
                .rewardFreeWheelTimes(0)
                .rewardLineDetailList(rewardLineDetailList)
                .totalMultiple(basicMultiple.multiply(pieMultiple.getValue()))
                .build();
    }

    /**
     * 计算中奖线中奖金额
     * 中奖线中奖金额计算公式 = 投注金额 * 基础倍率 * 倍率盘倍率
     */
    @Override
    public BigDecimal calculateLineRewardAmount(RewardLineDo rewardLine, Long betAmount, BigDecimal basicMultiple, BigDecimal pieMultiple) {
        BigDecimal totalMultiply = basicMultiple.multiply(pieMultiple);
        Integer rewardMaxMultiple = bakunawaConfig.getRewardMaxMultiple();
        if (totalMultiply.intValue() > rewardMaxMultiple) {
            return BigDecimal.valueOf(betAmount * rewardMaxMultiple);
        } else {
            return BigDecimal.valueOf(betAmount).multiply(totalMultiply);
        }
    }

    private RewardLineDo<SymbolDo> calcRewardLine(GameInfoDo gameInfo, WheelDo<SymbolDo> wheel) {
        SymbolDo[][] symbolArr = wheel.getSymbolArr();
        List<RewardSymbolDo<SymbolDo>> rewardSymbolList = Lists.newArrayList();
        for (int column = ONE_COLUM_INDEX; column < gameInfo.getColumn(); column++) {
            SymbolDo symbolDo = symbolArr[ONE_ROW_INDEX][column];
            if (CollectionUtils.isEmpty(rewardSymbolList) && REWARD_SYMBOL_LIST.contains(symbolDo)) {
                rewardSymbolList.add(RewardSymbolDo.<SymbolDo>builder()
                        .symbol(symbolDo)
                        .index(IndexFactory.buildDo(ONE_ROW_INDEX, column))
                        .build());
            } else if (CollectionUtils.isNotEmpty(rewardSymbolList) && !StringUtils.equals(BAKUNAWA_SymbolNull.getId(), symbolDo.getId())) {
                rewardSymbolList.add(RewardSymbolDo.<SymbolDo>builder()
                        .symbol(symbolDo)
                        .index(IndexFactory.buildDo(ONE_ROW_INDEX, column))
                        .build());
            }
        }
        if (CollectionUtils.isEmpty(rewardSymbolList)) {
            return null;
        }
        return RewardLineDo.<SymbolDo>builder()
                .rewardLineType(null)
                .rewardLineSubType(RewardLineSubTypeEnum.COMMON)
                .rewardLineCount(1)
                .rewardSymbol(null)
                .rewardSymbolList(rewardSymbolList)
                .build();
    }

    private void validate(RewardHandleDo<SymbolDo> rewardHandle) {
        AssertUtil.assertNotNull(rewardHandle, ErrorCode.ERR_PARAM, "rewardHandle is null");
        AssertUtil.assertNotNull(rewardHandle.getGameType(), ErrorCode.ERR_INTER, "not support gameType");
        AssertUtil.assertNotNull(rewardHandle.getBetType(), ErrorCode.ERR_PARAM, "not support betType");
        AssertUtil.assertNotNull(rewardHandle.getBetSubType(), ErrorCode.ERR_PARAM, "not support betSubType");
        AssertUtil.assertNotNull(rewardHandle.getWheel(), ErrorCode.ERR_PARAM, "wheel is null");
        AssertUtil.assertNotNull(rewardHandle.getWheel().getIndex(), ErrorCode.ERR_PARAM, "wheel index is null");
        AssertUtil.assertNotNull(rewardHandle.getWheel().getSymbolArr(), ErrorCode.ERR_PARAM, "wheel symbolArr is null");
        AssertUtil.assertTrue(Objects.equals(rewardHandle.getGameType(), GameTypeEnum.BAKUNAWA), ErrorCode.ERR_PARAM, "just support game type of bakunawa");
        AssertUtil.assertTrue(rewardHandle.getBetAmount() > 0, ErrorCode.ERR_PARAM, "bet amount must greater than 0");
        SymbolDo[][] symbolArr = rewardHandle.getWheel().getSymbolArr();
        // 校验：滚轮行数
        GameInfoDo gameInfo = rewardHandle.getGameInfo();
        AssertUtil.assertTrue(symbolArr.length == gameInfo.getRow(), ErrorCode.ERR_PARAM, "symbol arr err");
        for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
            // 校验：滚轮列数
            AssertUtil.assertTrue(symbolArr[row].length == gameInfo.getColumn(), ErrorCode.ERR_PARAM, "symbol colum arr err");
            // 滚轮：所有元素不能为空
            for (SymbolDo symbolDo : symbolArr[row]) {
                AssertUtil.assertNotNull(symbolDo, ErrorCode.ERR_PARAM, "all symbol can not be null");
            }
        }
    }
}
