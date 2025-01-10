package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.UserLoginDo;
import com.wegame.capi.biz.domain.user.UserDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.context.UserInfo;

import java.util.Date;
import java.util.Optional;

public class UserInfoBuilder {

    private UserInfoBuilder() {
    }

    public static UserInfo buildUserInfo(UserLoginDo userLoginDo, Long userId, String token) {
        return UserInfo.builder()
                .id(String.valueOf(userId))
                .pId(userLoginDo.getPId())
                .channelId(userLoginDo.getChannelId())
                .gameId(userLoginDo.getGameId())
                .playerId(userLoginDo.getPlayerId())
                .playerName(userLoginDo.getPlayerName())
                .nickName(userLoginDo.getNickName())
                .userLevel(userLoginDo.getUserLevel())
                .userName(userLoginDo.getUserName())
                .token(token)
                .build();
    }

    public static UserDo buildUserDo(UserLoginDo userLoginDo) {
        return UserDo.builder()
                .id(null)
                .pId(userLoginDo.getPId())
                .playerId(userLoginDo.getPlayerId())
                .playerName(userLoginDo.getPlayerName())
                .nickName(Optional.ofNullable(userLoginDo.getNickName()).orElse(""))
                .userLevel(Optional.ofNullable(userLoginDo.getUserLevel()).orElse(""))
                .userName(Optional.ofNullable(userLoginDo.getUserName()).orElse(""))
                .presentAmount(0L)
                .extend("")
                .creator(Constants.SYSTEM)
                .updater(Constants.SYSTEM)
                .updateTime(new Date())
                .build();
    }

}
