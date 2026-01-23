package com.wegame.capi.biz.module.bayanihan;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.BayanihanSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
public class BayanihanSymbolUtil {

    public static BayanihanSymbolDo[][] copySymbolArr(BayanihanSymbolDo[][] symbolArr) {
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        BayanihanSymbolDo[][] newSymbolArr = new BayanihanSymbolDo[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (Objects.isNull(symbolArr[i][j])) {
                    newSymbolArr[i][j] = null;
                } else {
                    newSymbolArr[i][j] = symbolArr[i][j].copy();
                }
            }
        }
        return newSymbolArr;
    }

    public static int countScatterInWheel(BayanihanSymbolDo[][] symbolArr) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        int count = 0;
        for (int row = 0; row < symbolArr.length; row++) {
            for (int col = 0; col < symbolArr[row].length; col++) {
                if (Objects.nonNull(symbolArr[row][col]) && symbolArr[row][col].isScatterSymbol()) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    public static int countScatterInColumn(BayanihanSymbolDo[][] symbolArr, int column) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        int count = 0;
        for (int row = ONE_ROW_INDEX; row < symbolArr.length; row++) {
            if (Objects.nonNull(symbolArr[row][column]) && symbolArr[row][column].isScatterSymbol()) {
                count = count + 1;
            }
        }
        return count;
    }

    public static int countNewSymbolInColumn(BayanihanSymbolDo[][] symbolArr, String symbolId) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        int count = 0;
        for (int row = 0; row < symbolArr.length; row++) {
            for (int col = 0; col < symbolArr[row].length; col++) {
                if (Objects.nonNull(symbolArr[row][col]) && symbolArr[row][col].isNewSymbol() && Objects.equals(symbolId, symbolArr[row][col].getId())) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    /**
     * 消除中奖元素 <br/>
     */
    public static void clearRewardSymbol(BayanihanSymbolDo[][] symbolArr, RewardResultDo<BayanihanSymbolDo> rewardResult) {
        List<RewardSymbolDo<BayanihanSymbolDo>> rewardSymbolDoList = getRewardSymbolDoList(rewardResult);
        if (CollectionUtils.isNotEmpty(rewardSymbolDoList)) {
            rewardSymbolDoList.parallelStream().forEach(rewardSymbol -> clearIndexRewardSymbol(symbolArr, rewardSymbol));
        }
    }

    public static List<RewardSymbolDo<BayanihanSymbolDo>> getRewardSymbolDoList(RewardResultDo<BayanihanSymbolDo> rewardResult) {
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        AssertUtil.assertNotEmpty(rewardResult.getRewardLineDetailList(), ErrorCode.ERR_INTER);
        return rewardResult.getRewardLineDetailList()
                .stream()
                .map(RewardLineDetailDo<BayanihanSymbolDo>::getRewardSymbolList).flatMap(List::stream).collect(Collectors.toList());
    }

    private static void clearIndexRewardSymbol(BayanihanSymbolDo[][] symbolArr, RewardSymbolDo<BayanihanSymbolDo> rewardSymbol) {
        if (rewardSymbol.getSymbol().isScatterSymbol()) {
            // scatter 元素， 不做消除
        } else if (rewardSymbol.getSymbol().isWildSymbol()) {
            // wild 和 占位符 元素,直接移除
            //todo 如果有次数，则减少次数，否则移除
            removeSymbol(symbolArr, rewardSymbol);
        } else {
            // 普通元素消除
            removeSymbol(symbolArr, rewardSymbol);
        }
    }

    /**
     * 移除元素
     */
    private static void removeSymbol(BayanihanSymbolDo[][] symbolArr, RewardSymbolDo<BayanihanSymbolDo> rewardSymbol) {
        IndexDo indexDo = rewardSymbol.getIndex();
        // 直接移除
        symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = null;
    }


}
