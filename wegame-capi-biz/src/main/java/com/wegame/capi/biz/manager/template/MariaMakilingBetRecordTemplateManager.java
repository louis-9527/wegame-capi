package com.wegame.capi.biz.manager.template;


import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.operation.BetRecordTemplateDo;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.service.template.GameRecordTemplateService;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.wegame.capi.biz.config.mongo.MariamakilingMongoConfig.mariamakilingTransactionManager;

@Slf4j
@Component("mariaMakilingBetRecordTemplateManager")
public class MariaMakilingBetRecordTemplateManager extends AbstractBetRecordTemplateManager<GameRecordTemplateDo> {
    @Autowired
    GameRecordTemplateService<GameRecordTemplateDo> gameRecordTemplateService;
    @Override
    @Transactional(transactionManager = mariamakilingTransactionManager, rollbackFor = Exception.class)
    public Long add(BetRecordTemplateDo<GameRecordTemplateDo> betRecordTemplate, TemplateTypeEnum templateTypeEnum) {
        try {
            AssertUtil.assertNotEmpty(betRecordTemplate.getGameRecordList(), ErrorCode.ERR_PARAM);
            betRecordTemplateService.add(betRecordTemplate, templateTypeEnum);
        } catch (DuplicateKeyException e) {
            log.warn("add Duplicate key, betRecordId: {}", betRecordTemplate.getId());
            throw e;
        }
        boolean ret = gameRecordTemplateService.batchAdd(betRecordTemplate.getGameRecordList(), templateTypeEnum, GameRecordTemplateDo.class);
        AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        return betRecordTemplate.getId();
    }
}
