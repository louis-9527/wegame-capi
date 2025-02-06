package com.wegame.capi.common.deserializer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public interface EnumDeserializerInterface<Enum> {
    @JsonValue
    String jsonValue();

    @JsonCreator
    Enum jsonCreator(String jsonValue);
}
