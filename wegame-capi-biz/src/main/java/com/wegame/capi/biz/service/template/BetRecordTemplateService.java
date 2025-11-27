package com.wegame.capi.biz.service.template;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.operation.BetRecordTemplateDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import com.wegame.capi.dao.repository.domain.business.BetRecordPo;
import com.wegame.capi.dao.repository.domain.template.BetRecordTemplatePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wegame.capi.biz.converter.BetRecordTemplateConverter.doToPo;
import static com.wegame.capi.biz.converter.BetRecordTemplateConverter.doToPoList;

@Slf4j
@Service
public class BetRecordTemplateService extends AbstractService {

    public Long add(BetRecordTemplateDo betRecordTemplateDo, TemplateTypeEnum templateTypeEnum) {
        BetRecordTemplatePo po = getMongoTemplate(templateTypeEnum.getGameType()).insert(doToPo(betRecordTemplateDo, OptEnum.ADD),
                templateTypeEnum.getBetRecordTemplateName());
        return po.getId();
    }

    public boolean batchAdd(List<BetRecordTemplateDo> betRecordList, TemplateTypeEnum templateTypeEnum) {
        AssertUtil.assertNotEmpty(betRecordList, ErrorCode.ERR_INTER);
        AssertUtil.assertNotEmpty(betRecordList, ErrorCode.ERR_PARAM, "betRecordTemplateList is empty");
        AssertUtil.assertNotNull(templateTypeEnum, ErrorCode.ERR_PARAM, "gameType is null");
        List<BetRecordTemplatePo> poList = doToPoList(betRecordList, OptEnum.ADD);
        BulkOperations bulkOps = getMongoTemplate(templateTypeEnum.getGameType()).bulkOps(BulkOperations.BulkMode.UNORDERED,
                BetRecordPo.class,
                templateTypeEnum.getBetRecordTemplateName());
        poList.forEach(bulkOps::insert);
        bulkOps.execute();
        return true;
    }
}
