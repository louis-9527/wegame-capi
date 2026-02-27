package com.wegame.capi.api.domain.res.promotion;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GetRedirectGameUrlRes implements Serializable {

    private static final long serialVersionUID = -8967662891053166172L;

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
