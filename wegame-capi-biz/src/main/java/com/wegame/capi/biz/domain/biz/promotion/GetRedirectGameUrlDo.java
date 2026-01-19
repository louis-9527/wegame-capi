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
public class GetRedirectGameUrlDo implements Serializable {

    private static final long serialVersionUID = -8967662892053165172L;
    /**
     * url
     */
    private String url;
    /**
     * playerName
     */
    private String playerName;
    /**
     * refId
     */
    private String refId;
}
