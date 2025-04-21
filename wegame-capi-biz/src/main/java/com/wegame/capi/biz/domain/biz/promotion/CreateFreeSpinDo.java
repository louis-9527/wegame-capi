package com.wegame.capi.biz.domain.biz.promotion;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CreateFreeSpinDo implements Serializable {
    private static final long serialVersionUID = 2893019421534730839L;
    /** 状态代码 */
    private Integer result;
    /** 活动编号 */
    private String eventId;
    /** 提示信息 */
    private String message;
}
