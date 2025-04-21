package com.wegame.capi.api.domain.res.singlewallet;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SingleWalletBaseRes implements Serializable {
    private static final long serialVersionUID = -851901361660936626L;
    /**
     * refId
     */
    private String refId;
}
