package com.wegame.capi.biz.domain.biz.bet;

import com.wegame.capi.api.domain.res.singlewallet.GetPoolConfigRes;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BetBloodPoolControlDo implements Serializable {
    private static final long serialVersionUID = -226630893710848L;
    /**
     * 是否走血池控制（控制单钱包）， 默认false
     */
    private boolean enableFundPool = false;
    /**
     * 匹配的血池控制配置
     */
    private GetPoolConfigRes.BloodPoolConfig matchBloodPoolConfig;
}

