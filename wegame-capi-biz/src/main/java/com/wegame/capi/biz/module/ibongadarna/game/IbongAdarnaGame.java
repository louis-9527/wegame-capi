package com.wegame.capi.biz.module.ibongadarna.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.IbongAdarnaGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.IbongSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.wegame.capi.biz.module.ibongadarna.IbongAdarnaGameHelper.IBONG_ADARNA_GAME_INFO;
import static com.wegame.capi.common.Constants.CONST_1000000;
import static com.wegame.capi.common.constants.GameConstants.*;

@Slf4j
@Component
public class IbongAdarnaGame extends AbstractGame<IbongAdarnaGameRecordDo> {

    // ONE_COLUM_INDEX,THREE_COLUM_INDEX,FIVE_COLUM_INDEX顺序不可更改
    private static final List<Integer> SCATTER_COLUM_INDEX_LIST = Arrays.asList(
            ONE_COLUM_INDEX,
            THREE_COLUM_INDEX,
            FIVE_COLUM_INDEX);
    private static final Integer DEFAULT_WHEEL_INDEX = 0;

    @Autowired
    IbongAdarnaConfig ibongAdarnaConfig;
    @Autowired
    RewardHandler ibongAdarnaRewardHandler;


    @Override
    public GameInfoDo getGameInfo() {
        return IBONG_ADARNA_GAME_INFO;
    }

    @Override
    public List<IbongAdarnaGameRecordDo> play(GamePlayDo gamePlay) {
        List<IbongAdarnaGameRecordDo> ret = new ArrayList<>();
        // 获取-游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        WheelDo<SymbolDo> firstWheel = generateNormalWheel(gameInfo, getProbabilisticConfig(gamePlay, false));

        AssertUtil.assertNotNull(firstWheel, ErrorCode.ERR_INTER);
        RewardResultDo<SymbolDo> rewardResult = ibongAdarnaRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, firstWheel, BigDecimal.ZERO));
        IbongAdarnaGameRecordDo record = buildGameRecord(generateGameRecordId(),
                gamePlay.getBetRecordId(),
                firstWheel,
                rewardResult);
        ret.add(record);
        // 判断是否命中免费轮
        return ret;
    }

    /**
     * 正常滚轮生成：生成逻辑无任何限制
     *
     * @param gameInfo 游戏信息
     * @return 滚轮
     */
    private WheelDo<IbongSymbolDo> generateNormalWheel(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
        IbongSymbolDo[][] symbolArr = new IbongSymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
        // 生成整盘随机数(row*column)
        List<Integer> randomNumberList = randomService.randomIntList(1, CONST_1000000, gameInfo.getRow() * gameInfo.getColumn());
        int randomNumberindex = 0;
        for (int colum = 0; colum < gameInfo.getColumn(); colum++) {
            // 一列只能包含一个scatter
            Map<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(colum, probabilisticConfig);
            for (int row = 0; row < gameInfo.getRow(); row++) {
                Integer random = randomNumberList.get(randomNumberindex);
                // 随机生成
                SymbolDo symbolDo = randomNormalSymbol(random, gameInfo, columProbabilisticMap);
                symbolArr[row][colum] = symbolDo;
                randomNumberindex++;
            }
        }
        return WheelDo.<SymbolDo>builder()
                .index(DEFAULT_WHEEL_INDEX)
                .symbolArr(symbolArr).build();
    }

    public Map<String, List<BigDecimal>> getProbabilisticConfig(GamePlayDo gamePlay, boolean isFreeSpinsFlag) {
//        if (isFreeSpinsFlag) {
//            return ibongAdarnaConfig.getProbabilisticConfig().getFreePlayWheelProbabilisticConfig();
//        } else {
//            switch (gamePlay.getBetSubType()) {
//                case NORMAL_BET:
//                case BUY_BET:
//                    return ibongAdarnaConfig.getProbabilisticConfig().getCommonPlayWheelProbabilisticConfig();
//                case FREE_SPINS_NORMAL_BET:
//                case FREE_SPINS_BUY_BET:
//                    return ibongAdarnaConfig.getProbabilisticConfig().getFreePlayWheelProbabilisticConfig();
//                default:
//                    throw new BusinessException(ErrorCode.ERR_PARAM, "NOT SUPPORTED");
//            }
//        }
        return null;
    }

    private static IbongAdarnaGameRecordDo buildGameRecord(Long id,
                                                           Long betRecordId,
                                                           WheelDo<SymbolDo> wheel,
                                                           RewardResultDo<SymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return IbongAdarnaGameRecordDo.builder()
                .id(id)
                .betRecordId(betRecordId)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .build();
    }

    private Integer getWheelNumber() {
//        int randomNumber = randomService.randomInt(1, 100);
//        int startVal = 0;
//        for (Map.Entry<Integer, Integer> entry : CollectionUtils.emptyIfNull(ibongAdarnaConfig.getProbabilisticConfig().getFreeWheelTimesProbabilisticConfig().entrySet())) {
//            if (randomNumber > startVal && randomNumber <= startVal + entry.getValue()) {
//                return entry.getKey();
//            } else {
//                startVal += entry.getValue();
//            }
//        }
        throw new BusinessException(ErrorCode.ERR_INTER);
    }
}
