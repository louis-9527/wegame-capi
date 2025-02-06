package com.wegame.capi.common.deserializer;

import com.fasterxml.jackson.annotation.JsonValue;

public interface EnumDeserializerInterface<Enum> {
    @JsonValue
    String jsonValue();

    Enum jsonCreator(String jsonValue);
}
