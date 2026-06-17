package com.wegame.capi.biz.config.domain;


import com.wegame.capi.client.enums.GameTypeEnum;

public interface ConfigValidate {

    void validate();

    GameTypeEnum gameType();

}
