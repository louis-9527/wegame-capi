package com.wegame.capi.biz.module.bakunawa.reward;

import cn.hutool.core.collection.CollectionUtil;
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
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.*;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.wegame.capi.biz.module.bakunawa.BakunawaGameHelper.*;
import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
@Component("bakunawaRewardHandler")
public class BakunawaRewardHandler extends AbstractRewardHandler<SymbolDo> {
    List<SymbolDo> REWARD_SYMBOL_ARR = Lists.newArrayList(BAKUNAWA_Symbol1, BAKUNAWA_Symbol5, BAKUNAWA_Symbol10);


    @Autowired
    MultipleCalculateStrategy bakunawaMultipleCalculateStrategy;
    @Autowired
    BakunawaConfig bakunawaConfig;
    @Autowired
    RandomService randomService;

    public RewardResultDo<SymbolDo> handle(RewardHandleDo<SymbolDo> rewardHandle) {
        // 1、业务校验
        validate(rewardHandle);
        // 2、计算中奖线
        List<RewardLineDo<SymbolDo>> rewardLineList = calcRewardLineList(rewardHandle.getGameInfo(), rewardHandle.getWheel());
        // 3、计算滚盘倍率
        BigDecimal totalMultiple = bakunawaMultipleCalculateStrategy.calculateMultiple(rewardHandle, rewardLineList);
        // 4、 计算倍率盘倍率
        if (CollectionUtil.isEmpty(rewardLineList)) {
            // 未中奖
            return RewardResultDo.<SymbolDo>builder()
                    .betAmount(rewardHandle.getBetAmount())
                    .rewardType(RewardTypeEnum.NONE)
                    .rewardSubType(RewardSubTypeEnum.NONE)
                    .totalRewardAmount(0L)
                    .totalMultiple(BigDecimal.ZERO)
                    .hasRewardFreeWheel(false)
                    .rewardLineDetailList(null)
                    .build();
        }
        AssertUtil.assertTrue(rewardLineList.size() == 1, ErrorCode.ERR_INTER);

        // 4、中奖线处理

        // 中奖总倍率
        // 3、计算基础倍率-> 根据wheel计算基础倍率
        BigDecimal basicMultiple = bakunawaMultipleCalculateStrategy.calculateMultiple(rewardHandle, rewardLineList);
        // 4、计算倍率盘倍率
        Pair<MultiplePieTypeEnum, BigDecimal> pieMultiple = bakunawaMultipleCalculateStrategy.calculateMultiplePie(rewardHandle);

        // 中奖坐标列表
        List<RewardLineDetailDo<SymbolDo>> rewardLineDetailList = Lists.newArrayList();
        RewardLineDo<SymbolDo> rewardLine = rewardLineList.get(0);
        // 计算（中奖线）中奖金额：中奖线中奖金额计算公式 = 投注金额 * 基础倍率 * 倍率盘倍率
        BigDecimal lineRewardAmount = this.calculateLineRewardAmount(rewardLine, rewardHandle.getBetAmount(), basicMultiple, pieMultiple.getValue());
        // 中奖总金额
        long totalRewardAmount = lineRewardAmount.longValue();
        // 中奖线明细列表
        rewardLineDetailList.add(RewardLineDetailDo.<SymbolDo>builder()
                .betAmount(rewardHandle.getBetAmount())
                .rewardAmount(lineRewardAmount.longValue())
                .odds(BigDecimal.ZERO)
                .multiple(basicMultiple)
                .rewardLineType(rewardLine.getRewardLineType())
                .rewardLineSubType(rewardLine.getRewardLineSubType())
                .rewardLineCount(rewardLine.getRewardLineCount())
                .rewardSymbol(null)
                .rewardSymbolList(rewardLine.getRewardSymbolList())
                .build());
        // 5.构建中奖结果
        return RewardResultDo.<SymbolDo>builder()
                .rewardType(RewardTypeEnum.valueOfRewardLineCont(rewardLineList.size()))
                .rewardSubType(RewardHelper.calculateRewardSubType(rewardHandle.getBetAmount(), totalRewardAmount))
                .betAmount(rewardHandle.getBetAmount())
                .totalRewardAmount(totalRewardAmount)
                .hasRewardFreeWheel(false)
                .rewardLineDetailList(null)
                .totalMultiple(basicMultiple.multiply(pieMultiple.getValue()))
                .build();
    }

    /**
     * 计算中奖线中奖金额
     * 中奖线中奖金额计算公式 = 投注金额 * 基础倍率 * 倍率盘倍率
     *
     * @param rewardLine 中奖线
     * @param betAmount  投注金额
     * @param odds       赔率 / 赔率盘倍率
     * @param multiple   基础倍率
     * @return 中奖金额
     */
    @Override
    public BigDecimal calculateLineRewardAmount(RewardLineDo rewardLine, Long betAmount, BigDecimal odds, BigDecimal multiple) {
        return BigDecimal.valueOf(betAmount)
                .multiply(odds)
                .multiply(multiple);
    }

    /**
     * 计算中奖线
     * 备注: 如果存在中奖构建单条中奖线，否则返回空列表
     * @param gameInfo
     * @param wheel
     * @return
     */
    private List<RewardLineDo<SymbolDo>> calcRewardLineList(GameInfoDo gameInfo, WheelDo<SymbolDo> wheel) {
        SymbolDo[][] symbolArr = wheel.getSymbolArr();
        List<RewardLineDo<SymbolDo>> rewardLineDoList = Lists.newArrayList();
        // 当前线是否中奖
        if (hasReward(symbolArr, gameInfo)) {
            // 构建中奖线
            rewardLineDoList.add(buildRewardLineDo(symbolArr, gameInfo));
        }
        // 返回中奖线列表
        return rewardLineDoList;
    }

    private boolean hasReward(SymbolDo[][] symbolArr, GameInfoDo gameInfo) {
        boolean hasReward = false;
        for (int column = ONE_COLUM_INDEX; column < gameInfo.getColumn(); column++) {
            for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
                SymbolDo symbolDo = symbolArr[row][column];
                if (REWARD_SYMBOL_ARR.contains(symbolDo)) {
                    hasReward = true;
                    break;
                }
            }
        }
        return hasReward;
    }

    private RewardLineDo<SymbolDo> buildRewardLineDo(SymbolDo[][] symbolArr, GameInfoDo gameInfo) {
        List<RewardSymbolDo<SymbolDo>> rewardSymbolDoList = Lists.newArrayList();
        for (int column = ONE_COLUM_INDEX; column < gameInfo.getColumn(); column++) {
            for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
                SymbolDo symbolDo = symbolArr[row][column];
                if (REWARD_SYMBOL_ARR.contains(symbolDo)) {
                    rewardSymbolDoList.add(RewardSymbolDo.<SymbolDo>builder()
                            .symbol(symbolDo)
                            .index(IndexFactory.buildDo(row, column))
                            .build());
                    break;
                }
            }
        }
        return RewardLineDo.<SymbolDo>builder()
                .rewardLineType(null)
                .rewardLineSubType(RewardLineSubTypeEnum.COMMON)
                .rewardLineNo(null)
                .rewardLineCount(1)
                .rewardSymbol(null)
                .rewardSymbolList(rewardSymbolDoList)
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
        for (int row = 0; row < gameInfo.getRow(); row++) {
            // 校验：滚轮列数
            AssertUtil.assertTrue(symbolArr[row].length == gameInfo.getColumn(), ErrorCode.ERR_PARAM, "symbol colum arr err");
            // 滚轮：所有元素不能为空
            for (SymbolDo symbolDo : symbolArr[row]) {
                AssertUtil.assertNotNull(symbolDo, ErrorCode.ERR_PARAM, "all symbol can not be null");
            }
        }
    }
}
