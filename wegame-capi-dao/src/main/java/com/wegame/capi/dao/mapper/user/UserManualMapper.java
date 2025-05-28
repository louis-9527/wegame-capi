package com.wegame.capi.dao.mapper.user;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.wegame.capi.dao.mapper.domain.user.UserPo;
import org.apache.ibatis.annotations.*;

/**
 * user：玩家用户
 * 方法顺序：增删改查
 */
@DS("wegame_user_db")
@Mapper
public interface UserManualMapper {

    /** 查 */
    @Select("select * from `user` where `player_id` = #{playerId} limit 1")
    UserPo selectByPlayerId(@Param("playerId") String playerId);

    /** 查 */
    @Select("select id from `user` where `player_id` = #{playerId} and `p_id` = #{pid} limit 1")
    Long selectIdByPlayerIdAndPid(@Param("playerId") String playerId, @Param("pid") String pid);

    @Update({"<script>",
            " update `user` ",
            " <trim prefix=\"set\" suffixOverrides=\",\"> ",
            "     <if test=\"pId != null\">`p_id` = #{pId},</if> ",
            "     <if test=\"playerId != null\">`player_id` = #{playerId},</if> ",
            "     <if test=\"playerName != null\">`player_name` = #{playerName},</if> ",
            "     <if test=\"nickName != null\">`nick_name` = #{nickName},</if> ",
            "     <if test=\"userLevel != null\">`user_level` = #{userLevel},</if> ",
            "     <if test=\"userName != null\">`user_name` = #{userName},</if> ",
            "     <if test=\"presentAmount != null\">`present_amount` = #{presentAmount},</if> ",
            "     <if test=\"extend != null\">`extend` = #{extend},</if> ",
            "     <if test=\"creator != null\">`creator` = #{creator},</if> ",
            "     <if test=\"updater != null\">`updater` = #{updater},</if> ",
            "     `update_time` = current_timestamp() ",
            " </trim> ",
            " where `player_id` = #{playerId} limit 1",
            "</script>"})
    int updateByPlayerId(UserPo po);

    @Insert({"<script>",
            "INSERT INTO `user` (id, p_id, player_id, player_name, nick_name, user_level, user_name, present_amount, extend, creator, updater, update_time)",
            "VALUES (",
            "   #{po.id}, ",
            "   #{po.pId}, ",
            "   #{po.playerId}, ",
            "   #{po.playerName}, ",
            "   #{po.nickName}, ",
            "   #{po.userLevel}, ",
            "   #{po.userName}, ",
            "   #{po.presentAmount}, ",
            "   #{po.extend}, ",
            "   #{po.creator}, ",
            "   #{po.updater}, ",
            "   CURRENT_TIMESTAMP()",
            ") ",
            "ON DUPLICATE KEY UPDATE ",
            "   p_id = VALUES(p_id), ",
            "   player_name = VALUES(player_name), ",
            "   nick_name = VALUES(nick_name), ",
            "   user_level = VALUES(user_level), ",
            "   user_name = VALUES(user_name), ",
            "   present_amount = VALUES(present_amount), ",
            "   extend = VALUES(extend), ",
            "   updater = VALUES(updater), ",
            "   update_time = CURRENT_TIMESTAMP()",
            "</script>"})
    @Options(useGeneratedKeys = true, keyProperty = "po.id")
    int insertOrUpdateUser(@Param("po") UserPo po);
}

