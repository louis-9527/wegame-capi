package com.wegame.capi.biz.service.promotion;

import com.mongodb.client.result.UpdateResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.promotion.RedeemRewardRes;
import com.wegame.capi.api.promotion.BagApi;
import com.wegame.capi.biz.builder.StarCardUseRecordQueryBuilder;
import com.wegame.capi.biz.domain.biz.promotion.RedeemRewardResultDo;
import com.wegame.capi.biz.domain.biz.promotion.StarCardUseRecordDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.StarCardUseTypeEnum;
import com.wegame.capi.dao.mapper.domain.star.StarCardUseRecordPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import static com.wegame.capi.biz.builder.req.RedeemRewardReqBuilder.build;
import static com.wegame.capi.biz.converter.promotion.StartCardUseRecordConvert.doToPo;

/**
 * 星卡service
 */
@Service
public class StarCardUseRecordService extends AbstractService {

    @Autowired
    BagApi bagApi;

    public Long add(StarCardUseRecordDo useRecordDo, GameTypeEnum gameType) {
        StarCardUseTypeEnum starCardUseType = StarCardUseTypeEnum.valueOfGameType(gameType);
        AssertUtil.assertNotNull(starCardUseType, ErrorCode.ERR_INTER);
        StarCardUseRecordPo poToInsert = doToPo(useRecordDo, OptEnum.ADD);
        StarCardUseRecordPo po = getMongoTemplate(gameType).insert(poToInsert, starCardUseType.getCollectionName());
        return po.getId();
    }

    public Boolean update(StarCardUseRecordDo useRecordDo, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(useRecordDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(useRecordDo.getId(), ErrorCode.ERR_PARAM, "param \'id\' is null");
        Criteria criteria = new Criteria();
        criteria.and("_id").is(useRecordDo.getId());
        Query query = new Query(criteria);
        UpdateResult result = getMongoTemplate(gameType).updateFirst(query,
                StarCardUseRecordQueryBuilder.buildMongoUpdate(useRecordDo),
                StarCardUseRecordPo.class,
                gameType.getBetRecordCollectionName());
        return result.getModifiedCount() > 0;
    }
}
