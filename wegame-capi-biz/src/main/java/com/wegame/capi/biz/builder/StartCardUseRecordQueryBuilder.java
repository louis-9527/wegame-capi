package com.wegame.capi.biz.builder;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.promotion.StartCardUseRecordDo;
import com.wegame.capi.dao.mapper.domain.business.BetRecordQuery;
import com.wegame.capi.dao.mapper.domain.business.StartCardUseRecordQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Objects;

public class StartCardUseRecordQueryBuilder {

    public static Update buildMongoUpdate(StartCardUseRecordDo recordDo) {
        if (recordDo == null) {
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
        Update update = new Update();
        if (Objects.nonNull(recordDo.getReportStatus())) {
            update.set("reportStatus", recordDo.getReportStatus().getCode());
        }
        if (Objects.nonNull(recordDo.getUseStatus())) {
            update.set("useStatus", recordDo.getUseStatus().getCode());
        }
        if (Objects.nonNull(recordDo.getFailMsg())) {
            update.set("failMsg", recordDo.getFailMsg());
        }
        if (Objects.nonNull(recordDo.getUpdateTime())) {
            update.set("updateTime", recordDo.getUpdateTime());
        }else {
            update.set("updateTime", DateUtil.getCurrentMilli());
        }
        return update;
    }

}
