package com.wegame.capi.biz.module.atlantis.game;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.AtlantisConfig;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.AtlantisGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.bizevent.GameBizEventDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.*;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.atlantis.AtlantisBizEventHandler;
import com.wegame.capi.biz.module.atlantis.AtlantisGameHelper;
import com.wegame.capi.biz.module.atlantis.AtlantisSymbolUtil;
import com.wegame.capi.biz.module.atlantis.reward.AtlantisRewardHandler;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.BizEventEnum;
import com.wegame.capi.common.enums.RewardTypeEnum;
import com.wegame.capi.common.utils.GameUtil;
import com.wegame.capi.common.utils.TtlUtil;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
@Component("atlantisGameNormalPlay")
public class AtlantisGameNormalPlay extends AbstractGame<AtlantisGameRecordDo> {

    private static final Integer WHEEL_DEFAULT_INDEX = 0;
    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final Integer BUY_BET_INIT_SCATTER_COUNT = 4;
    private static final GameInfoDo GAME_INFO = AtlantisGameHelper.ATLANTIS_GAME_INFO;
    private static final List<BizEventEnum> SUPPORT_BIZ_EVENT_LIST = Lists.newArrayList(
            BizEventEnum.EVENT_SCATTER
            , BizEventEnum.EVENT_WILD
            , BizEventEnum.EVENT_BOM
            , BizEventEnum.EVENT_TRIDENT
            , BizEventEnum.EVENT_GOLDEN_TANK);

    @Autowired
    AtlantisConfig atlantisConfig;
    @Autowired
    AtlantisRewardHandler atlantisRewardHandler;
    @Autowired
    private AtlantisBizEventHandler atlantisBizEventHandler;

    @Override
    public List<AtlantisGameRecordDo> play(BetDo bet, GamePlayDo gamePlay) {

        List<AtlantisGameRecordDo> ret = new ArrayList<>();
        AtomicLong totalRewardAmount = new AtomicLong(0L);
        // 首轮游戏处理,返回是否产生免费轮结果
        AtlantisGameRecordDo lastRoundGameRecord = this.oneWheelPlay(gamePlay, RECORD_DEFAULT_PARENT_ID, totalRewardAmount, new AtomicInteger(0), ret, BigDecimal.ZERO, false);
        // 如果有免费轮，加入免费轮处理
        if (lastRoundGameRecord.getRewardResult().isHasRewardFreeWheel()
                && lastRoundGameRecord.getRewardResult().getRewardFreeWheelTimes() > 0) {
            // 处理免费轮-迭代替代递归
            this.freeWheelHandler(totalRewardAmount, ret, gamePlay, lastRoundGameRecord);
        }
        if (CollectionUtils.isNotEmpty(ret)) {
            ret.get(ret.size() - 1).setLastGameFlag(true);
        }
        return ret;
    }

    /**
     * 首轮处理
     *
     * @param gamePlay          gamePlay
     * @param totalRewardAmount 累计中奖金额
     * @param ret               存储结果
     * @return 最后一回合的gameRecord
     */
    private AtlantisGameRecordDo oneWheelPlay(GamePlayDo gamePlay, Long parentId, AtomicLong totalRewardAmount, AtomicInteger totalRewardCount, List<AtlantisGameRecordDo> ret, BigDecimal currentMultiple, boolean isFreeFlag) {
        // 1.生成滚轮
        WheelDo<SymbolDo> wheelDo = this.generateCommonWheel(isFreeFlag);
        // 2.中奖处理
        RewardResultDo<SymbolDo> rewardResultDo = this.rewardHandler(gamePlay, wheelDo, currentMultiple, totalRewardAmount.get(), totalRewardCount);
        if (!this.canContinue(gamePlay, rewardResultDo, totalRewardAmount)) {
            // 购买免费轮首轮处理
            rewardResultDo = this.reRewardHandlerIfNeed(gamePlay, rewardResultDo, wheelDo, null, isFreeFlag, currentMultiple, totalRewardAmount.get(), totalRewardCount);
        }
        // 3.组装单轮游戏记录
        long gameRecordId = generateGameRecordId();
        AtlantisGameRecordDo gameRecordDo = this.buildGameRecord(gameRecordId, parentId, gamePlay.getBetRecordId(), gameRecordId, wheelDo, rewardResultDo);
        ret.add(gameRecordDo);
        // 4.总的中奖金额添加
        totalRewardAmount.addAndGet(rewardResultDo.getTotalRewardAmount());
        // 5.回合处理
        // 如果有中奖，处理回合
        if (this.canContinuePlayOnPreWheel(gamePlay, gameRecordDo, totalRewardAmount)) {
            return this.roundHandler(gamePlay, gameRecordDo, ret, totalRewardAmount, totalRewardCount, isFreeFlag);
        } else {
            // 未中奖
            return gameRecordDo;
        }
    }

    private WheelDo<SymbolDo> generateWheel(boolean isFreeFlag, GamePlayDo gamePlay) {
        WheelDo<SymbolDo> commonWheelDo = this.generateCommonWheel(isFreeFlag);
        if (isFreeFlag || !Objects.equals(gamePlay.getBetType(), BetTypeEnum.BUY_BET)) {
            return commonWheelDo;
        } else {
            // 非免费轮的购买轮,默认生成4个
            List<IndexDo> scatterIndexList = Lists.newArrayList();
            List<IndexDo> noScatterIndexList = Lists.newArrayList();
            SymbolDo[][] symbolArr = commonWheelDo.getSymbolArr();
            for (int row = 0; row < symbolArr.length; row++) {
                for (int col = 0; col < symbolArr[row].length; col++) {
                    SymbolDo symbolDo = symbolArr[row][col];
                    if (symbolDo.isScatterSymbol()) {
                        scatterIndexList.add(IndexFactory.buildDo(row, col));
                    } else {
                        noScatterIndexList.add(IndexFactory.buildDo(row, col));
                    }
                }
            }

            if (scatterIndexList.size() >= BUY_BET_INIT_SCATTER_COUNT) {
                return commonWheelDo;
            } else {
                Set<Integer> newScatterIndexSet = randomService.randomUniqueIntList(0, noScatterIndexList.size() - 1, BUY_BET_INIT_SCATTER_COUNT - scatterIndexList.size());
                for (Integer index : newScatterIndexSet) {
                    IndexDo indexDo = noScatterIndexList.get(index);
                    symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = AtlantisGameHelper.ATLANTIS_SCATTER;
                }
            }
        }
        return commonWheelDo;
    }

    /**
     * 构建单列概率
     *
     * @param cellNo                 列号
     * @param probabilisticConfigMap 概率配置
     * @return map
     */
    public TreeMap<AtlantisConfig.ProbabilisticConfig.SymbolKeyConfig, Integer> buildAtlantisColumProbabilisticMap(int cellNo, TreeMap<AtlantisConfig.ProbabilisticConfig.SymbolKeyConfig, List<BigDecimal>> probabilisticConfigMap) {
        AssertUtil.assertTrue(MapUtils.isNotEmpty(probabilisticConfigMap), ErrorCode.ERR_INTER);
        TreeMap<AtlantisConfig.ProbabilisticConfig.SymbolKeyConfig, Integer> map =
                new TreeMap<>(Comparator.comparing(AtlantisConfig.ProbabilisticConfig.SymbolKeyConfig::getSymbolId).thenComparing(AtlantisConfig.ProbabilisticConfig.SymbolKeyConfig::getCount));
        probabilisticConfigMap.forEach((key, value) -> {
            int val = (value.get(cellNo).multiply(new BigDecimal(Constants.CONST_10000))).intValue();
            if (val > 0) {
                map.put(key, val);
            }
        });
        Integer sum = map.values()
                .stream()
                .reduce(0, Integer::sum);
        AssertUtil.assertTrue(sum == Constants.CONST_1000000, ErrorCode.ERR_INTER, "Symbol config error,cellNo:" + cellNo);
        return map;
    }


    /**
     * 回合处理
     * 迭代替代递归
     *
     * @param firstRoundGameRecordDo 一轮的第一回合record
     * @param ret                    统计总的游戏记录
     * @param totalRewardAmount      总的中奖金额统计
     * @return 产生的免费轮元数据 最后一回合的gameRecord
     */
    private AtlantisGameRecordDo roundHandler(GamePlayDo gamePlay, AtlantisGameRecordDo firstRoundGameRecordDo, List<AtlantisGameRecordDo> ret, AtomicLong totalRewardAmount, AtomicInteger totalRewardCount, boolean isFreeWheel) {
        LinkedList<AtlantisGameRecordDo> roundHandlerQueue = Lists.newLinkedList();
        roundHandlerQueue.addLast(firstRoundGameRecordDo);

        while (!roundHandlerQueue.isEmpty()) {
            // 取出任务
            AtlantisGameRecordDo preAtlantisGameRecordDo = roundHandlerQueue.pollFirst();
            // preWheel
            WheelDo<SymbolDo> preWheel = preAtlantisGameRecordDo.getWheel();
            // copy 
            SymbolDo[][] nextSymbolArr = AtlantisSymbolUtil.copySymbolArr(preWheel.getSymbolArr());
            // 重新构建nextSymbolArr
            ReBuildSymbolArrResultDo preReBuildSymbolArrResultDo = this.rebuildSymbolArr(gamePlay, nextSymbolArr, preAtlantisGameRecordDo, totalRewardCount);
            // 构建新滚轮
            WheelDo<SymbolDo> nextWheel = WheelDo.<SymbolDo>builder()
                    .index(preWheel.getIndex() + 1)
                    .freeFlag(preWheel.isFreeFlag())
                    .symbolArr(nextSymbolArr)
                    .build();
            // 中奖处理
            RewardResultDo<SymbolDo> nextRewardResultDo = this.rewardHandler(gamePlay, nextWheel, preAtlantisGameRecordDo.getRewardResult().getTotalMultiple(), totalRewardAmount.get(), totalRewardCount);
            if (!this.canContinue(gamePlay, nextRewardResultDo, totalRewardAmount)) {
                // 购买免费轮首轮处理
                nextRewardResultDo = this.reRewardHandlerIfNeed(gamePlay, nextRewardResultDo, nextWheel, preReBuildSymbolArrResultDo, isFreeWheel, preAtlantisGameRecordDo.getRewardResult().getTotalMultiple(), totalRewardAmount.get(), totalRewardCount);
            }

            // 构建gameRecord
            AtlantisGameRecordDo nextGameRecordDo = this.buildGameRecord(generateGameRecordId(), preAtlantisGameRecordDo.getId(), gamePlay.getBetRecordId(), preAtlantisGameRecordDo.getBatchId(), nextWheel, nextRewardResultDo);
            // 添加到gameRecord列表
            ret.add(nextGameRecordDo);
            // 总金额添加
            totalRewardAmount.addAndGet(nextRewardResultDo.getTotalRewardAmount());
            // 中奖->加入队列
            // 如果有中奖，处理回合
            if (this.canContinuePlayOnPreWheel(gamePlay, nextGameRecordDo, totalRewardAmount)) {
                roundHandlerQueue.addLast(nextGameRecordDo);
            } else {
                // 购买免费轮首轮兜底处理
                return nextGameRecordDo;
            }
        }
        return null;
    }


    /**
     * 购买免费轮首轮兜底处理
     * <p> 如果是购买免费轮首轮，保证一定返回指定数量的scatter.需要满足以下规则</p>
     * <ul>1.只针对购买免费轮的首轮进行处理</ul>
     * <ul>2.scatter 所能覆盖的位置一定只能是最后一次下落的剩余空位</ul>
     * <ul>3.最后一回合肯定是不中奖的，调用方保证</ul>
     *
     * @param gamePlay       gamePlay
     * @param rewardResultDo nextRewardResultDo
     * @param isFreeWheel    是否是免费轮
     * @return 兜底gameRecord
     */
    private RewardResultDo<SymbolDo> reRewardHandlerIfNeed(GamePlayDo gamePlay, RewardResultDo<SymbolDo> rewardResultDo, WheelDo<SymbolDo> nextWheel,
                                                           ReBuildSymbolArrResultDo preReBuildSymbolArrResultDo, boolean isFreeWheel, BigDecimal totalMultiple,
                                                           long totalRewardAmount, AtomicInteger totalRewardCount) {
        if (isFreeWheel || !Objects.equals(gamePlay.getBetType(), BetTypeEnum.BUY_BET)) {
            return rewardResultDo;
        } else {
            // 统计处当前滚轮有多少scatter
            List<IndexDo> scatterIndexList = Lists.newArrayList();
            SymbolDo[][] symbolArr = nextWheel.getSymbolArr();
            for (int row = 0; row < symbolArr.length; row++) {
                for (int col = 0; col < symbolArr[row].length; col++) {
                    SymbolDo symbolDo = symbolArr[row][col];
                    if (symbolDo.isScatterSymbol()) {
                        scatterIndexList.add(new IndexDo(row, col));
                    }
                }
            }

            // 如果没有达到期望的个数，则补充差的个数
            Integer exceptedScatterCount = this.getExceptedScatterCount(gamePlay);
            if (exceptedScatterCount <= 0) {
                log.info("no add scatter...");
                return rewardResultDo;
            }

            if (scatterIndexList.size() < exceptedScatterCount) {
                int addScatterCount = exceptedScatterCount - scatterIndexList.size();
                // 最后一次下落后的空位中随机挑出对应个数补充
                List<IndexDo> injectEmptyIndexList = this.getCanInjectEmptyIndexList(preReBuildSymbolArrResultDo, symbolArr);
                injectEmptyIndexList.removeAll(scatterIndexList);
                // 这里直接报错
                AssertUtil.assertTrue(addScatterCount <= injectEmptyIndexList.size(), ErrorCode.ERR_INTER, "Scatter count error");
                // 从injectEmptyIndexList 随机addScatterCount个数，替换成 scatter
                if (injectEmptyIndexList.size() == 1) {
                    IndexDo indexDo = injectEmptyIndexList.get(0);
                    symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = AtlantisGameHelper.ATLANTIS_SCATTER;
                }

                Set<Integer> injectScatterIndexSet = randomService.randomUniqueIntList(0, injectEmptyIndexList.size() - 1, addScatterCount);
                for (Integer index : injectScatterIndexSet) {
                    IndexDo indexDo = injectEmptyIndexList.get(index);
                    symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = AtlantisGameHelper.ATLANTIS_SCATTER;
                }
                return this.rewardHandler(gamePlay, nextWheel, totalMultiple, totalRewardAmount, totalRewardCount);
            }
        }
        return rewardResultDo;

    }

    private List<IndexDo> getCanInjectEmptyIndexList(ReBuildSymbolArrResultDo preReBuildSymbolArrResultDo, SymbolDo[][] symbolArr) {
        if (Objects.isNull(preReBuildSymbolArrResultDo)) {
            List<IndexDo> canInjectEmptyIndexList = Lists.newArrayList();
            // 首轮首回合-在当前盘面中筛选
            for (int row = 0; row < symbolArr.length; row++) {
                for (int col = 0; col < symbolArr[row].length; col++) {
                    canInjectEmptyIndexList.add(IndexFactory.buildDo(row, col));
                }
            }
            return canInjectEmptyIndexList;
        } else {
            // 非首轮首回合-在上一轮中筛选
            return preReBuildSymbolArrResultDo.getInjectEmptyIndexList();

        }


    }

    private Integer getExceptedScatterCount(GamePlayDo gamePlay) {
        // todo
        return atlantisConfig.getBuyBetExceptedScatterCount();
    }

    /**
     * 重新构建符号数组
     * nextSymbolArr 变动聚合这个方法内进行
     *
     * @param nextSymbolArr           nextSymbolArr
     * @param preAtlantisGameRecordDo preAtlantisGameRecordDo
     */
    private ReBuildSymbolArrResultDo rebuildSymbolArr(GamePlayDo gamePlay, SymbolDo[][] nextSymbolArr, AtlantisGameRecordDo preAtlantisGameRecordDo, AtomicInteger totalRewardCount) {
        // 中奖消除
        this.rewardClearUp(nextSymbolArr, preAtlantisGameRecordDo.getRewardResult());
        // 中奖下落
        this.symbolFall(nextSymbolArr);
        // 事件处理:事件影响+事件记录到preAtlantisGameRecordDo
        this.bizEventHandler(nextSymbolArr, preAtlantisGameRecordDo);
        // 新滚轮填充空位
        List<IndexDo> injectEmptyIndexList = this.injectEmptyIndex(nextSymbolArr);
        return ReBuildSymbolArrResultDo.builder().injectEmptyIndexList(injectEmptyIndexList).build();
    }

    /**
     * 填充空位
     * 根据概率表进行空位的填充
     *
     * @param nextSymbolArr nextSymbolArr
     */
    private List<IndexDo> injectEmptyIndex(SymbolDo[][] nextSymbolArr) {
        List<IndexDo> nullIndexList = Lists.newArrayList();
        // 根据配置获取
        TreeMap<AtlantisConfig.ProbabilisticConfig.SymbolKeyConfig, List<BigDecimal>> wheelProbabilisticConfig = atlantisConfig.getProbabilisticConfig().getWheelProbabilisticConfig();
        // 按列填充盘面
        for (int columnNum = 0; columnNum < GAME_INFO.getColumn(); columnNum++) {
            // 1.统计-当前列null元素坐标
            List<IndexDo> columNullIndexList = Lists.newArrayList();
            // 元素填充
            for (int rowNum = 0; rowNum < GAME_INFO.getRow(); rowNum++) {
                // 其中一个不为空（因为已经下落国）,则跳出
                if (Objects.nonNull(nextSymbolArr[rowNum][columnNum])) {
                    break;
                } else {
                    columNullIndexList.add(IndexFactory.buildDo(rowNum, columnNum));
                }
            }

            // 2.生成元素并填充
            if (CollectionUtil.isNotEmpty(columNullIndexList)) {
                // 构建可随机的概率map
                TreeMap<AtlantisConfig.ProbabilisticConfig.SymbolKeyConfig, Integer> columProbabilisticMap = this.buildAtlantisColumProbabilisticMap(columnNum, wheelProbabilisticConfig);
                // 整列元素随机
                List<SymbolDo> symbolDoList = this.randomSymbolList(columProbabilisticMap, columNullIndexList.size());
                AssertUtil.assertTrue(symbolDoList.size() == columNullIndexList.size(), ErrorCode.ERR_INTER);
                // 填充
                for (int i = 0; i < columNullIndexList.size(); i++) {
                    IndexDo indexDo = columNullIndexList.get(i);
                    nextSymbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = symbolDoList.get(i);
                }
                nullIndexList.addAll(columNullIndexList);
            }
        }
        return nullIndexList;
    }

    /**
     * 事件处理:事件影响+事件记录到preAtlantisGameRecordDo
     *
     * @param nextSymbolArr           nextSymbolArr
     * @param preAtlantisGameRecordDo 前一个gameRecordDo
     */
    private void bizEventHandler(SymbolDo[][] nextSymbolArr, AtlantisGameRecordDo preAtlantisGameRecordDo) {
        // 获取上一轮中奖产生的事件
        List<BizEventEnum> rewardBizEventTypeList = preAtlantisGameRecordDo.getRewardResult().getBizEventList();
        List<GameBizEventDo> rewardEffectBizEventList = preAtlantisGameRecordDo.getRewardResult().getEffectBizEventList();
        Map<Integer, GameBizEventDo> rewardBizEventEffectIndexMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(rewardEffectBizEventList)) {
            rewardBizEventEffectIndexMap = rewardEffectBizEventList.stream().collect(Collectors.toMap(GameBizEventDo::getSort, e -> e));
        }
        if (CollectionUtils.isEmpty(rewardBizEventTypeList)) {
            return;
        }

        List<GameBizEventDo> gameBizEventDoList = Lists.newArrayList();
        // 支持的事件单个处理
        for (int i = 0; i < rewardBizEventTypeList.size(); i++) {
            BizEventEnum bizEvent = rewardBizEventTypeList.get(i);
            // game和reward 聚合
            GameBizEventDo rewardEventDo = rewardBizEventEffectIndexMap.get(i);
            if (Objects.nonNull(rewardEventDo)) {
                gameBizEventDoList.add(rewardEventDo);
                continue;
            }

            if (!SUPPORT_BIZ_EVENT_LIST.contains(bizEvent)) {
                continue;
            }
            // 生成事件处理结果
            GameBizEventDo gameBizEventDo = atlantisBizEventHandler.handleBizEvent(bizEvent, nextSymbolArr, i);
            // 处理影响-元素替换
            if (Objects.nonNull(gameBizEventDo) && gameBizEventDo.hasSymbolChange()) {
                SymbolDo changeToSymbol = gameBizEventDo.getChangeToSymbol();
                gameBizEventDo.getIndexDoList().forEach(indexDo -> nextSymbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = changeToSymbol);
            }
            // 事件下落-单个事件执行完成下落再执行下一个事件
            this.symbolFall(nextSymbolArr);
            // 添加事件处理到上一个gameRecordDo
            gameBizEventDoList.add(gameBizEventDo);
        }
        gameBizEventDoList.sort(Comparator.comparingInt(GameBizEventDo::getSort));
        preAtlantisGameRecordDo.setBizEventList(gameBizEventDoList);
    }

    /**
     * 元素在当前列下落
     *
     * @param nextSymbolArr nextSymbolArr
     */
    private void symbolFall(SymbolDo[][] nextSymbolArr) {
        for (int column = ONE_COLUM_INDEX; column < GAME_INFO.getColumn(); column++) {
            int currentRow = GAME_INFO.getRow() - 1;
            for (int row = GAME_INFO.getRow() - 1; row >= ONE_ROW_INDEX; row--) {
                if (Objects.nonNull(nextSymbolArr[row][column])) {
                    if (!Objects.equals(currentRow, row)) {
                        // 移动
                        SymbolDo temp = nextSymbolArr[row][column];
                        nextSymbolArr[currentRow][column] = temp;
                        nextSymbolArr[row][column] = null;
                    }
                    currentRow--;
                }
            }
        }
    }

    /**
     * 中奖元素消除
     *
     * @param nextSymbolArr   nextSymbolArr
     * @param preRewardResult preRewardResult
     */
    private void rewardClearUp(SymbolDo[][] nextSymbolArr, RewardResultDo<SymbolDo> preRewardResult) {
        List<IndexDo> preRewardIndexDoList = preRewardResult.getRewardLineDetailList().stream()
                .map(RewardLineDetailDo::getRewardSymbolList).flatMap(List::stream)
                .map(RewardSymbolDo::getIndex).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(preRewardIndexDoList)) {
            preRewardIndexDoList.forEach(indexDo -> nextSymbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = null);
        }
    }


    private boolean canContinuePlayOnPreWheel(GamePlayDo gamePlay, AtlantisGameRecordDo gameRecordDo, AtomicLong totalRewardAmount) {
        if (Objects.isNull(gameRecordDo)) {
            return false;
        }
        RewardResultDo<SymbolDo> rewardResult = gameRecordDo.getRewardResult();
        return this.canContinue(gamePlay, rewardResult, totalRewardAmount);
    }

    private boolean canContinue(GamePlayDo gamePlay, RewardResultDo<SymbolDo> rewardResult, AtomicLong totalRewardAmount) {
        if (Objects.isNull(rewardResult)) {
            return false;
        }
        if (Objects.equals(rewardResult.getRewardType(), RewardTypeEnum.NONE)) {
            return false;
        }
        if (rewardResult.getAccumulateReachMaxMultiple()) {
            log.warn("reach max reward multiple bet. totalRewardAmount: {}, payAmount: {}", totalRewardAmount.get(), gamePlay.getPayAmount());
            return false;
        }
        return CollectionUtils.isNotEmpty(rewardResult.getRewardLineDetailList());
    }


    private void freeWheelHandler(AtomicLong totalRewardAmount, List<AtlantisGameRecordDo> ret, GamePlayDo gamePlay, AtlantisGameRecordDo preGameRecord) {
        // 如果达到最大倍数，则结束-在gameRecord中添加判断方法
        if (Objects.equals(preGameRecord.getRewardResult().getAccumulateReachMaxMultiple(), Boolean.TRUE)) {
            log.info("atlantis freeWheelHandler AccumulateReachMaxMultiple,betRecordId:{}", gamePlay.getBetRecordId());
            return;
        }
        // 免费轮独立计算
        AtomicInteger totalRewardCount = new AtomicInteger(0);
        LinkedList<FreeWheelHandleDo> freeWheelQueue = Lists.newLinkedList();
        freeWheelQueue.addLast(FreeWheelHandleDo.builder().parentGameRecord(preGameRecord).taskTimes(preGameRecord.getRewardResult().getRewardFreeWheelTimes()).build());
        // 初始队列
        while (!freeWheelQueue.isEmpty()) {
            // 取出一个任务
            FreeWheelHandleDo preFreeWheelHandleDo = freeWheelQueue.pollFirst();
            AtlantisGameRecordDo parentGameRecord = preFreeWheelHandleDo.getParentGameRecord();
            long parentGameRecordId = parentGameRecord.getId();
            for (int i = Constants.CONST_0; i < preFreeWheelHandleDo.getTaskTimes(); i++) {
                // 免费轮处理
                AtlantisGameRecordDo lastRoundGameRecord = this.oneWheelPlay(gamePlay, parentGameRecordId, totalRewardAmount, totalRewardCount, ret, parentGameRecord.getRewardResult().getTotalMultiple(), true);
                // parentGameRecord 添加子gameRecordId-同一轮中的batchId
                parentGameRecord.addGameRecordId(lastRoundGameRecord.getBatchId());
                RewardResultDo<SymbolDo> freeRewardResult = lastRoundGameRecord.getRewardResult();
                // 如果达到最大倍数，则结束
                if (Objects.equals(freeRewardResult.getAccumulateReachMaxMultiple(), Boolean.TRUE)) {
                    log.info("atlantis freeWheelHandler AccumulateReachMaxMultiple,betRecordId:{}", gamePlay.getBetRecordId());
                    return;
                }
                // 如果有中免费轮，则加入队列
                if (freeRewardResult.isHasRewardFreeWheel() && freeRewardResult.getRewardFreeWheelTimes() > 0) {
                    freeWheelQueue.addLast(FreeWheelHandleDo.builder().parentGameRecord(lastRoundGameRecord).taskTimes(freeRewardResult.getRewardFreeWheelTimes()).build());
                }
            }
        }
    }

    private RewardResultDo<SymbolDo> rewardHandler(GamePlayDo gamePlay, WheelDo<SymbolDo> wheel, BigDecimal currentMultiple, Long currentTotalRewardAmount, AtomicInteger totalRewardCount) {
        // 中奖处理
        RewardHandleDo<SymbolDo> rewardHandle = RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, currentMultiple, currentTotalRewardAmount, totalRewardCount.get(), null);
        RewardResultDo<SymbolDo> rewardResult = atlantisRewardHandler.handle(rewardHandle);
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        if (CollectionUtil.isNotEmpty(rewardResult.getRewardLineDetailList())) {
            totalRewardCount.addAndGet(rewardResult.getRewardLineDetailList().size());
        }
        return rewardResult;
    }

    /**
     * 通用滚轮生成
     *
     * @return 滚轮
     */
    private WheelDo<SymbolDo> generateCommonWheel(boolean freeFlag) {
        // 根据配置获取
        TreeMap<AtlantisConfig.ProbabilisticConfig.SymbolKeyConfig, List<BigDecimal>> wheelProbabilisticConfig = atlantisConfig.getProbabilisticConfig().getWheelProbabilisticConfig();
        SymbolDo[][] symbolArr = new SymbolDo[GAME_INFO.getRow()][GAME_INFO.getColumn()];
        // 按列生成盘面
        for (int columnNum = 0; columnNum < GAME_INFO.getColumn(); columnNum++) {
            // 构建可随机的概率map
            TreeMap<AtlantisConfig.ProbabilisticConfig.SymbolKeyConfig, Integer> columProbabilisticMap = this.buildAtlantisColumProbabilisticMap(columnNum, wheelProbabilisticConfig);
            // 整列元素随机
            List<SymbolDo> symbolDoList = this.randomSymbolList(columProbabilisticMap, GAME_INFO.getRow());
            AssertUtil.assertTrue(symbolDoList.size() == GAME_INFO.getRow(), ErrorCode.ERR_INTER);

            // 元素填充
            for (int rowNum = 0; rowNum < GAME_INFO.getRow(); rowNum++) {
                symbolArr[rowNum][columnNum] = symbolDoList.get(rowNum);
            }
        }
        // 返回：滚轮
        return WheelDo.<SymbolDo>builder()
                .index(WHEEL_DEFAULT_INDEX)
                .freeFlag(freeFlag)
                .symbolArr(symbolArr)
                .build();
    }

    /**
     * 随机元素
     *
     * @param columProbabilisticMap 参与随机的元素概率map
     * @param symbolCount           期望生成的元素个数
     * @return
     */
    public List<SymbolDo> randomSymbolList(TreeMap<AtlantisConfig.ProbabilisticConfig.SymbolKeyConfig, Integer> columProbabilisticMap, int symbolCount) {
        List<SymbolDo> symbolDoList = Lists.newArrayList();

        while (symbolDoList.size() < symbolCount) {
            int randomInt = randomService.randomInt(Constants.CONST_1, Constants.CONST_1000000);
            AtlantisConfig.ProbabilisticConfig.SymbolKeyConfig symbolKeyConfig = GameUtil.randomKey(columProbabilisticMap, randomInt);
            // 生成单个元素列表
            for (int i = 0; i < symbolKeyConfig.getCount(); i++) {
                SymbolDo symbolDo = GAME_INFO.getSymbolById(symbolKeyConfig.getSymbolId());
                symbolDoList.add(symbolDo);
                // 如果满足个数了，则返回
                if (symbolDoList.size() >= symbolCount) {
                    return symbolDoList;
                }
            }
        }
        return symbolDoList;
    }

    private AtlantisGameRecordDo buildGameRecord(Long id,
                                                 Long parentId,
                                                 Long betRecordId,
                                                 Long batchId,
                                                 WheelDo<SymbolDo> wheel,
                                                 RewardResultDo<SymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return AtlantisGameRecordDo.builder()
                .id(id)
                .parentId(parentId)
                .betRecordId(betRecordId)
                .batchId(batchId)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .index(wheel.getIndex())
                .freeWheelFlag(wheel.isFreeFlag())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .gameRecordIds(null)
                .lastGameFlag(false)
                .bizEventList(rewardResult.getEffectBizEventList())
                .creator(userInfo.getPlayerId())
                .createTime(DateUtil.getCurrentMilli())
                .updater(userInfo.getPlayerId())
                .updateTime(DateUtil.getCurrentMilli())
                .expireAt(TtlUtil.calculateExpiredAt(DateUtil.getCurrentMilli()))
                .build();
    }

    @Data
    @SuperBuilder
    private static class FreeWheelHandleDo {
        // parentGameRecord
        private AtlantisGameRecordDo parentGameRecord;
        // 任务执行次数
        private int taskTimes;
    }

    @Data
    @SuperBuilder
    private static class ReBuildSymbolArrResultDo {
        private List<IndexDo> injectEmptyIndexList;
    }
}