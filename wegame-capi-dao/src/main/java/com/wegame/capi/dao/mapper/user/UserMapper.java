package com.wegame.capi.dao.mapper.user;

import com.wegame.capi.dao.mapper.domain.user.UserPo;
import com.wegame.capi.dao.mapper.domain.user.UserQuery;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * user：用户
 * 方法顺序：增删改查
 * @author generate
 */
@DS("wegame_user_db")
@Mapper
public interface UserMapper {
    /** 增 */
    @Insert({"insert into `user`(" +
            " `platform_id`, " +
            " `player_id`, " +
            " `player_name`, " +
            " `nick_name`, " +
            " `user_level`, " +
            " `user_name`, " +
            " `present_amount`, " +
            " `extend`, " +
            " `creator`, " +
            " `updater`, " +
            " `create_time`, " +
            " `update_time` " +
            ") values (" +
            " #{platformId}, " +
            " #{playerId}, " +
            " #{playerName}, " +
            " #{nickName}, " +
            " #{userLevel}, " +
            " #{userName}, " +
            " #{presentAmount}, " +
            " #{extend}, " +
            " #{creator}, " +
            " #{updater}, " +
            " current_timestamp(), " +
            " current_timestamp()" +
            ")"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(UserPo user);

    @Insert({"<script>",
        " insert into `user` (",
        "   `platform_id`, ",
        "   `player_id`, ",
        "   `player_name`, ",
        "   `nick_name`, ",
        "   `user_level`, ",
        "   `user_name`, ",
        "   `present_amount`, ",
        "   `extend`, ",
        "   `creator`, ",
        "   `updater`, ",
        "   `create_time`, ",
        "   `update_time` ",
        ") values ",
        " <foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\" nullable=\"true\"> ",
        " (",
        "   #{item.platformId}, ",
        "   #{item.playerId}, ",
        "   #{item.playerName}, ",
        "   #{item.nickName}, ",
        "   #{item.userLevel}, ",
        "   #{item.userName}, ",
        "   #{item.presentAmount}, ",
        "   #{item.extend}, ",
        "   #{item.creator}, ",
        "   #{item.updater}, ",
        "   current_timestamp(), ",
        "   current_timestamp()",
        " )",
        " </foreach> ",
        "</script>"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int batchInsert(@Param("list") List<UserPo> list);

    /** 删 */
    @Delete("delete from `user` where `id` = #{id} limit 1")
    int deleteById(@Param("id") Long id);

    @Delete({"<script>",
        " delete from `user` where `id` in ",
        " <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" nullable=\"true\"> ",
        "   #{item} ",
        " </foreach> ",
        "</script>"})
    int deleteByIds(@Param("ids") List<Long> ids);

    /** 改 */
    @Update({"<script>",
        " update `user` ",
        " <trim prefix=\"set\" suffixOverrides=\",\"> ",
        "     <if test=\"platformId != null\">`platform_id` = #{platformId},</if> ",
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
        " where id = #{id} limit 1",
        "</script>"})
    int updateById(UserPo user);

    /** 查 */
    @Select("select * from `user` where `id` = #{id} limit 1")
    UserPo selectById(@Param("id") Long id);

    @Select({"<script>",
            " select * from `user` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.platformId != null and query.platformId != ''\">and platform_id like CONCAT('%', #{query.platformId}, '%')</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.playerName != null and query.playerName != ''\">and player_name like CONCAT('%', #{query.playerName}, '%')</if> ",
            "     <if test=\"query.nickName != null and query.nickName != ''\">and nick_name like CONCAT('%', #{query.nickName}, '%')</if> ",
            "     <if test=\"query.userLevel != null and query.userLevel != ''\">and user_level like CONCAT('%', #{query.userLevel}, '%')</if> ",
            "     <if test=\"query.userName != null and query.userName != ''\">and user_name like CONCAT('%', #{query.userName}, '%')</if> ",
            "     <if test=\"query.presentAmount != null\">and `present_amount` = #{query.presentAmount}</if> ",
            "     <if test=\"query.extend != null and query.extend != ''\">and extend like CONCAT('%', #{query.extend}, '%')</if> ",
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
    UserPo selectOne(@Param("query") UserQuery query);

    @Select({"<script>",
            " select id from `user` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.platformId != null and query.platformId != ''\">and platform_id like CONCAT('%', #{query.platformId}, '%')</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.playerName != null and query.playerName != ''\">and player_name like CONCAT('%', #{query.playerName}, '%')</if> ",
            "     <if test=\"query.nickName != null and query.nickName != ''\">and nick_name like CONCAT('%', #{query.nickName}, '%')</if> ",
            "     <if test=\"query.userLevel != null and query.userLevel != ''\">and user_level like CONCAT('%', #{query.userLevel}, '%')</if> ",
            "     <if test=\"query.userName != null and query.userName != ''\">and user_name like CONCAT('%', #{query.userName}, '%')</if> ",
            "     <if test=\"query.presentAmount != null\">and `present_amount` = #{query.presentAmount}</if> ",
            "     <if test=\"query.extend != null and query.extend != ''\">and extend like CONCAT('%', #{query.extend}, '%')</if> ",
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
    Long selectId(@Param("query") UserQuery query);

    @Select({"<script>",
            " select * from `user` where `id` in ",
            "   <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" nullable=\"true\"> ",
            "     #{item} ",
            "   </foreach> ",
            "</script>"})
    List<UserPo> listByIds(@Param("ids") List<Long> ids);

    @Select("select * from `user` order by `create_time` desc")
    List<UserPo> listAll();

    @Select({"<script>",
            " select count(1) from `user` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.platformId != null and query.platformId != ''\">and platform_id like CONCAT('%', #{query.platformId}, '%')</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.playerName != null and query.playerName != ''\">and player_name like CONCAT('%', #{query.playerName}, '%')</if> ",
            "     <if test=\"query.nickName != null and query.nickName != ''\">and nick_name like CONCAT('%', #{query.nickName}, '%')</if> ",
            "     <if test=\"query.userLevel != null and query.userLevel != ''\">and user_level like CONCAT('%', #{query.userLevel}, '%')</if> ",
            "     <if test=\"query.userName != null and query.userName != ''\">and user_name like CONCAT('%', #{query.userName}, '%')</if> ",
            "     <if test=\"query.presentAmount != null\">and `present_amount` = #{query.presentAmount}</if> ",
            "     <if test=\"query.extend != null and query.extend != ''\">and extend like CONCAT('%', #{query.extend}, '%')</if> ",
            "     <if test=\"query.creator != null and query.creator != ''\">and creator like CONCAT('%', #{query.creator}, '%')</if> ",
            "     <if test=\"query.updater != null and query.updater != ''\">and updater like CONCAT('%', #{query.updater}, '%')</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "</script>"})
    long count(@Param("query") UserQuery query);

    @Select({"<script>",
            " select * from `user` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.platformId != null and query.platformId != ''\">and platform_id like CONCAT('%', #{query.platformId}, '%')</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.playerName != null and query.playerName != ''\">and player_name like CONCAT('%', #{query.playerName}, '%')</if> ",
            "     <if test=\"query.nickName != null and query.nickName != ''\">and nick_name like CONCAT('%', #{query.nickName}, '%')</if> ",
            "     <if test=\"query.userLevel != null and query.userLevel != ''\">and user_level like CONCAT('%', #{query.userLevel}, '%')</if> ",
            "     <if test=\"query.userName != null and query.userName != ''\">and user_name like CONCAT('%', #{query.userName}, '%')</if> ",
            "     <if test=\"query.presentAmount != null\">and `present_amount` = #{query.presentAmount}</if> ",
            "     <if test=\"query.extend != null and query.extend != ''\">and extend like CONCAT('%', #{query.extend}, '%')</if> ",
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
    List<UserPo> list(@Param("query") UserQuery query);


    @Select({"<script>",
            " select id from `user` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.platformId != null and query.platformId != ''\">and platform_id like CONCAT('%', #{query.platformId}, '%')</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.playerName != null and query.playerName != ''\">and player_name like CONCAT('%', #{query.playerName}, '%')</if> ",
            "     <if test=\"query.nickName != null and query.nickName != ''\">and nick_name like CONCAT('%', #{query.nickName}, '%')</if> ",
            "     <if test=\"query.userLevel != null and query.userLevel != ''\">and user_level like CONCAT('%', #{query.userLevel}, '%')</if> ",
            "     <if test=\"query.userName != null and query.userName != ''\">and user_name like CONCAT('%', #{query.userName}, '%')</if> ",
            "     <if test=\"query.presentAmount != null\">and `present_amount` = #{query.presentAmount}</if> ",
            "     <if test=\"query.extend != null and query.extend != ''\">and extend like CONCAT('%', #{query.extend}, '%')</if> ",
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
    List<Long> listId(@Param("query") UserQuery query);
}

