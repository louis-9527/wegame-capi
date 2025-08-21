package com.wegame.capi.biz.listener.handler.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.wegame.base.exception.LockException;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.domain.TemplateMultipleRange;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 模版预加载后处理执行器
 */
@Slf4j
@Component
public class MariaMakilingTemplateLoadPostHandler extends AbstractTemplateLoadPostHandler {
    @Autowired
    MariaMakilingConfig mariaMakilingConfig;

    protected TemplateTypeEnum getTemplateType() {
        return TemplateTypeEnum.MARIA_MAKILING;
    }

    protected MariaMakilingConfig.TemplateRangeConfig getTemplateRangeConfig() {
        return mariaMakilingConfig.getTemplateRangeConfig();
    }

    @Override
    public void handle() {
        if (!supportGame(GameTypeEnum.MARIA_MAKILING)) {
            return;
        }
        try {
            //            distributeLock.lockThenRun(LockNameUtil.getBetRecordTemplatePreLoadLockName(this.getTemplateType()), 60, TimeUnit.SECONDS, () -> {
            try {
                log.info("template load start...");
                MariaMakilingConfig.TemplateRangeConfig config = this.getTemplateRangeConfig();
                config.getMainGame().keySet().forEach((String key) -> loadTemplate(BetTypeEnum.NORMAL_BET, TemplateMultipleRange.build(key)));
                config.getFreeGame().keySet().forEach((String key) -> loadTemplate(BetTypeEnum.BUY_BET, TemplateMultipleRange.build(key)));
                log.info("template load end...");
            } catch (Exception e) {
                log.error("template load error", e);
            }
            //            }, "lock fail");
        } catch (LockException e) {
            log.warn("template load lock warn", e);
        } catch (Exception e) {
            log.error("template load error", e);
        }
    }

    private void loadTemplate(BetTypeEnum betType, TemplateMultipleRange multipleRange) {
        TemplateTypeEnum templateType = this.getTemplateType();
        log.info("template load start, betType: {}, range: {}-{}...", betType, multipleRange.getStart(), multipleRange.getEnd());
        try {
            // todo 增加缓存策略
            // 投注记录模版缓存 先失效，再加载
            List<Long> betRecordTemplateIdList = betRecordTemplateService.listIdByMultipleRange(templateType, betType, multipleRange.getStart(), multipleRange.getEnd());
            if (!(betType == BetTypeEnum.NORMAL_BET && multipleRange.getEnd().compareTo(BigDecimal.ONE) <= 0
                    || betType == BetTypeEnum.BUY_BET && multipleRange.getEnd().compareTo(new BigDecimal("0.5")) <= 0)) {
                log.info("template load end, betType: {}, range: {}-{}, size: {}...， no load gameRecordTemplate", betType, multipleRange.getStart(), multipleRange.getEnd(), betRecordTemplateIdList.size());
                return;
            }
            if (CollectionUtil.isNotEmpty(betRecordTemplateIdList)) {
                List<List<Long>> multiList = ListUtil.partition(betRecordTemplateIdList, 1000);
                multiList.forEach(idList -> {
                    List<GameRecordTemplateDo> templateList = gameRecordTemplateService.listByBetRecordIdList(templateType, idList);
                    idList.forEach(betRecordId -> {
                        gameRecordTemplateService.putCache(templateType, betRecordId, templateList.stream()
                                .filter(template -> Objects.equals(template.getBetRecordId(), betRecordId))
                                .collect(Collectors.toList()));
                    });
                });
                // gameRecord模版缓存 先失效，再加载
                //                betRecordTemplateIdList.forEach(betRecordTemplateId -> {
                //                    // 正常投注：0倍 缓存到本地
                //                    // 免费投注： 0-0.5倍   缓存到本地
                //                    gameRecordTemplateService.listByBetRecordId(templateType, betRecordTemplateId);
                //                });
            }
            log.info("template load end, betType: {}, range: {}-{}, size: {}...", betType, multipleRange.getStart(), multipleRange.getEnd(), betRecordTemplateIdList.size());
        } catch (Exception e) {
            log.info("template load error, betType: {}, range: {}-{}...", betType, multipleRange.getStart(), multipleRange.getEnd());
        }
    }
}
