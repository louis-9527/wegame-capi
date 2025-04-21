package com.wegame.capi.api.domain.res.promotion;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CreateFreeSpinRes implements Serializable {
    private static final long serialVersionUID = -1446061719242474813L;
    /** 状态代码 */
    private Integer result;
    /** 活动编号 */
    private String eventId;
}
