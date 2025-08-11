package com.wegame.capi.biz.service;

import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.client.domain.request.generate.TemplateGenerateRequest;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 模版服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class TemplateService {

    @Autowired
    BetService betService;

    public void generate(TemplateGenerateRequest request) {
        // todo 控制并发， 并发为1 @maidong
        log.info("begain generate template");
        // todo 构建用户上下文 参考：单元测试
        // todo 其他
        for (int num = 1; num <= request.getTotal_num(); num++) {
            BetResultDo betResultDo = betService.bet(BetDo.builder()
                    .gameId(GameTypeEnum.MARIA_MAKILING.getId())
                    .gameType(GameTypeEnum.MARIA_MAKILING)
                    .betType(BetTypeEnum.NORMAL_BET)
                    .betSubType(BetSubTypeEnum.NORMAL_BET)
                    .betAmount(1000L)
                    .payAmount(1000L)
                    .betPromotionType(BetPromotionTypeEnum.NONE)
                    .build());
            // todo 根据betResultDo（落库成功 && 写文件成功） 增加num

            // num 为10 的倍数打印日志
            if (num % 10 == 0) {
                log.info("current progress: {} / {}", num, request.getTotal_num());
            }
        }
        log.info("finish generate template");
    }
}