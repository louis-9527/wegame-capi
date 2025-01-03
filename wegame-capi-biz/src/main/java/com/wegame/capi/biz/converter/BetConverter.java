package com.wegame.capi.biz.converter;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.domain.*;
import com.wegame.capi.biz.domain.business.bet.*;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.domain.vo.*;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * UserConverter
 *
 * @author generate
 */
public class BetConverter {

    private static final Map<BetSubTypeEnum, Class<?>> mysteriousClassMap = new ImmutableMap.Builder<BetSubTypeEnum, Class<?>>()
            .put(BetSubTypeEnum.NORMAL_BET, MysteriousNormalBetDo.class)
            .put(BetSubTypeEnum.BUY_FREE_SPINS_BET, MysteriousBuyFreeSpinsBetDo.class)
            .put(BetSubTypeEnum.NON_BUY_FREE_SPINS_BET, MysteriousNonBuyFreeSpinsBetDo.class)
            .build();

    private static final Map<GameTypeEnum, Map<BetSubTypeEnum, Class<?>>> gameClassMap = new ImmutableMap.Builder<GameTypeEnum, Map<BetSubTypeEnum, Class<?>>>()
            .put(GameTypeEnum.MARIA_MAKILING, mysteriousClassMap)
            .build();

    public static BetDo requestToDo(BetRequest betRequest) {
        if (Objects.isNull(betRequest)) {
            return null;
        }
        GameTypeEnum gameType = GameTypeEnum.valueOfCode(betRequest.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
        Map<BetSubTypeEnum, Class<?>> map = gameClassMap.get(gameType);
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_PARAM);
        Class clazz = map.get(betRequest.getBetSubType());
        AssertUtil.assertNotNull(clazz, ErrorCode.ERR_PARAM);
        return BetDo.builder()
                .gameId(betRequest.getGameId())
                .gameType(GameTypeEnum.valueOfCode(betRequest.getGameId()))
                .betType(BetTypeEnum.valueOfCode(betRequest.getBetType()))
                .betSubType(BetSubTypeEnum.valueOfCode(betRequest.getBetSubType()))
                .betDetail(FastJsonUtils.fromJSONString(FastJsonUtils.toJSONString(betRequest.getBetDetail()), clazz))
                .build();
    }

    public static <R> BetResultVo doToVo(GameBetResultDo<R> betResult) {
        BetResultVo betBaseVo = new BetResultVo();
        betBaseVo.setGameRecordId(String.valueOf(betResult.getGameRecordId()));
        betBaseVo.setGameId(betResult.getGameId());
        betBaseVo.setBetType(betResult.getBetType().getCode());
        betBaseVo.setBetSubType(betResult.getBetSubType().getCode());
        betBaseVo.setCredit(10000L);
        if (betResult.resultDetail instanceof MysteriousNormalBetResultDo) {
            MysteriousNormalBetResultDo normalResult = (MysteriousNormalBetResultDo) betResult.resultDetail;
            GameBetResultDetailDo detailDo = normalResult.getResultDetailDo();
            betBaseVo.setResultDetail(detailToNormalVo(detailDo));
        } else if (betResult.resultDetail instanceof MysteriousNonBuyFreeSpinsBetResultDo) {
            MysteriousNonBuyFreeSpinsBetResultDo normalResult = (MysteriousNonBuyFreeSpinsBetResultDo) betResult.resultDetail;
            List<Integer> freeSpinIds = normalResult.getFreeSpinIds();
            betBaseVo.setResultDetail(freeSpinIds);
        } else if (betResult.resultDetail instanceof MysteriousBuyFreeSpinsBetResultDo) {
            MysteriousBuyFreeSpinsBetResultDo buyResult = (MysteriousBuyFreeSpinsBetResultDo) betResult.resultDetail;
            List<Integer> freeSpinIds = buyResult.getFreeSpinIds();
            GameBetBuyVo betBuyVo = detailToBuyVo(buyResult.getResultDetailDo());
            betBuyVo.setFreeSpinIds(freeSpinIds);
            betBaseVo.setResultDetail(betBuyVo);
        }
        return betBaseVo;
    }

    public static GameBetNormalVo detailToNormalVo(GameBetResultDetailDo detailDo) {
        GameBetNormalVo normalVo = new GameBetNormalVo();
        normalVo.setPayOutAmountType(detailDo.getRewardAmountType() == null ? null : detailDo.getRewardAmountType().getCode());
        normalVo.setPayOutTotalAmount(detailDo.getRewardAmount());
        normalVo.setSpinsResult(convertToIntegerArray(detailDo.getGameResult()));
        List<GameBetPayOutDetailVo> detailVos = new ArrayList<>();
        List<GameBetResultDetailDo.RewardResult> rewardResults = detailDo.getRewardResults();
        if (CollectionUtils.isNotEmpty(rewardResults)) {
            for (GameBetResultDetailDo.RewardResult rewardResult : rewardResults) {
                GameBetPayOutDetailVo detailVo = new GameBetPayOutDetailVo();
                List<String> rewardSymbolIdList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(rewardResult.getRewardSymbolList())) {
                    rewardResult.getRewardSymbolList().forEach(symbol -> rewardSymbolIdList.add(symbol.getId()));
                }
                detailVo.setRewardSymbolIdList(rewardSymbolIdList);
                detailVo.setMultiple(rewardResult.getMultiple());
                detailVo.setOdds(rewardResult.getOdds());
                detailVo.setPayOutAmount(rewardResult.getRewardAmount());
                List<SymbolIndexVo> symbolIndexVo = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(rewardResult.getRewardIndex())) {
                    rewardResult.getRewardIndex().forEach(indexDo -> {
                        symbolIndexVo.add(SymbolIndexVo.build(indexDo.getRowIndex(), indexDo.getColumIndex()));
                    });
                }

                detailVo.setIndex(symbolIndexVo);
                detailVos.add(detailVo);
            }
        }

        normalVo.setPayOutResult(detailVos);
        return normalVo;
    }


    public static GameBetBuyVo detailToBuyVo(GameBetResultDetailDo detailDo) {
        GameBetBuyVo normalVo = new GameBetBuyVo();
        normalVo.setPayOutAmountType(detailDo.getRewardAmountType() == null ? null : detailDo.getRewardAmountType().getCode());
        normalVo.setPayOutTotalAmount(detailDo.getRewardAmount());
        normalVo.setSpinsResult(convertToIntegerArray(detailDo.getGameResult()));
        List<GameBetPayOutDetailVo> detailVos = new ArrayList<>();
        List<GameBetResultDetailDo.RewardResult> rewardResults = detailDo.getRewardResults();
        if (CollectionUtils.isNotEmpty(rewardResults)) {
            for (GameBetResultDetailDo.RewardResult rewardResult : rewardResults) {
                GameBetPayOutDetailVo detailVo = new GameBetPayOutDetailVo();
                List<String> rewardSymbolIdList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(rewardResult.getRewardSymbolList())) {
                    rewardResult.getRewardSymbolList().forEach(symbol -> rewardSymbolIdList.add(symbol.getId()));
                }
                detailVo.setRewardSymbolIdList(rewardSymbolIdList);
                detailVo.setMultiple(rewardResult.getMultiple());
                detailVo.setOdds(rewardResult.getOdds());
                detailVo.setPayOutAmount(rewardResult.getRewardAmount());
                List<SymbolIndexVo> symbolIndexVo = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(rewardResult.getRewardIndex())) {
                    rewardResult.getRewardIndex().forEach(indexDo -> {
                        symbolIndexVo.add(SymbolIndexVo.build(indexDo.getRowIndex(), indexDo.getColumIndex()));
                    });
                }

                detailVo.setIndex(symbolIndexVo);
                detailVos.add(detailVo);
            }
        }

        normalVo.setPayOutResult(detailVos);
        return normalVo;
    }


    public static String[][] convertToIntegerArray(SymbolDo[][] symbolDoArray) {
        if (symbolDoArray == null) {
            return null;
        }
        int rows = symbolDoArray.length;
        int cols = symbolDoArray[0].length;
        String[][] integerArray = new String[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                integerArray[i][j] = symbolDoArray[i][j].getId();
            }
        }
        return integerArray;
    }

}