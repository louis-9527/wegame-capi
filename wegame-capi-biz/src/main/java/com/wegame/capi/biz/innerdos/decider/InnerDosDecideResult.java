package com.wegame.capi.biz.innerdos.decider;

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
}
