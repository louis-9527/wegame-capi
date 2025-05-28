package com.wegame.capi.dao.mapper.user;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.wegame.capi.dao.mapper.domain.user.UserLoginInfoPo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * user_login_info：用户登录信息
 * 方法顺序：增删改查
 * @author generate
 */
@DS("wegame_user_db")
@Mapper
public interface UserLoginInfoManualMapper {
    @Insert({"<script>",
            "INSERT INTO `user_login_info` (",
            "   `channel_id`, ",
            "   `game_id`, ",
            "   `user_id`, ",
            "   `player_id`, ",
            "   `register_time`, ",
            "   `login_time`, ",
            "   `creator`, ",
            "   `updater`, ",
            "   `create_time`, ",
            "   `update_time` ",
            ")",
            "VALUES (",
            "   #{po.channelId}, ",
            "   #{po.gameId}, ",
            "   #{po.userId}, ",
            "   #{po.playerId}, ",
            "   #{po.registerTime}, ",
            "   #{po.loginTime}, ",
            "   #{po.creator}, ",
            "   #{po.updater}, ",
            "   current_timestamp(), ",
            "   current_timestamp()",
            ") ",
            "ON DUPLICATE KEY UPDATE ",
            "   login_time = VALUES(login_time), ",
            "   updater = VALUES(updater), ",
            "   update_time = CURRENT_TIMESTAMP()",
            "</script>"})
    @Options(useGeneratedKeys = true, keyProperty = "po.id")
    int insertOrUpdateUser(@Param("po") UserLoginInfoPo po);
}

