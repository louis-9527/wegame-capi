package com.wegame.capi.biz.domain.biz.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RedeemRewardResultDo {
    private Integer status;
    private Boolean success;
    private String message;


    public Boolean invalidPayOutAmount(){
        return status == 5017;
    }
}
