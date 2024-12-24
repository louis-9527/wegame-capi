package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.user.UserDo;
import com.wegame.capi.client.domain.request.UserLoginRequest;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.context.UserInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Optional;

public class UserInfoBuilder {

    private UserInfoBuilder() {
    }

    public static UserInfo buildUserInfo(UserLoginRequest request, Long userId, String token) {
        return UserInfo.builder()
                .id(String.valueOf(userId))
                .playerId(request.getPlayerId())
                .playerId(request.getPlayerId().replace(Constants.PLAYER_ID_PREFIX, ""))
                .name(request.getName())
                .nickName(request.getNickName())
                .realName(request.getRealName())
                .phoneNumber(request.getPhoneNumber())
                .gender(Optional.ofNullable(request.getGender()).orElse(0))
                .playerAvatar(StringUtils.isAnyBlank(request.getFtpDomain(), request.getPlayerAvatar()) ? "" : request.getFtpDomain() + request.getPlayerAvatar())
                .password(request.getPassword())
                .email(request.getEmail())
                .nationality(request.getNationality())
                .province(request.getProvince())
                .city(request.getCity())
                .address(request.getAddress())
                .platformId(request.getPlatformId())
                .registrationTime(request.getRegistrationTime())
                .token(token)
                .build();
    }

    public static UserDo buildUserDo(UserLoginRequest request) {
        return UserDo.builder()
                .id(null)
                .playerId(request.getPlayerId())
                .name(Optional.ofNullable(request.getName()).orElse(""))
                .nickName(Optional.ofNullable(request.getNickName()).orElse(""))
                .realName(Optional.ofNullable(request.getRealName()).orElse(""))
                .phoneNumber(request.getPhoneNumber())
                .gender(Optional.ofNullable(request.getGender()).orElse(0))
                .playerAvatar(StringUtils.isAnyBlank(request.getFtpDomain(), request.getPlayerAvatar()) ? "" : request.getFtpDomain() + request.getPlayerAvatar())
                .password(Optional.ofNullable(request.getPassword()).orElse(""))
                .email(Optional.ofNullable(request.getEmail()).orElse(""))
                .nationality(Optional.ofNullable(request.getNationality()).orElse(""))
                .province(Optional.ofNullable(request.getProvince()).orElse(""))
                .city(Optional.ofNullable(request.getCity()).orElse(""))
                .address(Optional.ofNullable(request.getAddress()).orElse(""))
                .platformId(request.getPlatformId())
                .registrationTime(Optional.ofNullable(request.getRegistrationTime()).map(Date::new).orElse(new Date()))
                .status(1)
                .extend("")
                .creator(Constants.SYSTEM)
                .updater(Constants.SYSTEM)
                .updateTime(new Date())
                .build();

    }

}
