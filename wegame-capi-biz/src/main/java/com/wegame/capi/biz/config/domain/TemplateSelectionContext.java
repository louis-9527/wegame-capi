package com.wegame.capi.biz.config.domain;

import com.wegame.capi.client.enums.AbTypeEnum;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模板选择上下文
 * <p>持有本次投注的所有可能被策略链用到的条件，各策略按需取用</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateSelectionContext {

    /** 干预类型（NONE / FREE_SPIN / STAR_CARD 等） */
    private BetPromotionTypeEnum promotionType;

    /** 用户 AB 分组（A / B） */
    private AbTypeEnum abType;
}
