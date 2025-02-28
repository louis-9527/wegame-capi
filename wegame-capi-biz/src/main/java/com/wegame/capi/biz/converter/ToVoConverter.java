package com.wegame.capi.biz.converter;

public interface ToVoConverter<T, R> {

    T toResultVo();

    R toRecordVo();

     boolean hasRewardFreeSpins();
    }

