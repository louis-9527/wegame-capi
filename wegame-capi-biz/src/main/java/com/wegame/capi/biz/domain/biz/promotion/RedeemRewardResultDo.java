package com.wegame.capi.biz.domain.biz.promotion;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RedeemRewardResultDo implements Serializable {
    /**
     * status
     */
    private Integer status;
    /**
     * sucess
     */
    private Boolean success;
    /**
     * message
     */
    private String message;

    //    public Boolean invalidPayOutAmount(){
    //        return status == 5017;
    //    }
}
