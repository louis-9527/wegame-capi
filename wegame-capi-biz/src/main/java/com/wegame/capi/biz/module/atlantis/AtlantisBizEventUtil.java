package com.wegame.capi.biz.module.atlantis;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.bizevent.GameBizEventDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexFactory;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.common.enums.BizEventEnum;
import com.wegame.capi.common.utils.SecureRandomUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
public class AtlantisBizEventUtil {

    /**
     * 内部不对nextSymbolArr 做修改
     *
     * @param bizEventEnum  bizEventEnum
     * @param nextSymbolArr nextSymbolArr
     * @param sort          排序
     * @return GameBizEventDo
     */
    public static GameBizEventDo handleBizEvent(BizEventEnum bizEventEnum, SymbolDo[][] nextSymbolArr, int sort) {
        switch (bizEventEnum) {
            case EVENT_NONE:
                return null;
            case EVENT_SCATTER:
                return handleEventScatter(nextSymbolArr, sort);
            case EVENT_WILD:
                return handleEventWild(nextSymbolArr, sort);
            case EVENT_BOM:
                return handleEventBom(nextSymbolArr, sort);
            case EVENT_TRIDENT:
                return handleEventTrident(nextSymbolArr, sort);
            case EVENT_GOLDEN_TANK:
                return handleEventGoldenTank(nextSymbolArr, sort);
            case EVENT_GOLDEN_COW:
                return handleEventGoldenCow(sort);
            default:
                throw new BusinessException(ErrorCode.ERR_INTER, "no support biz event");
        }
    }

    /**
     * 黄金奶牛，当前收益乘以 2-5倍。
     *
     * @param sort 排序
     * @return GameBizEventDo
     * @see BizEventEnum#EVENT_GOLDEN_COW
     */
    public static GameBizEventDo handleEventGoldenCow(int sort) {
        return GameBizEventDo.builder()
                .sort(sort)
                .bizEventType(BizEventEnum.EVENT_GOLDEN_COW)
                .eventMultiple(BigDecimal.valueOf(SecureRandomUtil.randomIntInRange(2, 5)))
                .build();
    }

    /**
     * 黄金战车，冲撞棋盘，将1-3row（横排） 变为wild
     *
     * @param nextSymbolArr nextSymbolArr
     * @param sort          排序
     * @return GameBizEventDo
     * @see BizEventEnum#EVENT_GOLDEN_TANK
     */
    private static GameBizEventDo handleEventGoldenTank(SymbolDo[][] nextSymbolArr, int sort) {
        List<IndexDo> toWildIndexList = Lists.newArrayList();
        GameInfoDo gameInfo = AtlantisGameHelper.ATLANTIS_GAME_INFO;
        for (int row = 0; row < 3; row++) {
            for (int colum = 0; colum < gameInfo.getColumn(); colum++) {
                toWildIndexList.add(IndexFactory.buildDo(row, colum));
            }
        }

        return GameBizEventDo.builder()
                .sort(sort)
                .bizEventType(BizEventEnum.EVENT_GOLDEN_TANK)
                .changeToSymbol(AtlantisGameHelper.ATLANTIS_Wild)
                .indexDoList(toWildIndexList)
                .build();

    }

    /**
     * 三叉戟，引发潮汐冲刷棋盘，将1-3reel（竖列）变为wild。
     *
     * @param nextSymbolArr nextSymbolArr
     * @param sort          排序
     * @return GameBizEventDo
     * @see BizEventEnum#EVENT_TRIDENT
     */
    private static GameBizEventDo handleEventTrident(SymbolDo[][] nextSymbolArr, int sort) {
        List<IndexDo> toWildIndexList = Lists.newArrayList();
        GameInfoDo gameInfo = AtlantisGameHelper.ATLANTIS_GAME_INFO;
        for (int colum = 0; colum < 3; colum++) {
            for (int row = 0; row < gameInfo.getRow(); row++) {
                toWildIndexList.add(IndexFactory.buildDo(row, colum));
            }
        }

        return GameBizEventDo.builder()
                .sort(sort)
                .bizEventType(BizEventEnum.EVENT_TRIDENT)
                .changeToSymbol(AtlantisGameHelper.ATLANTIS_Wild)
                .indexDoList(toWildIndexList)
                .build();

    }

    /**
     * 炸弹，将棋盘中数量最少的棋子清除（不触发奖励）
     *
     * @param nextSymbolArr nextSymbolArr
     * @param sort          排序
     * @return GameBizEventDo
     */
    private static GameBizEventDo handleEventBom(SymbolDo[][] nextSymbolArr, int sort) {
        // 统计盘面中的棋子 key:symbol,value:对应symbol的坐标列表
        Map<SymbolDo, List<IndexDo>> symbolIndexMap = new HashMap<>();
        for (int row = 0; row < nextSymbolArr.length; row++) {
            for (int column = 0; column < nextSymbolArr[row].length; column++) {
                SymbolDo symbolDo = nextSymbolArr[row][column];
                List<IndexDo> indexDos = symbolIndexMap.get(symbolDo);
                if (CollectionUtil.isEmpty(indexDos)) {
                    symbolIndexMap.put(symbolDo, Lists.newArrayList(IndexFactory.buildDo(row, column)));
                } else {
                    indexDos.add(IndexFactory.buildDo(row, column));
                }
            }
        }
        // 找出数量最少的棋子坐标
        List<IndexDo> toNullIndexList = symbolIndexMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null)
                .min(Map.Entry.comparingByValue(Comparator.comparing(List::size)))
                .map(Map.Entry::getValue)
                .orElse(null);

        return GameBizEventDo.builder()
                .sort(sort)
                .bizEventType(BizEventEnum.EVENT_BOM)
                .changeToSymbol(null)
                .indexDoList(toNullIndexList)
                .build();


    }

    /**
     * 随机将棋盘中的1-3个空位变成wild
     *
     * @param nextSymbolArr nextSymbolArr
     * @param sort          排序
     * @return GameBizEventDo
     */
    private static GameBizEventDo handleEventWild(SymbolDo[][] nextSymbolArr, int sort) {
        List<IndexDo> allNullIndexList = getAllNullIndex(nextSymbolArr);
        List<IndexDo> toWildIndexList = Lists.newArrayList();
        // 随机一到3个空位
        if (CollectionUtil.isNotEmpty(allNullIndexList)) {
            if (allNullIndexList.size() == 1) {
                toWildIndexList.add(allNullIndexList.get(0));
            } else {
                // 随机1到3个空位
                int toWildCount = SecureRandomUtil.randomIntInRange(1, 3);
                toWildIndexList = getRandomIndexList(allNullIndexList, toWildCount);
            }
        } else {
            // 没有空位，则整个盘面随机一到3个元素
            log.info("handleEventWild no empty index,random the no empty index List.symbolArr:{}", JSON.toJSONString(nextSymbolArr));
            int toWildCount = SecureRandomUtil.randomIntInRange(1, 3);
            toWildIndexList = getRandomIndexList(getAllIndex(nextSymbolArr), toWildCount);
        }
        return GameBizEventDo.builder()
                .sort(sort)
                .bizEventType(BizEventEnum.EVENT_WILD)
                .changeToSymbol(AtlantisGameHelper.ATLANTIS_Wild)
                .indexDoList(toWildIndexList)
                .build();
    }

    /**
     * 处理scatter 业务事件
     * action:随机将棋盘中的一个空位变成Scatter
     *
     * @param nextSymbolArr nextSymbolArr
     * @param sort          排序
     * @return GameBizEventDo
     */
    private static GameBizEventDo handleEventScatter(SymbolDo[][] nextSymbolArr, int sort) {
        // 获取所有空位
        List<IndexDo> nullIndexList = getAllNullIndex(nextSymbolArr);
        IndexDo indexDo;
        // 随机一个空位
        if (CollectionUtil.isNotEmpty(nullIndexList)) {
            if (nullIndexList.size() == 1) {
                indexDo = nullIndexList.get(0);
            } else {
                indexDo = nullIndexList.get(SecureRandomUtil.randomIntInRange(0, nullIndexList.size() - 1));
            }
        } else {
            // 没有空位，则整个盘面随机一个元素
            log.info("handleEventScatter no empty index,random the no empty index.symbolArr:{}", JSON.toJSONString(nextSymbolArr));
            indexDo = getRandomIndexList(getAllIndex(nextSymbolArr), 1).get(0);
        }
        return GameBizEventDo.builder()
                .sort(sort)
                .bizEventType(BizEventEnum.EVENT_SCATTER)
                .changeToSymbol(AtlantisGameHelper.ATLANTIS_SCATTER)
                .indexDoList(Lists.newArrayList(indexDo))
                .build();

    }

    /**
     * 从源列表中随机获取指定数量的索引
     *
     * @param sourceList 源列表
     * @param count      数量
     * @return 索引列表
     */
    private static List<IndexDo> getRandomIndexList(List<IndexDo> sourceList, int count) {
        if (sourceList.size() <= count) {
            return Lists.newArrayList(sourceList);
        }

        Set<IndexDo> resultSet = new HashSet<>();
        while (resultSet.size() < count) {
            int randomIndex = SecureRandomUtil.randomIntInRange(0, sourceList.size() - 1);
            IndexDo currentIndex = sourceList.get(randomIndex);
            resultSet.add(currentIndex);
        }
        return Lists.newArrayList(resultSet);
    }

    private static List<IndexDo> getAllIndex(SymbolDo[][] nextSymbolArr) {
        List<IndexDo> indexList = Lists.newArrayList();
        for (int row = 0; row < nextSymbolArr.length; row++) {
            if (nextSymbolArr[row] == null) {
                continue;
            }

            for (int colum = 0; colum < nextSymbolArr[row].length; colum++) {
                indexList.add(IndexFactory.buildDo(row, colum));
            }
        }
        return indexList;
    }

    private static List<IndexDo> getAllNullIndex(SymbolDo[][] nextSymbolArr) {
        List<IndexDo> nullIndexList = Lists.newArrayList();
        for (int row = 0; row < nextSymbolArr.length; row++) {
            if (nextSymbolArr[row] == null) {
                continue;
            }

            for (int colum = 0; colum < nextSymbolArr[row].length; colum++) {
                if (nextSymbolArr[row][colum] == null) {
                    nullIndexList.add(IndexFactory.buildDo(row, colum));
                }
            }
        }
        return nullIndexList;
    }

}
