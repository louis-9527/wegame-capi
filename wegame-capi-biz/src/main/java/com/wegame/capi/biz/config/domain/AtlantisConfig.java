package com.wegame.capi.biz.config.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AtlantisConfig implements Serializable {
    private static final long serialVersionUID = 55506678430050L;

    // todo

    public void validate() {
    }


}
