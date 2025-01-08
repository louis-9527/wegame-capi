package com.wegame.capi.biz.converter;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.capi.biz.domain.biz.MariaMakilingBetRecordDetailDo;
import com.wegame.capi.client.domain.vo.BetRecordVo;

import java.util.Objects;

/**
 * MariaMakilingBetRecordDetailConverter
 *
 * @author generate
 */
public class MariaMakilingBetRecordDetailConverter {

    public static BetRecordVo.MariaMakilingBetRecordDetailVo doToVo(MariaMakilingBetRecordDetailDo detailDo) {
        if (Objects.isNull(detailDo) || CollectionUtil.isEmpty(detailDo.getGameRecordList())) {
            return null;
        }
        BetRecordVo.MariaMakilingBetRecordDetailVo vo = new BetRecordVo.MariaMakilingBetRecordDetailVo();
        vo.setGameRecordList(MariaMakilingGameRecordConverter.doToVoList(detailDo.getGameRecordList()));
        return vo;
    }

}