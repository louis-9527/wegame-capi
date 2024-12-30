package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.user.UserDo;
import com.wegame.capi.client.domain.request.UserLoginRequest;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.context.UserInfo;

import java.util.Date;
import java.util.Optional;

public class UserInfoBuilder {

    private UserInfoBuilder() {
    }

    public static UserInfo buildUserInfo(UserLoginRequest request, Long userId, String token) {
        return UserInfo.builder()
                .id(String.valueOf(userId))
                .platformId(request.getPlatformId())
                .channelId(request.getChanelId())
                .playerId(request.getPlayerId())
                .playerName(request.getPlayerName())
                .nickName(request.getNickName())
                .userLevel(request.getUserLevel())
                .userName(request.getUserName())
                .birthday(request.getBirthday())
                .token(token)
                .build();
    }

    public static UserDo buildUserDo(UserLoginRequest request) {
        return UserDo.builder()
                .id(null)
                .playerId(request.getPlayerId())
                .nickName(Optional.ofNullable(request.getNickName()).orElse(""))
                .platformId(request.getPlatformId())
                .extend("")
                .creator(Constants.SYSTEM)
                .updater(Constants.SYSTEM)
                .updateTime(new Date())
                .build();

    }

}
