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
    @Select("select id from `user` where `player_id` = #{playerId} limit 1")
    Long selectIdByPlayerId(@Param("playerId") String playerId);

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
}

