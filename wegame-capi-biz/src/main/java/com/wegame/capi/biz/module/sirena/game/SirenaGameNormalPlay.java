package com.wegame.capi.biz.module.sirena.game;

import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.SirenaConfig;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.SirenaGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.*;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.manager.SirenaBetRecordManager;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.sirena.SirenaGameHelper;
import com.wegame.capi.biz.module.sirena.reward.SirenaRewardHandler;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.utils.GameUtil;
import com.wegame.capi.dao.repository.domain.business.GameRecordQuery;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Component("sirenaGameNormalPlay")
public class SirenaGameNormalPlay extends AbstractGame<SirenaGameRecordDo> {

    private static final Integer WHEEL_DEFAULT_INDEX = 0;
    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;

    private static final GameInfoDo GAME_INFO = SirenaGameHelper.SIRENA_GAME_INFO;
    private static final Integer MAX_WHEEL = 12;

    @Autowired
    SirenaConfig sirenaConfig;
    @Autowired
    SirenaRewardHandler sirenaRewardHandler;
    @Autowired
    private SirenaBetRecordManager sirenaBetRecordManager;

    @Override
    public List<SirenaGameRecordDo> play(GamePlayDo gamePlay) {
        List<SirenaGameRecordDo> ret = new ArrayList<>();
        AtomicLong totalRewardAmount = new AtomicLong(0L);
        // 首轮游戏处理
        OneWheelPlayWrapDo firstWheelPlayWrapDo = this.oneWheelPlay(gamePlay, RECORD_DEFAULT_PARENT_ID, totalRewardAmount, ret, null, null, false,true);
        // 免费轮处理-迭代替代递归
        // 是否需要处理
        if (firstWheelPlayWrapDo.getRewardResult().isHasRewardFreeWheel()
                && firstWheelPlayWrapDo.getRewardResult().getRewardFreeWheelTimes() > 0) {
            this.freeWheelHandler(firstWheelPlayWrapDo.getGameRecordDo(), totalRewardAmount, ret, gamePlay);
        }
        if (CollectionUtils.isNotEmpty(ret)) {
            ret.get(ret.size() - 1).setLastGameFlag(true);
        }
        return ret;
    }


    private OneWheelPlayWrapDo oneWheelPlay(GamePlayDo gamePlay,
                                            Long parentId,
                                            AtomicLong totalRewardAmount,
                                            List<SirenaGameRecordDo> ret,
                                            BigDecimal currentMultiple,
                                            Map<IndexDo, SirenaSymbolDo> beforePearlWildIndex,
                                            boolean freeFlag, boolean isScatter) {


        // 如果是正常投注,并且有保留的珍珠,需要使用上次的珍珠填充
        Map<IndexDo, SirenaSymbolDo> pearlWildIndexMap = null;
        if (gamePlay.getBetSubType() == BetSubTypeEnum.NORMAL_BET && !freeFlag
                && gamePlay.getContinuePlayContext() != null && gamePlay.getContinuePlayContext().getParentBetRecordId() != null) {
            // 验证
            Long parentBetRecordId = gamePlay.getContinuePlayContext().getParentBetRecordId();
            BetRecordDo oneBetRecordByTimeLast = sirenaBetRecordManager.findOneBetRecordByTimeLast(UserInfoContext.getUserInfo().getPlayerId(), GameTypeEnum.SIRENA);
            if (oneBetRecordByTimeLast == null
                    || parentBetRecordId.longValue() != oneBetRecordByTimeLast.getId().longValue()
                    || !BetSubTypeEnum.NORMAL_BET.getCode().equals(oneBetRecordByTimeLast.getBetSubType())
                    || gamePlay.getBetAmount().longValue() != oneBetRecordByTimeLast.getBetAmount().longValue()) {
                throw new BusinessException(ErrorCode.ERR_INTER);
            }
            // 获取游戏记录
            GameRecordQuery gameRecordQuery = GameRecordQuery.builder().betRecordId(parentBetRecordId).build();
            SirenaGameRecordDo oneGameRecord = sirenaBetRecordManager.findOneGameRecord(gameRecordQuery, GameTypeEnum.SIRENA);
            if (oneGameRecord == null) {
                throw new BusinessException(ErrorCode.ERR_INTER);
            }
            // 获取珍珠的坐标
            pearlWildIndexMap = this.getPearlWildIndexList(oneGameRecord.getWheel());
        }
        // 生成滚轮
        WheelDo<SirenaSymbolDo> wheelDo = this.generateCommonWheel(this.buildWheelChartModel(gamePlay, freeFlag), beforePearlWildIndex, freeFlag,true);

        if (gamePlay.getBetSubType() == BetSubTypeEnum.NORMAL_BET && !freeFlag
                && gamePlay.getContinuePlayContext() != null && gamePlay.getContinuePlayContext().getParentBetRecordId() != null) {
            // 替换珍珠
            SirenaSymbolDo[][] symbolArr = wheelDo.getSymbolArr();
            pearlWildIndexMap.forEach((indexDo, v) -> {
                symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = v;
            });
        }
        // 中奖以及滚轮后处理
        RewardResultDo<SirenaSymbolDo> rewardResultDo = this.rewardAndWheelAfterHandler(gamePlay, wheelDo, currentMultiple, totalRewardAmount.get());
        // 组装单轮游戏记录
        SirenaGameRecordDo gameRecordDo = this.buildGameRecord(generateGameRecordId(), parentId, gamePlay.getBetRecordId(), wheelDo, rewardResultDo);
        ret.add(gameRecordDo);
        // 总的中奖金额添加
        totalRewardAmount.addAndGet(rewardResultDo.getTotalRewardAmount());
        return OneWheelPlayWrapDo.builder().gameRecordDo(gameRecordDo).build();
    }

    private void freeWheelHandler(SirenaGameRecordDo firstGameRecord, AtomicLong totalRewardAmount, List<SirenaGameRecordDo> ret, GamePlayDo gamePlay) {
        // 如果达到最大倍数，则结束
        if (Objects.equals(firstGameRecord.getRewardResult().getAccumulateReachMaxMultiple(), Boolean.TRUE)) {
            log.info("freeWheelHandler AccumulateReachMaxMultiple,betRecordId:{}", gamePlay.getBetRecordId());
            return;
        }

        LinkedList<FreeWheelHandleDo> freeWheelQueue = Lists.newLinkedList();
        freeWheelQueue.addLast(FreeWheelHandleDo.builder().parentGameRecord(firstGameRecord).taskTimes(firstGameRecord.getRewardResult().getRewardFreeWheelTimes()).build());
        Map<IndexDo, SirenaSymbolDo> beforePearlWildIndex = null;
        int scatter1Count = 0;
        boolean scatter = true;
        // 初始队列
        while (!freeWheelQueue.isEmpty()) {
            // 取出一个任务
            FreeWheelHandleDo freeWheelHandleDo = freeWheelQueue.pollFirst();
            SirenaGameRecordDo parentGameRecord = freeWheelHandleDo.getParentGameRecord();
            long parentGameRecordId = parentGameRecord.getId();
            for (int i = Constants.CONST_0; i < freeWheelHandleDo.getTaskTimes(); i++) {
                scatter1Count++;
                if(scatter1Count >= MAX_WHEEL){
                    scatter= false;
                }
                OneWheelPlayWrapDo freeWheelPlayWrapDo = this.oneWheelPlay(gamePlay, parentGameRecordId, totalRewardAmount, ret, BigDecimal.ZERO, beforePearlWildIndex, true,scatter);

                beforePearlWildIndex =  this.getPearlWildIndexList(freeWheelPlayWrapDo.getGameRecordDo().getWheel());
                // parentGameRecord 添加子gameRecordId
                parentGameRecord.addGameRecordId(freeWheelPlayWrapDo.getGameRecordDo().getId());
                RewardResultDo<SirenaSymbolDo> freeRewardResult = freeWheelPlayWrapDo.getRewardResult();
                // 如果达到最大倍数，则结束
                if (Objects.equals(freeRewardResult.getAccumulateReachMaxMultiple(), Boolean.TRUE)) {
                    log.info("freeWheelHandler AccumulateReachMaxMultiple,betRecordId:{}", gamePlay.getBetRecordId());
                    return;
                }
                // 如果有中免费轮，则加入队列
                if (freeRewardResult.isHasRewardFreeWheel() && freeRewardResult.getRewardFreeWheelTimes() > 0) {
                    freeWheelQueue.addLast(FreeWheelHandleDo.builder().parentGameRecord(freeWheelPlayWrapDo.getGameRecordDo()).taskTimes(freeRewardResult.getRewardFreeWheelTimes()).build());
                }
            }
        }
    }

    private RewardResultDo<SirenaSymbolDo> rewardAndWheelAfterHandler(GamePlayDo gamePlay, WheelDo<SirenaSymbolDo> wheel, BigDecimal currentMultiple, Long currentTotalRewardAmount) {
        // 中奖处理
        RewardResultDo<SirenaSymbolDo> rewardResult = sirenaRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, currentMultiple, currentTotalRewardAmount));
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        // 中奖后滚轮后处理
        this.wheelAfterHandler(gamePlay, rewardResult, wheel);
        return rewardResult;
    }

    private Map<IndexDo, SirenaSymbolDo> getPearlWildIndex(WheelDo<SirenaSymbolDo> wheel) {
        SirenaSymbolDo[][] symbolArr = wheel.getSymbolArr();
        Map<IndexDo, SirenaSymbolDo> indexMap = new HashMap<>();
        for (int columNum = 0; columNum < GAME_INFO.getColumn(); columNum++) {
            // 填充
            for (int rowNum = 0; rowNum < GAME_INFO.getRow(); rowNum++) {
                // 填充
                SirenaSymbolDo sirenaSymbolDo = symbolArr[rowNum][columNum];
                if (sirenaSymbolDo.isWildSymbol()) {
                    IndexDo index = IndexDo.builder().rowIndex(rowNum).columnIndex(columNum).build();
                    indexMap.put(index, sirenaSymbolDo.copy());
                }

            }
        }
        return indexMap;
    }

    /**
     * 中奖后如果中奖线有贝壳元素,贝壳变为小珍珠
     *
     * @param handleResultDo handleResultDo
     * @param wheel          wheel
     */
    private void wheelAfterHandler(GamePlayDo gamePlay, RewardResultDo<SirenaSymbolDo> handleResultDo, WheelDo<SirenaSymbolDo> wheel) {
        if (BetSubTypeEnum.NORMAL_BET == gamePlay.getBetSubType()) {
            // 统计出所有symbol中奖线内的贝壳wild坐标
            List<IndexDo> conchSymbolWildIndexList = Optional.ofNullable(handleResultDo.getRewardLineDetailList()).orElse(Collections.emptyList()).stream()
                    // 所有贝壳元素
                    .filter(line -> line.getRewardSymbol().isConchSymbol())
                    // 现金symbol中奖线元素详情
                    .flatMap(line -> line.getRewardSymbolList().stream())
                    // 现金symbol 中奖线元素详情 中的wild元素
                    .filter(rewardSymbol -> rewardSymbol.getSymbol().isWildSymbol()).map(RewardSymbolDo::getIndex)
                    .collect(Collectors.toList());

            SirenaSymbolDo[][] symbolArr = wheel.getSymbolArr();

            for (IndexDo indexDo : conchSymbolWildIndexList) {
                SirenaSymbolDo sirenaSymbolDo = symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()];
                // 生成珍珠Wild
                SymbolDo pearlWild = SirenaGameHelper.SIRENA_Pearl_Wild;
                // 计算小珍珠的倍率
                TreeMap<BigDecimal, BigDecimal> smallPearlMultipleWeightConfig = sirenaConfig.getProbabilisticConfig().getSmallPearlMultipleWeightConfig();
                BigDecimal bigDecimal = GameUtil.randomKey(smallPearlMultipleWeightConfig, Constants.CONST_10000, randomService.randomInt(1, Constants.CONST_1000000));
                SirenaSymbolDo pearlWildDo = SirenaSymbolDo.buildPearlWildSymbol(pearlWild, bigDecimal);
                sirenaSymbolDo.setNestSymbol(pearlWildDo);
            }
        }
    }


    /**
     * 通用滚轮生成
     *
     * @param wheelChartModel 轮代表moudle 二维数组，一维：reel 二维：元素id轮带
     * @return 滚轮
     */
    private WheelDo<SirenaSymbolDo> generateCommonWheel(List<List<String>> wheelChartModel,Map<IndexDo, SirenaSymbolDo> beforePearlWildIndex, boolean freeFlag,Boolean isScatter) {
        SirenaSymbolDo[][] symbolArr = new SirenaSymbolDo[GAME_INFO.getRow()][GAME_INFO.getColumn()];
        if(freeFlag){
            // 免费轮,随机生成1-3个珍珠
            TreeMap<Integer, BigDecimal> freeWheelSmallPearlCountWeightConfig = sirenaConfig.getProbabilisticConfig().getFreeWheelSmallPearlCountWeightConfig();
            Integer pearlCount = GameUtil.randomKey(freeWheelSmallPearlCountWeightConfig, Constants.CONST_10000, randomService.randomInt(1, Constants.CONST_1000000));
            List<IndexDo> randomPositions = generateRandomPositionsExcluding(beforePearlWildIndex, pearlCount);
            // 在随机位置生成珍珠
            for (IndexDo position : randomPositions) {
                SymbolDo pearlWild = SirenaGameHelper.SIRENA_Pearl_Wild;
                // 计算小珍珠的倍率
                TreeMap<BigDecimal, BigDecimal> smallPearlMultipleWeightConfig = sirenaConfig.getProbabilisticConfig().getSmallPearlMultipleWeightConfig();
                BigDecimal bigDecimal = GameUtil.randomKey(smallPearlMultipleWeightConfig, Constants.CONST_10000, randomService.randomInt(1, Constants.CONST_1000000));
                SirenaSymbolDo pearlWildDo = SirenaSymbolDo.buildPearlWildSymbol(pearlWild, bigDecimal);
                symbolArr[position.getRowIndex()][position.getColumnIndex()] = pearlWildDo;
            }
        }
        // 通过当前轮带，按照colum 生成整列元素
        for (int columNum = 0; columNum < GAME_INFO.getColumn(); columNum++) {
            // 随机循环获取row个元素id
            List<String> currentColumSymbolIdList = super.randomCircularGenSymbolIdList(wheelChartModel.get(columNum), GAME_INFO.getRow());
            // 填充
            for (int rowNum = 0; rowNum < GAME_INFO.getRow(); rowNum++) {
                // 如果该位置已经有珍珠，则跳过
                if (symbolArr[rowNum][columNum] != null) {
                    continue;
                }
                SymbolDo symbolDo = GAME_INFO.getSymbolById(currentColumSymbolIdList.get(rowNum));
                AssertUtil.assertNotNull(symbolDo, ErrorCode.ERR_INTER, "symbolDo is null of id " + currentColumSymbolIdList.get(rowNum));
                // 构建SirenaSymbolDo
                SirenaSymbolDo sirenaSymbolDo = this.buildSirenaSymbol(symbolDo);
                if(!isScatter && sirenaSymbolDo.isScatterSymbol()){
                    sirenaSymbolDo = this.buildSirenaSymbol( SirenaGameHelper.SIRENA_Symbol1009);
                }
                // 填充
                symbolArr[rowNum][columNum] = sirenaSymbolDo;
            }
        }
        if(freeFlag) {
            // 填充完之后 从右上角遍历,如果珍珠组成2*2的格子，则将 [0][0] 替换为 sirenaBigPearlWild,其他3个位置替换为 sirenaSymbolPlaceholder
            SymbolDo sirenaBigPearlWild = SirenaGameHelper.SIRENA_Big_Pearl_Wild;
            SirenaSymbolDo sirenaSymbolPlaceholder = SirenaGameHelper.SIRENA_SYMBOL_PLACEHOLDER;

            // 从右上角开始遍历，检查2*2格子
            for (int row = 0; row < GAME_INFO.getRow() - 1; row++) {
                for (int col = GAME_INFO.getColumn() - 1; col > 0; col--) {
                    // 检查当前2*2格子是否都是珍珠
                    if (isPearl2x2Grid(symbolArr, row, col)) {
                        // 将[0][0]位置替换为大珍珠
                        symbolArr[0][0] = SirenaSymbolDo.buildPearlWildSymbol(sirenaBigPearlWild, BigDecimal.ZERO);
                        // 将其他3个位置替换为占位符
                        symbolArr[row][col] = sirenaSymbolPlaceholder;
                        symbolArr[row][col - 1] = sirenaSymbolPlaceholder;
                        symbolArr[row + 1][col] = sirenaSymbolPlaceholder;
                        symbolArr[row + 1][col - 1] = sirenaSymbolPlaceholder;
                        // 继续遍历，替换所有2*2格子
                    }
                }
            }
        }

        // 返回：滚轮
        return WheelDo.<SirenaSymbolDo>builder()
                .index(WHEEL_DEFAULT_INDEX)
                .freeFlag(freeFlag)
                .symbolArr(symbolArr)
                .build();
    }

    /**
     * 生成排除指定坐标外的随机位置
     *
     * @param excludeIndexMap 需要排除的坐标映射
     * @param count 需要生成的随机位置数量
     * @return 随机位置列表
     */
    private List<IndexDo> generateRandomPositionsExcluding(Map<IndexDo, SirenaSymbolDo> excludeIndexMap, int count) {
        List<IndexDo> availablePositions = new ArrayList<>();

        // 将排除的坐标转换为Set<String>，使用坐标字符串作为key
        Set<String> excludeCoordinates = new HashSet<>();
        if (excludeIndexMap != null) {
            for (IndexDo indexDo : excludeIndexMap.keySet()) {
                excludeCoordinates.add(indexDo.getRowIndex() + "," + indexDo.getColumnIndex());
            }
        }

        // 生成所有可用位置（排除指定坐标和第一列）
        for (int row = 0; row < GAME_INFO.getRow(); row++) {
            for (int col = 0; col < GAME_INFO.getColumn(); col++) {
                // 排除第一列（列索引为0）
                if (col == 0) {
                    continue;
                }

                // 检查当前坐标是否在排除列表中
                String coordinateKey = row + "," + col;
                if (!excludeCoordinates.contains(coordinateKey)) {
                    IndexDo position = IndexDo.builder()
                            .rowIndex(row)
                            .columnIndex(col)
                            .build();
                    availablePositions.add(position);
                }
            }
        }

        // 如果可用位置数量少于请求数量，返回所有可用位置
        if (availablePositions.size() <= count) {
            return availablePositions;
        }

        // 随机选择指定数量的位置
        List<IndexDo> selectedPositions = new ArrayList<>();
        Set<Integer> selectedIndices = new HashSet<>();

        while (selectedPositions.size() < count) {
            int randomIndex = randomService.randomInt(0, availablePositions.size() - 1);
            if (!selectedIndices.contains(randomIndex)) {
                selectedIndices.add(randomIndex);
                selectedPositions.add(availablePositions.get(randomIndex));
            }
        }

        return selectedPositions;
    }

    /**
     * 检查指定位置的2*2格子是否都是珍珠
     *
     * @param symbolArr 符号数组
     * @param startRow 起始行
     * @param startCol 起始列
     * @return 如果2*2格子都是珍珠返回true，否则返回false
     */
    private boolean isPearl2x2Grid(SirenaSymbolDo[][] symbolArr, int startRow, int startCol) {
        // 检查边界
        if (startRow + 1 >= symbolArr.length || startCol - 1 < 0) {
            return false;
        }

        // 检查2*2格子的4个位置是否都是珍珠
        return isPearlSymbol(symbolArr[startRow][startCol]) &&
               isPearlSymbol(symbolArr[startRow][startCol - 1]) &&
               isPearlSymbol(symbolArr[startRow + 1][startCol]) &&
               isPearlSymbol(symbolArr[startRow + 1][startCol - 1]);
    }

    /**
     * 检查符号是否为珍珠
     *
     * @param symbol 符号
     * @return 如果是珍珠返回true，否则返回false
     */
    private boolean isPearlSymbol(SirenaSymbolDo symbol) {
        return symbol != null && StringUtils.equals(symbol.getId(), SirenaGameHelper.SIRENA_Pearl_Wild.getId());
    }

    private SirenaSymbolDo buildSirenaSymbol(SymbolDo symbolDo) {
        return SirenaSymbolDo.buildFromSymbol(symbolDo, BigDecimal.ZERO, null);
    }

    private Map<IndexDo, SirenaSymbolDo> getPearlWildIndexList(WheelDo<SirenaSymbolDo> wheel) {
        if (wheel == null) {
            return null;
        }
        Map<IndexDo, SirenaSymbolDo> sirenaSymbolDoMap = new HashMap<>();
        SirenaSymbolDo[][] symbolArr = wheel.getSymbolArr();
        // 使用传统for循环遍历二维数组以获取坐标
        for (int rowNum = 0; rowNum < symbolArr.length; rowNum++) {
            for (int columNum = 0; columNum < symbolArr[rowNum].length; columNum++) {
                SirenaSymbolDo symbol = symbolArr[rowNum][columNum];
                if (symbol.isConchAndPearlSymbol()) {
                    // 创建IndexDo对象来保存坐标信息
                    IndexDo indexDo = IndexDo.builder()
                            .rowIndex(rowNum)
                            .columnIndex(columNum)
                            .build();
                    sirenaSymbolDoMap.put(indexDo, symbol);
                }
            }
        }

        return sirenaSymbolDoMap;
    }

    /**
     * 获取轮代表moudel
     *
     * @param gamePlay gameplay
     * @return 轮代表moudle
     */
    private List<List<String>> buildWheelChartModel(GamePlayDo gamePlay, boolean isFreeSpinFlag) {
        TreeMap<String, BigDecimal> wheelChartProbabilisticConfig;
        // 获取轮代表
        if (isFreeSpinFlag) {
            // 免费轮
            wheelChartProbabilisticConfig = sirenaConfig.getProbabilisticConfig().getFreeWheelWheelChatWeightConfig();
        } else {
            // 付费轮-正常投注
            if (Objects.equals(gamePlay.getBetType(), BetTypeEnum.NORMAL_BET)) {
                // 判断是否保留了珍珠
                if (gamePlay.getContinuePlayContext() != null && gamePlay.getContinuePlayContext().getParentBetRecordId() != null) {
                    wheelChartProbabilisticConfig = sirenaConfig.getProbabilisticConfig().getSpecialWheelChatWeightConfig();
                } else {
                    wheelChartProbabilisticConfig = sirenaConfig.getProbabilisticConfig().getNormalBetWheelChatWeightConfig();
                }
            } else if (Objects.equals(gamePlay.getBetType(), BetTypeEnum.BUY_BET)) {
                // 付费轮-购买投注
                wheelChartProbabilisticConfig = sirenaConfig.getProbabilisticConfig().getBuyBetWheelChatWeightConfig();
            } else {
                throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT BET TYPE");
            }
        }
        // 根据概率随机出model名称
        String modelName = GameUtil.randomKey(wheelChartProbabilisticConfig, Constants.CONST_10000, randomService.randomInt(1, Constants.CONST_1000000));
        List<List<String>> wheelChartModel = sirenaConfig.getWheelChartConfig().get(modelName);
        AssertUtil.assertNotNull(wheelChartModel, ErrorCode.ERR_INTER, "wheelChartModel is null");
        return wheelChartModel;
    }

    private SirenaGameRecordDo buildGameRecord(Long id,
                                               Long parentId,
                                               Long betRecordId,
                                               WheelDo<SirenaSymbolDo> wheel,
                                               RewardResultDo<SirenaSymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return SirenaGameRecordDo.builder()
                .id(id)
                .parentId(parentId)
                .betRecordId(betRecordId)
                .batchId(id)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .index(wheel.getIndex())
                .freeWheelFlag(wheel.isFreeFlag())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .gameRecordIds(null)
                .lastGameFlag(false)
                .build();
    }

    @Data
    @SuperBuilder
    private static class FreeWheelHandleDo {
        // parentGameRecord
        private SirenaGameRecordDo parentGameRecord;
        // 任务执行次数
        private int taskTimes;
    }

    @Data
    @SuperBuilder
    private static class OneWheelPlayWrapDo {
        private SirenaGameRecordDo gameRecordDo;

        public RewardResultDo<SirenaSymbolDo> getRewardResult() {
            return gameRecordDo.getRewardResult();
        }

    }
}