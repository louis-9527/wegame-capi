package com.wegame.capi.biz.innerdos.decider;

import cn.hutool.core.util.StrUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 内部干预决策结果
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InnerDosDecideResult {
    /**
     * 模版id范围
     */
    private String templateIdRange;
    /**
     * 下一个决策器
     */
    private InnerDosDecider nextDecider;

    public boolean hasEnd() {
        return StrUtil.isNotEmpty(this.templateIdRange);
    }
}
