package com.wegame.capi.biz.builder.req;

public class Builder {

    public static String buildRefId(String traceId, String gameId) {
        return traceId + gameId;
    }
}
