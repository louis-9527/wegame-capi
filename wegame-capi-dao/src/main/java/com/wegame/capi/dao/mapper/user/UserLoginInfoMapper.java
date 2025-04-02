package com.wegame.capi.dao.mapper.user;

import com.wegame.capi.dao.mapper.domain.user.UserLoginInfoPo;
import com.wegame.capi.dao.mapper.domain.user.UserLoginInfoQuery;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * user_login_info：用户登录信息
 * 方法顺序：增删改查
 * @author generate
 */
@DS("wegame_user_db")
@Mapper
public interface UserLoginInfoMapper {
    /** 增 */
    @Insert({"insert into `user_login_info`(" +
            " `channel_id`, " +
            " `game_id`, " +
            " `user_id`, " +
            " `player_id`, " +
            " `register_time`, " +
            " `login_time`, " +
            " `creator`, " +
            " `updater`, " +
            " `create_time`, " +
            " `update_time` " +
            ") values (" +
            " #{channelId}, " +
            " #{gameId}, " +
            " #{userId}, " +
            " #{playerId}, " +
            " #{registerTime}, " +
            " #{loginTime}, " +
            " #{creator}, " +
            " #{updater}, " +
            " current_timestamp(), " +
            " current_timestamp()" +
            ")"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(UserLoginInfoPo userLoginInfo);

    @Insert({"<script>",
        " insert into `user_login_info` (",
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
        ") values ",
        " <foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\" nullable=\"true\"> ",
        " (",
        "   #{item.channelId}, ",
        "   #{item.gameId}, ",
        "   #{item.userId}, ",
        "   #{item.playerId}, ",
        "   #{item.registerTime}, ",
        "   #{item.loginTime}, ",
        "   #{item.creator}, ",
        "   #{item.updater}, ",
        "   current_timestamp(), ",
        "   current_timestamp()",
        " )",
        " </foreach> ",
        "</script>"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int batchInsert(@Param("list") List<UserLoginInfoPo> list);

    /** 删 */
    @Delete("delete from `user_login_info` where `id` = #{id} limit 1")
    int deleteById(@Param("id") Long id);

    @Delete({"<script>",
        " delete from `user_login_info` where `id` in ",
        " <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" nullable=\"true\"> ",
        "   #{item} ",
        " </foreach> ",
        "</script>"})
    int deleteByIds(@Param("ids") List<Long> ids);

    /** 改 */
    @Update({"<script>",
        " update `user_login_info` ",
        " <trim prefix=\"set\" suffixOverrides=\",\"> ",
        "     <if test=\"channelId != null\">`channel_id` = #{channelId},</if> ",
        "     <if test=\"gameId != null\">`game_id` = #{gameId},</if> ",
        "     <if test=\"userId != null\">`user_id` = #{userId},</if> ",
        "     <if test=\"playerId != null\">`player_id` = #{playerId},</if> ",
        "     <if test=\"registerTime != null\">`register_time` = #{registerTime},</if> ",
        "     <if test=\"loginTime != null\">`login_time` = #{loginTime},</if> ",
        "     <if test=\"creator != null\">`creator` = #{creator},</if> ",
        "     <if test=\"updater != null\">`updater` = #{updater},</if> ",
        "     `update_time` = current_timestamp() ",
        " </trim> ",
        " where id = #{id} limit 1",
        "</script>"})
    int updateById(UserLoginInfoPo userLoginInfo);

    /** 查 */
    @Select("select * from `user_login_info` where `id` = #{id} limit 1")
    UserLoginInfoPo selectById(@Param("id") Long id);

    @Select({"<script>",
            " select * from `user_login_info` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.channelId != null\">and `channel_id` = #{query.channelId}</if> ",
            "     <if test=\"query.gameId != null and query.gameId != ''\">and game_id like CONCAT('%', #{query.gameId}, '%')</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.registerTime != null\">and `register_time` = #{query.registerTime}</if> ",
            "     <if test=\"query.loginTime != null\">and `login_time` = #{query.loginTime}</if> ",
            "     <if test=\"query.creator != null and query.creator != ''\">and creator like CONCAT('%', #{query.creator}, '%')</if> ",
            "     <if test=\"query.updater != null and query.updater != ''\">and updater like CONCAT('%', #{query.updater}, '%')</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "     <if test=\"query.orderByClause != null and query.orderByClause != ''\"> ${query.orderByClause} </if> ",
            "     limit 1 ",
            "</script>"})
    UserLoginInfoPo selectOne(@Param("query") UserLoginInfoQuery query);

    @Select({"<script>",
            " select id from `user_login_info` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.channelId != null\">and `channel_id` = #{query.channelId}</if> ",
            "     <if test=\"query.gameId != null and query.gameId != ''\">and game_id like CONCAT('%', #{query.gameId}, '%')</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.registerTime != null\">and `register_time` = #{query.registerTime}</if> ",
            "     <if test=\"query.loginTime != null\">and `login_time` = #{query.loginTime}</if> ",
            "     <if test=\"query.creator != null and query.creator != ''\">and creator like CONCAT('%', #{query.creator}, '%')</if> ",
            "     <if test=\"query.updater != null and query.updater != ''\">and updater like CONCAT('%', #{query.updater}, '%')</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "     <if test=\"query.orderByClause != null and query.orderByClause != ''\"> ${query.orderByClause} </if> ",
            "     limit 1 ",
            "</script>"})
    Long selectId(@Param("query") UserLoginInfoQuery query);

    @Select({"<script>",
            " select * from `user_login_info` where `id` in ",
            "   <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" nullable=\"true\"> ",
            "     #{item} ",
            "   </foreach> ",
            "</script>"})
    List<UserLoginInfoPo> listByIds(@Param("ids") List<Long> ids);

    @Select("select * from `user_login_info` order by `create_time` desc")
    List<UserLoginInfoPo> listAll();

    @Select({"<script>",
            " select count(1) from `user_login_info` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.channelId != null\">and `channel_id` = #{query.channelId}</if> ",
            "     <if test=\"query.gameId != null and query.gameId != ''\">and game_id like CONCAT('%', #{query.gameId}, '%')</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.registerTime != null\">and `register_time` = #{query.registerTime}</if> ",
            "     <if test=\"query.loginTime != null\">and `login_time` = #{query.loginTime}</if> ",
            "     <if test=\"query.creator != null and query.creator != ''\">and creator like CONCAT('%', #{query.creator}, '%')</if> ",
            "     <if test=\"query.updater != null and query.updater != ''\">and updater like CONCAT('%', #{query.updater}, '%')</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "</script>"})
    long count(@Param("query") UserLoginInfoQuery query);

    @Select({"<script>",
            " select * from `user_login_info` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.channelId != null\">and `channel_id` = #{query.channelId}</if> ",
            "     <if test=\"query.gameId != null and query.gameId != ''\">and game_id like CONCAT('%', #{query.gameId}, '%')</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.registerTime != null\">and `register_time` = #{query.registerTime}</if> ",
            "     <if test=\"query.loginTime != null\">and `login_time` = #{query.loginTime}</if> ",
            "     <if test=\"query.creator != null and query.creator != ''\">and creator like CONCAT('%', #{query.creator}, '%')</if> ",
            "     <if test=\"query.updater != null and query.updater != ''\">and updater like CONCAT('%', #{query.updater}, '%')</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "     <if test=\"query.orderByClause != null and query.orderByClause != ''\"> ${query.orderByClause} </if> ",
            "     <if test=\"query.limit != null\"> limit #{query.limit}  </if> ",
            "     <if test=\"query.offset != null\"> offset #{query.offset}  </if> ",
            "</script>"})
    List<UserLoginInfoPo> list(@Param("query") UserLoginInfoQuery query);


    @Select({"<script>",
            " select id from `user_login_info` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.channelId != null\">and `channel_id` = #{query.channelId}</if> ",
            "     <if test=\"query.gameId != null and query.gameId != ''\">and game_id like CONCAT('%', #{query.gameId}, '%')</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.registerTime != null\">and `register_time` = #{query.registerTime}</if> ",
            "     <if test=\"query.loginTime != null\">and `login_time` = #{query.loginTime}</if> ",
            "     <if test=\"query.creator != null and query.creator != ''\">and creator like CONCAT('%', #{query.creator}, '%')</if> ",
            "     <if test=\"query.updater != null and query.updater != ''\">and updater like CONCAT('%', #{query.updater}, '%')</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "     <if test=\"query.orderByClause != null and query.orderByClause != ''\"> ${query.orderByClause} </if> ",
            "     <if test=\"query.limit != null\"> limit #{query.limit}  </if> ",
            "     <if test=\"query.offset != null\"> offset #{query.offset}  </if> ",
            "</script>"})
    List<Long> listId(@Param("query") UserLoginInfoQuery query);
}

