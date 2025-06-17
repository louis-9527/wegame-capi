package com.wegame.capi.biz.module.diawatamayari.game;

import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.DiwataMayariConfig;
import com.wegame.capi.biz.domain.biz.game.DiwataMayariGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.*;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.diawatamayari.DiwataMayariGameHelper;
import com.wegame.capi.biz.module.diawatamayari.DiwataMayariSymbolUtil;
import com.wegame.capi.biz.module.diawatamayari.reward.DiwataMayariRewardHandler;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.RewardTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.wegame.capi.common.Constants.CONST_1000000;
import static com.wegame.capi.common.constants.GameConstants.*;

@Slf4j
@Component("diwataMayariGameNormalPlay")
public class DiwataMayariGameNormalPlay extends AbstractGame<DiwataMayariGameRecordDo> {
    private static final Integer WHEEL_DEFAULT_INDEX = 0;
    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo GAME_INFO = DiwataMayariGameHelper.DIWATA_MAYARI_GAME_INFO;

    @Autowired
    DiwataMayariConfig diwataMayariConfig;
    @Autowired
    DiwataMayariRewardHandler diwataMayariRewardHandler;

    @Override
    public List<DiwataMayariGameRecordDo> play(GamePlayDo gamePlay) {
        List<DiwataMayariGameRecordDo> ret = new ArrayList<>();
        // 生成-游戏滚轮
        WheelDo<DiwataMayariSymbolDo> wheel;
        long gameRecordId = generateGameRecordId();
        switch (gamePlay.getBetType()) {
            // 正常投注
            case NORMAL_BET:
                wheel = generateNormalWheel( false);
                break;
            // 购买投注
            case BUY_BET:
                wheel = generateRewardFreeWheel( false);
                break;
            default:
                throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT");
        }
        // 判奖-根据游戏滚轮和中奖线算法判断
        AtomicLong totalRewardAmount = new AtomicLong(0L);
        // 计算中奖线
        RewardHandleDo<DiwataMayariSymbolDo> buildRewardReq = RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, BigDecimal.ZERO, totalRewardAmount.get());
        RewardResultDo<DiwataMayariSymbolDo> rewardResult = diwataMayariRewardHandler.handle(buildRewardReq);
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        // 组装-游戏记录
        DiwataMayariGameRecordDo gameRecord = buildGameRecord(gameRecordId, RECORD_DEFAULT_PARENT_ID, gamePlay.getBetRecordId(), gameRecordId, wheel, rewardResult);
        ret.add(gameRecord);
        totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
        // 判断首轮是否中奖
        if(this.canContinuePlayOnPreWheel(gamePlay, rewardResult,totalRewardAmount)){
            //  继续游戏
            LinkedList<Long> freeWheelList = new LinkedList<>();
            // 处理消除轮
            loopPlayOnPreGameRecord(gamePlay, ret, totalRewardAmount, gameRecord, freeWheelList);
            //  处理免费游戏
            handleFreeWheelGamePlay(gamePlay, ret, totalRewardAmount, freeWheelList);
        }
        if (CollectionUtils.isNotEmpty(ret)) {
            ret.get(ret.size() - 1).setLastGameFlag(true);
        }
        // 占位元素-将占位元素设置为空:
       DiwataMayariSymbolUtil.postProcessPlaceholderSymbol(ret);
        return ret;
    }

    /**
     * 生成正常滚轮：生成逻辑无任何限制
     *
     * @return 滚轮
     */
    private WheelDo<DiwataMayariSymbolDo> generateNormalWheel(boolean freeWheelFlag) {
        DiwataMayariSymbolDo[][] symbolArr = new DiwataMayariSymbolDo[GAME_INFO.getRow()][GAME_INFO.getColumn()];
        // 不生成元素,使用占位元素
        symbolArr[GAME_INFO.getRow()-1][ONE_COLUM_INDEX] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;
        symbolArr[GAME_INFO.getRow()-1][TWO_COLUM_INDEX] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;
        symbolArr[GAME_INFO.getRow()-1][GAME_INFO.getColumn() - 1] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;
        symbolArr[GAME_INFO.getRow()-1][GAME_INFO.getColumn() - 2] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;

        symbolArr[GAME_INFO.getRow()-2][ONE_COLUM_INDEX] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;
        symbolArr[GAME_INFO.getRow()-2][GAME_INFO.getColumn() - 1] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;

        // 生成首轮
        return generateCommonNormalWheel(
                symbolArr,
                true,
                WHEEL_DEFAULT_INDEX,
                freeWheelFlag);
    }

    /**
     * 生成正常滚轮：生成逻辑无任何限制
     * @param symbolArr 游戏轮盘
     * @param first 是否首轮
     * @param wheelIndex 滚轮序号
     * @param freeFlag 收否免费轮
     * @return
     */

    private WheelDo<DiwataMayariSymbolDo> generateCommonNormalWheel(DiwataMayariSymbolDo[][] symbolArr,
                                                               boolean first,
                                                               int wheelIndex,
                                                               boolean freeFlag) {
        // 生成整盘随机数(row*column)
        List<Integer> randomNumberList = randomService.randomIntList(1, CONST_1000000, GAME_INFO.getRow() * GAME_INFO.getColumn());
        List<List<Integer>> partitionRandom = Lists.partition(randomNumberList, GAME_INFO.getColumn());
        TreeMap<String, List<BigDecimal>> commonPlayProbabilisticConfig = this.getStringListTreeMap(first, freeFlag);
        //  生成-行列概率
        for (int column = ONE_COLUM_INDEX; column < GAME_INFO.getColumn(); column++) {
            TreeMap<String, Integer> columProbabilisticMap = super.buildColumProbabilisticMap(column,commonPlayProbabilisticConfig);
           this.buildRowSymbol(symbolArr, column, columProbabilisticMap, partitionRandom.get(column));
        }
        // 返回：滚轮
        return WheelDo.<DiwataMayariSymbolDo>builder()
                .index(wheelIndex)
                .freeFlag(freeFlag)
                .symbolArr(symbolArr)
                .build();
    }

    private TreeMap<String, List<BigDecimal>> getStringListTreeMap(boolean first, boolean freeFlag) {
        TreeMap<String, List<BigDecimal>> commonPlayProbabilisticConfig;
        if(first){
            if(freeFlag){
                commonPlayProbabilisticConfig = diwataMayariConfig.getProbabilisticConfig().getFreePlayFirstSpinProbabilisticConfig();
            }else {
                commonPlayProbabilisticConfig = diwataMayariConfig.getProbabilisticConfig().getCommonPlayFirstSpinProbabilisticConfig();
            }
        }else {
            if(freeFlag){
                commonPlayProbabilisticConfig = diwataMayariConfig.getProbabilisticConfig().getFreePlayRoundProbabilisticConfig();
            }else {
                commonPlayProbabilisticConfig = diwataMayariConfig.getProbabilisticConfig().getCommonPlayRoundProbabilisticConfig();
            }
        }
        return commonPlayProbabilisticConfig;
    }

    /**
     * 生成中奖免费轮滚轮: 需要满足3-6个Scatter
     * @return 滚轮
     */
    private WheelDo<DiwataMayariSymbolDo> generateRewardFreeWheel(boolean freeWheelFlag) {
        DiwataMayariSymbolDo[][] symbolArr = new DiwataMayariSymbolDo[GAME_INFO.getRow()][GAME_INFO.getColumn()];
        // 不生成元素,使用占位元素
        symbolArr[GAME_INFO.getRow()-1][ONE_COLUM_INDEX] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;
        symbolArr[GAME_INFO.getRow()-1][TWO_COLUM_INDEX] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;
        symbolArr[GAME_INFO.getRow()-1][GAME_INFO.getColumn() - 1] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;
        symbolArr[GAME_INFO.getRow()-1][GAME_INFO.getColumn() - 2] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;

        symbolArr[GAME_INFO.getRow()-2][ONE_COLUM_INDEX] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;
        symbolArr[GAME_INFO.getRow()-2][GAME_INFO.getColumn() - 1] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;

        WheelDo<DiwataMayariSymbolDo> wheel = generateCommonNormalWheel(symbolArr, true, WHEEL_DEFAULT_INDEX, freeWheelFlag);
        // 计算scatter数量
        int scatterSymbolCount = DiwataMayariSymbolUtil.countIndexSymbol(wheel.getSymbolArr(), SymbolEnum.Scatter);
        if(scatterSymbolCount >=3){
            return wheel;
        }
        int randomScatterSymbolCount = randomService.randomInt(3, 6);
        int needAddCount = randomScatterSymbolCount - scatterSymbolCount;
        if (needAddCount <= 0) {
            return wheel;
        }

        // 提取有效坐标集合
        List<IndexDo> indexList = new ArrayList<>();
        for (int row = 1; row < GAME_INFO.getRow(); row++) {
            for (int column = 0; column < GAME_INFO.getColumn(); column++) {
                DiwataMayariSymbolDo symbol = wheel.getSymbolArr()[row][column];
                if (Objects.nonNull(symbol)) {
                    if (!symbol.isPlaceholderSymbol() && !symbol.isScatterSymbol() && !symbol.isWildSymbol()) {
                        indexList.add(IndexFactory.buildDo(row, column));
                    }
                }
            }
        }

        // indexList 中随机取 needAddCount 个坐标 替换为scatter
        Collections.shuffle(indexList, new Random());
        for (int i=0;i<needAddCount;i++){
            IndexDo indexDo = indexList.get(i);
            DiwataMayariSymbolDo[][] symbolArr1 = wheel.getSymbolArr();
            symbolArr1[indexDo.getRowIndex()][indexDo.getColumnIndex()] = DiwataMayariSymbolDo.buildFromSymbol(DiwataMayariGameHelper.DIWATA_MAYARI_SCATTER);
        }
        return wheel;
    }


    public SymbolDo randomNonWildSymbol(Integer randomNumber, TreeMap<String, Integer> columnProbabilisticMap) {
        Integer tempRandomNumber = randomNumber;
        while (true) {
            SymbolEnum symbol = randomOneSymbol(columnProbabilisticMap, tempRandomNumber);
            SymbolDo symbolDo = GAME_INFO.getSymbolById(symbol.getId());
            DiwataMayariSymbolDo diwataMayariSymbolDo = DiwataMayariSymbolDo.buildFromSymbol(symbolDo);

            if (!diwataMayariSymbolDo.isSword() && !diwataMayariSymbolDo.isStick()) {
                return symbolDo;
            }
            tempRandomNumber = randomService.randomInt(1, Constants.CONST_1000000);
        }
    }

    /**
     * 构建行元素
     * @param symbolArr
     * @param column
     */
    private void buildRowSymbol(DiwataMayariSymbolDo[][] symbolArr, int column,TreeMap<String, Integer> columProbabilisticMap,List<Integer> randomNumberList) {
        boolean isScatter = false;
        for (int row = ONE_ROW_INDEX; row < GAME_INFO.getRow(); row++) {
            if (Objects.nonNull(symbolArr[row][column])) {
                continue;
            }

            Integer randomNumber = randomNumberList.get(ONE_ROW_INDEX);
            // 随机生成滚轮游戏
            SymbolDo symbolDo = randomNormalSymbol(randomNumber, GAME_INFO, columProbabilisticMap);
            DiwataMayariSymbolDo diwataMayariSymbolDo = DiwataMayariSymbolDo.buildFromSymbol(symbolDo);

            if (diwataMayariSymbolDo.isScatterSymbol()) {
                //scatter 元素
                isScatter = true;
                symbolArr[row][column] = diwataMayariSymbolDo;
                continue;
            }
            // 棒子元素
            if (diwataMayariSymbolDo.isSword()) {
                if (!isScatter) {
                    this.convertSword(symbolArr, column, row);
                } else {
                    // 重新生成非 棒子元素
                    SymbolDo symbolDoNoSword = this.randomNonWildSymbol(randomNumber, columProbabilisticMap);
                    symbolArr[row][column] = DiwataMayariSymbolDo.buildFromSymbol(symbolDoNoSword);
                    continue;
                }
            }
            // 大宝剑元素
            if (diwataMayariSymbolDo.isStick()) {
                if (!isScatter) {
                    this.convertStick(symbolArr, column);
                    break;
                } else {
                    // 重新生成非 大宝剑元素
                    SymbolDo symbolDoNoStick = this.randomNonWildSymbol(randomNumber, columProbabilisticMap);
                    symbolArr[row][column] = DiwataMayariSymbolDo.buildFromSymbol(symbolDoNoStick);
                }
            }
        }
    }


    /**
     * 将colum 中所有元素转换为wild
     * @param symbolArr
     * @param column
     */
    private void convertStick(DiwataMayariSymbolDo[][] symbolArr, int column){
            for (int row = ONE_ROW_INDEX; row < GAME_INFO.getRow(); row++){
                if(symbolArr[row][column] !=null && symbolArr[row][column].isPlaceholderSymbol()){
                    symbolArr[row][column] = DiwataMayariSymbolDo.buildFromSymbol(DiwataMayariGameHelper.DIWATA_MAYARI_SCATTER);
                }
            }
    }

    /**
     *  棒子周围元素生成规则
     * @param symbolArr
     * @param column
     * @param row
     */
    private void convertSword(DiwataMayariSymbolDo[][] symbolArr, int column, int row) {
        List<int[]> positions = new ArrayList<>();
        // 收集所有有效位置
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newColumn = column + j;
                if (newRow >= 0 && newRow < GAME_INFO.getRow() && newColumn >= 0 && newColumn < GAME_INFO.getColumn()) {
                    positions.add(new int[]{newRow, newColumn});
                }
            }
        }
        
        // 随机打乱位置列表
        Collections.shuffle(positions,  new Random());
        // 确保中心位置在选中的位置中
        int[] centerPos = new int[]{row, column};
        positions.removeIf(pos -> pos[0] == centerPos[0] && pos[1] == centerPos[1]);
        positions.add(0, centerPos);
        
        // 只取前4个位置进行替换
        positions.stream()
                .limit(4)
                .forEach(pos -> {
                    int newRow = pos[0];
                    int newColumn = pos[1];
                    DiwataMayariSymbolDo symbol = symbolArr[newRow][newColumn];
                    if (symbol == null || !symbol.isPlaceholderSymbol()) {
                        symbolArr[newRow][newColumn] = DiwataMayariSymbolDo.buildFromSymbol(DiwataMayariGameHelper.DIWATA_MAYARI_WILD_SWORD);
                    } else if (symbol.isSword()) {
                        symbolArr[newRow][newColumn] = DiwataMayariSymbolDo.buildFromSymbol(DiwataMayariGameHelper.DIWATA_MAYARI_WILD_COVER);
                    }
                });
    }

    /**
     *  处理免费轮
     * @param gamePlay
     * @param ret
     * @param totalRewardAmount
     * @param freeWheelList
     */
    private void handleFreeWheelGamePlay(GamePlayDo gamePlay,
                                         List<DiwataMayariGameRecordDo> ret,
                                         AtomicLong totalRewardAmount,
                                         LinkedList<Long> freeWheelList) {
        BigDecimal totalMultipleAmount = new BigDecimal("0");
        Map<Long, DiwataMayariGameRecordDo> retMap = ret.stream().collect(Collectors.toMap(DiwataMayariGameRecordDo::getId, item -> item));
        while (!freeWheelList.isEmpty()) {
            long  reeWheel = freeWheelList.removeFirst();
            // 获取免费轮的父投注记录
            DiwataMayariGameRecordDo parentGameRecord = retMap.get(reeWheel);
            AssertUtil.assertNotNull(parentGameRecord, ErrorCode.ERR_INTER);
            // 生成首轮元素
            WheelDo<DiwataMayariSymbolDo> firstWheelDo = this.generateNormalWheel(true);
            firstWheelDo.setFreeFlag(true);
            // 判奖-根据游戏滚轮和中奖线算法判断
            RewardHandleDo<DiwataMayariSymbolDo> buildRewardReq = RewardHandlerBuilder.build(GAME_INFO, gamePlay, firstWheelDo, totalMultipleAmount, totalRewardAmount.get());
            RewardResultDo<DiwataMayariSymbolDo> rewardResult = diwataMayariRewardHandler.handle(buildRewardReq);
            AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
            // 组装-游戏记录
            long gameRecordId = generateGameRecordId();
            DiwataMayariGameRecordDo gameRecord = this.buildGameRecord(gameRecordId, reeWheel, gamePlay.getBetRecordId(), gameRecordId, firstWheelDo, rewardResult);
            ret.add(gameRecord);
            totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
            if (CollectionUtils.isEmpty(parentGameRecord.getGameRecordIds())) {
                parentGameRecord.setGameRecordIds(new ArrayList<>());
            }
            parentGameRecord.getGameRecordIds().add(gameRecordId);
            // 判断首轮是否中奖
            if(this.canContinuePlayOnPreWheel(gamePlay, rewardResult,totalRewardAmount)){
                //  继续游戏
                LinkedList<Long> freeList = new LinkedList<>();
                // 处理当前消除
                loopPlayOnPreGameRecord(gamePlay, ret, totalRewardAmount, gameRecord, freeList);
                // 消除后的免费轮
                handleFreeWheelGamePlay(gamePlay, ret, totalRewardAmount, freeList);
            }
            if (CollectionUtils.isNotEmpty(ret)) {
                totalMultipleAmount = ret.get(ret.size() - 1).getRewardResult().getTotalMultiple();
            }
        }
    }

    /**
     * 计算所有消除滚轮结果(一回合,消除后未中奖或达到最大倍数就退出)
     */
    private void loopPlayOnPreGameRecord(GamePlayDo gamePlay,
                                        List<DiwataMayariGameRecordDo> ret,
                                        AtomicLong totalRewardAmount,
                                         DiwataMayariGameRecordDo preGameRecord,
                                        LinkedList<Long> freeWheelList) {

        // 根据上一轮滚轮生成新的滚轮
        // 上一轮中奖元素
        List<RewardSymbolDo<DiwataMayariSymbolDo>> rewardSymbolList =  preGameRecord.getRewardResult().getRewardLineDetailList()
                .stream()
                .map(RewardLineDetailDo<DiwataMayariSymbolDo>::getRewardSymbolList).flatMap(List::stream).collect(Collectors.toList());
        // 生成新的滚轮
        WheelDo<DiwataMayariSymbolDo> wheel = this.generateNormalWheelFromPreWheel(preGameRecord.getWheel(),rewardSymbolList, preGameRecord.getFreeWheelFlag());
        // 计算中奖结果
        RewardHandleDo<DiwataMayariSymbolDo> buildRewardReq = RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, preGameRecord.getRewardResult().getTotalMultiple(), totalRewardAmount.get());
        RewardResultDo<DiwataMayariSymbolDo> rewardResult = diwataMayariRewardHandler.handle(buildRewardReq);
        // 构建投注记录
        long gameRecordId = generateGameRecordId();
        DiwataMayariGameRecordDo gameRecord = this.buildGameRecord(gameRecordId, preGameRecord.getId(), gamePlay.getBetRecordId(), preGameRecord.getBatchId(), wheel, rewardResult);
        ret.add(gameRecord);
        totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
        // 判断是否中奖或达到最大倍数
        if(canContinuePlayOnPreWheel(gamePlay, rewardResult, totalRewardAmount)){
            if(rewardResult.isHasRewardFreeWheel()){
                // 中免费轮
                for (int times = 1; times <= rewardResult.getRewardFreeWheelTimes(); times++) {
                    freeWheelList.addLast(gameRecordId);
                }
            }
            loopPlayOnPreGameRecord(gamePlay, ret, totalRewardAmount, gameRecord, freeWheelList);
        }
    }

    /**
     * 上次滚轮重新生成新的滚轮
     * preWheel :  上次滚轮
     * preRewardResult :  上次中奖结果
     * freeWheelFlag : 上次是否命中免费轮
     * @return 新的滚轮
     */
    private WheelDo<DiwataMayariSymbolDo> generateNormalWheelFromPreWheel(WheelDo<DiwataMayariSymbolDo> preWheel,
                                                                          List<RewardSymbolDo<DiwataMayariSymbolDo>> preRewardResult,
                                                                          boolean freeWheelFlag) {
        // 复制：元素数组
        DiwataMayariSymbolDo[][] symbolArr = DiwataMayariSymbolUtil.copySymbolArr(preWheel.getSymbolArr());
        // 消除：中奖元素
        DiwataMayariSymbolUtil.clearRewardSymbol(symbolArr, preRewardResult);
        // 重建：元素数组（非空元素按列下落处理：第1-6Reel非空元素向下移动，第7Reel非空元素向左移动）
        DiwataMayariSymbolUtil.rebuildSymbolArr(symbolArr, GAME_INFO);
        // 生产：缺失元素(填补剩余元素)
        return generateCommonNormalWheel(
                symbolArr,
                false,
                preWheel.getIndex() + 1,
                freeWheelFlag);
    }

    // 判断是否可以继续生成
    private boolean canContinuePlayOnPreWheel(GamePlayDo gamePlay, RewardResultDo<DiwataMayariSymbolDo> rewardResult, AtomicLong totalRewardAmount) {
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
    private DiwataMayariGameRecordDo buildGameRecord(Long id,
                                                Long parentId,
                                                Long betRecordId,
                                                Long batchId,
                                                WheelDo<DiwataMayariSymbolDo> wheel,
                                                RewardResultDo<DiwataMayariSymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return DiwataMayariGameRecordDo.builder()
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
                .build();
    }

}