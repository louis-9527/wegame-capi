package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.MariaMakilingBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingBetDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.module.bet.strategy.AbstractBetStrategy;
import com.wegame.capi.biz.module.game.Game;
import com.wegame.capi.biz.module.game.MariaMakilingGame;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractMariaMakilingBetStrategy extends AbstractBetStrategy<MariaMakilingBetDo, MariaMakilingGameRecordDo> {

    @Autowired
    MariaMakilingGame mariaMakilingGame;

    @Override
    public Game<MariaMakilingGameRecordDo> getGame() {
        return mariaMakilingGame;
    }

    public MariaMakilingGameRecordDo queryGameRecord(Long betRecordId, Integer freeSpinsId) {
        BetRecordDo<MariaMakilingBetRecordDetailDo> betRecordDo = betRecordService.get(betRecordId);
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_PARAM);
        MariaMakilingBetRecordDetailDo betRecordDetailDo = betRecordDo.getDetail();
        AssertUtil.assertNotNull(betRecordDetailDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotEmpty(betRecordDetailDo.getGameRecordList(), ErrorCode.ERR_PARAM);
        List<MariaMakilingGameRecordDo> gameRecordList = betRecordDetailDo.getGameRecordList();

        MariaMakilingGameRecordDo mariaMakilingGameRecordDo = gameRecordList
                .stream()
                .filter(item -> Objects.equals(item.getIndex(), freeSpinsId))
                .findFirst()
                .orElse(null);
            AssertUtil.assertNotNull(mariaMakilingGameRecordDo, ErrorCode.ERR_PARAM);
            // 如果最后一次查询则，返回总的中奖金额
        if (betRecordDetailDo.isLastFreeSpins(freeSpinsId)) {
            mariaMakilingGameRecordDo.setRewardTotalAmount(betRecordDo.getPayOutAmount());
        }
        return mariaMakilingGameRecordDo;
    }
}
