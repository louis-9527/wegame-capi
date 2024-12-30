package com.wegame.capi.dao.mapper.user;

import com.wegame.capi.dao.mapper.domain.user.UserPresentRecordPo;
import com.wegame.capi.dao.mapper.domain.user.UserPresentRecordQuery;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * user_present_record：用户赠送记录
 * 方法顺序：增删改查
 * @author generate
 */
@DS("wegame_user_db")
@Mapper
public interface UserPresentRecordMapper {
    /** 增 */
    @Insert({"insert into `user_present_record`(" +
            " `user_id`, " +
            " `player_id`, " +
            " `type`, " +
            " `sub_type`, " +
            " `biz_Id`, " +
            " `amount`, " +
            " `status`, " +
            " `creator`, " +
            " `updater`, " +
            " `create_time`, " +
            " `update_time` " +
            ") values (" +
            " #{userId}, " +
            " #{playerId}, " +
            " #{type}, " +
            " #{subType}, " +
            " #{bizId}, " +
            " #{amount}, " +
            " #{status}, " +
            " #{creator}, " +
            " #{updater}, " +
            " current_timestamp(), " +
            " current_timestamp()" +
            ")"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(UserPresentRecordPo userPresentRecord);

    @Insert({"<script>",
        " insert into `user_present_record` (",
        "   `user_id`, ",
        "   `player_id`, ",
        "   `type`, ",
        "   `sub_type`, ",
        "   `biz_Id`, ",
        "   `amount`, ",
        "   `status`, ",
        "   `creator`, ",
        "   `updater`, ",
        "   `create_time`, ",
        "   `update_time` ",
        ") values ",
        " <foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\" nullable=\"true\"> ",
        " (",
        "   #{item.userId}, ",
        "   #{item.playerId}, ",
        "   #{item.type}, ",
        "   #{item.subType}, ",
        "   #{item.bizId}, ",
        "   #{item.amount}, ",
        "   #{item.status}, ",
        "   #{item.creator}, ",
        "   #{item.updater}, ",
        "   current_timestamp(), ",
        "   current_timestamp()",
        " )",
        " </foreach> ",
        "</script>"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int batchInsert(@Param("list") List<UserPresentRecordPo> list);

    /** 删 */
    @Delete("delete from `user_present_record` where `id` = #{id} limit 1")
    int deleteById(@Param("id") Long id);

    @Delete({"<script>",
        " delete from `user_present_record` where `id` in ",
        " <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" nullable=\"true\"> ",
        "   #{item} ",
        " </foreach> ",
        "</script>"})
    int deleteByIds(@Param("ids") List<Long> ids);

    /** 改 */
    @Update({"<script>",
        " update `user_present_record` ",
        " <trim prefix=\"set\" suffixOverrides=\",\"> ",
        "     <if test=\"userId != null\">`user_id` = #{userId},</if> ",
        "     <if test=\"playerId != null\">`player_id` = #{playerId},</if> ",
        "     <if test=\"type != null\">`type` = #{type},</if> ",
        "     <if test=\"subType != null\">`sub_type` = #{subType},</if> ",
        "     <if test=\"bizId != null\">`biz_Id` = #{bizId},</if> ",
        "     <if test=\"amount != null\">`amount` = #{amount},</if> ",
        "     <if test=\"status != null\">`status` = #{status},</if> ",
        "     <if test=\"creator != null\">`creator` = #{creator},</if> ",
        "     <if test=\"updater != null\">`updater` = #{updater},</if> ",
        "     `update_time` = current_timestamp() ",
        " </trim> ",
        " where id = #{id} limit 1",
        "</script>"})
    int updateById(UserPresentRecordPo userPresentRecord);

    /** 查 */
    @Select("select * from `user_present_record` where `id` = #{id} limit 1")
    UserPresentRecordPo selectById(@Param("id") Long id);

    @Select({"<script>",
            " select * from `user_present_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.type != null\">and `type` = #{query.type}</if> ",
            "     <if test=\"query.subType != null\">and `sub_type` = #{query.subType}</if> ",
            "     <if test=\"query.bizId != null and query.bizId != ''\">and biz_Id like CONCAT('%', #{query.bizId}, '%')</if> ",
            "     <if test=\"query.amount != null\">and `amount` = #{query.amount}</if> ",
            "     <if test=\"query.status != null\">and `status` = #{query.status}</if> ",
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
    UserPresentRecordPo selectOne(@Param("query") UserPresentRecordQuery query);

    @Select({"<script>",
            " select id from `user_present_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.type != null\">and `type` = #{query.type}</if> ",
            "     <if test=\"query.subType != null\">and `sub_type` = #{query.subType}</if> ",
            "     <if test=\"query.bizId != null and query.bizId != ''\">and biz_Id like CONCAT('%', #{query.bizId}, '%')</if> ",
            "     <if test=\"query.amount != null\">and `amount` = #{query.amount}</if> ",
            "     <if test=\"query.status != null\">and `status` = #{query.status}</if> ",
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
    Long selectId(@Param("query") UserPresentRecordQuery query);

    @Select({"<script>",
            " select * from `user_present_record` where `id` in ",
            "   <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" nullable=\"true\"> ",
            "     #{item} ",
            "   </foreach> ",
            "</script>"})
    List<UserPresentRecordPo> listByIds(@Param("ids") List<Long> ids);

    @Select("select * from `user_present_record` order by `create_time` desc")
    List<UserPresentRecordPo> listAll();

    @Select({"<script>",
            " select count(1) from `user_present_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.type != null\">and `type` = #{query.type}</if> ",
            "     <if test=\"query.subType != null\">and `sub_type` = #{query.subType}</if> ",
            "     <if test=\"query.bizId != null and query.bizId != ''\">and biz_Id like CONCAT('%', #{query.bizId}, '%')</if> ",
            "     <if test=\"query.amount != null\">and `amount` = #{query.amount}</if> ",
            "     <if test=\"query.status != null\">and `status` = #{query.status}</if> ",
            "     <if test=\"query.creator != null and query.creator != ''\">and creator like CONCAT('%', #{query.creator}, '%')</if> ",
            "     <if test=\"query.updater != null and query.updater != ''\">and updater like CONCAT('%', #{query.updater}, '%')</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "</script>"})
    long count(@Param("query") UserPresentRecordQuery query);

    @Select({"<script>",
            " select * from `user_present_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.type != null\">and `type` = #{query.type}</if> ",
            "     <if test=\"query.subType != null\">and `sub_type` = #{query.subType}</if> ",
            "     <if test=\"query.bizId != null and query.bizId != ''\">and biz_Id like CONCAT('%', #{query.bizId}, '%')</if> ",
            "     <if test=\"query.amount != null\">and `amount` = #{query.amount}</if> ",
            "     <if test=\"query.status != null\">and `status` = #{query.status}</if> ",
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
    List<UserPresentRecordPo> list(@Param("query") UserPresentRecordQuery query);


    @Select({"<script>",
            " select id from `user_present_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.type != null\">and `type` = #{query.type}</if> ",
            "     <if test=\"query.subType != null\">and `sub_type` = #{query.subType}</if> ",
            "     <if test=\"query.bizId != null and query.bizId != ''\">and biz_Id like CONCAT('%', #{query.bizId}, '%')</if> ",
            "     <if test=\"query.amount != null\">and `amount` = #{query.amount}</if> ",
            "     <if test=\"query.status != null\">and `status` = #{query.status}</if> ",
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
    List<Long> listId(@Param("query") UserPresentRecordQuery query);
}

