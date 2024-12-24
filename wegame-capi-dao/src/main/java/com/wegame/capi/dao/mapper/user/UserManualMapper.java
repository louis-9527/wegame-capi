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
            "     <if test=\"name != null\">`name` = #{name},</if> ",
            "     <if test=\"nickName != null\">`nick_name` = #{nickName},</if> ",
            "     <if test=\"realName != null\">`real_name` = #{realName},</if> ",
            "     <if test=\"phoneNumber != null\">`phone_number` = #{phoneNumber},</if> ",
            "     <if test=\"gender != null\">`gender` = #{gender},</if> ",
            "     <if test=\"playerAvatar != null\">`player_avatar` = #{playerAvatar},</if> ",
            "     <if test=\"password != null\">`password` = #{password},</if> ",
            "     <if test=\"email != null\">`email` = #{email},</if> ",
            "     <if test=\"nationality != null\">`nationality` = #{nationality},</if> ",
            "     <if test=\"province != null\">`province` = #{province},</if> ",
            "     <if test=\"city != null\">`city` = #{city},</if> ",
            "     <if test=\"address != null\">`address` = #{address},</if> ",
            "     <if test=\"platformId != null\">`platform_id` = #{platformId},</if> ",
            "     <if test=\"registrationTime != null\">`registration_time` = #{registrationTime},</if> ",
            "     <if test=\"status != null\">`status` = #{status},</if> ",
            "     <if test=\"extend != null\">`extend` = #{extend},</if> ",
            "     <if test=\"creator != null\">`creator` = #{creator},</if> ",
            "     <if test=\"updater != null\">`updater` = #{updater},</if> ",
            "     `update_time` = current_timestamp() ",
            " </trim> ",
            " where `player_id` = #{playerId} limit 1",
            "</script>"})
    int updateByPlayerId(UserPo po);
}

