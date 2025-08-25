package com.wegame.capi.biz.converter.user;

import com.wegame.capi.biz.domain.user.UserLoginDo;
import com.wegame.capi.client.domain.request.UserLoginRequest;

import java.util.Objects;

public class UserLoginConvert {

    public static UserLoginDo requestToDo(UserLoginRequest request) {
        if (Objects.isNull(request))
            return null;
        return UserLoginDo.builder()
                .pId(request.getPId())
                .channelId(request.getChannelId())
                .gameId(request.getGameId())
                .playerId(request.getPlayerId())
                .playerName(request.getPlayerName())
                .nickName(request.getNickName())
                .userLevel(request.getUserLevel())
                .userName(request.getUserName())
                .token(request.getToken())
                .ut(request.getUt())
                .build();
    }
}
