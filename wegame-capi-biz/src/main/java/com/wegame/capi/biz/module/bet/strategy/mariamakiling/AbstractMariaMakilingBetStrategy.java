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

    public MariaMakilingGameRecordDo queryFreeBetRecord(MariaMakilingBetDo betDetail) {
        BetRecordDo<MariaMakilingBetRecordDetailDo> betRecordDo = betRecordService.get(betDetail.getBetRecordId());
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_PARAM);
        MariaMakilingBetRecordDetailDo betRecordDetailDo = betRecordDo.getDetail();
        AssertUtil.assertNotNull(betRecordDetailDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotEmpty(betRecordDetailDo.getGameRecordList(), ErrorCode.ERR_PARAM);
        List<MariaMakilingGameRecordDo> gameRecordList = betRecordDetailDo.getGameRecordList();

        //获取最大ID
        Optional<Integer> maxId = gameRecordList.stream()
                .map(MariaMakilingGameRecordDo::getIndex)
                .max(Integer::compareTo);
        MariaMakilingGameRecordDo mariaMakilingGameRecordDo = gameRecordList
                .stream()
                .filter(item -> Objects.equals(item.getIndex(), betDetail.getFreeSpinsId()))
                .findFirst()
                .orElse(null);
        // 如果最后一次查询则，返回总的中奖金额
        if (mariaMakilingGameRecordDo != null && Objects.equals(mariaMakilingGameRecordDo.getIndex(), maxId.get())) {
            mariaMakilingGameRecordDo.setRewardTotalAmount(betRecordDo.getPayOutAmount());
        }
        return mariaMakilingGameRecordDo;
    }
}
