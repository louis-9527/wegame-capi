package com.wegame.capi.dao.mapper.user;

import com.wegame.capi.dao.mapper.domain.user.UserPo;
import com.wegame.capi.dao.mapper.domain.user.UserQuery;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * user：玩家用户
 * 方法顺序：增删改查
 * @author generate
 */
@DS("wegame_user_db")
@Mapper
public interface UserMapper {
    /** 增 */
    @Insert({"insert into `user`(" +
            " `player_id`, " +
            " `name`, " +
            " `nick_name`, " +
            " `real_name`, " +
            " `phone_number`, " +
            " `gender`, " +
            " `player_avatar`, " +
            " `password`, " +
            " `email`, " +
            " `nationality`, " +
            " `province`, " +
            " `city`, " +
            " `address`, " +
            " `platform_id`, " +
            " `registration_time`, " +
            " `status`, " +
            " `extend`, " +
            " `creator`, " +
            " `updater`, " +
            " `create_time`, " +
            " `update_time` " +
            ") values (" +
            " #{playerId}, " +
            " #{name}, " +
            " #{nickName}, " +
            " #{realName}, " +
            " #{phoneNumber}, " +
            " #{gender}, " +
            " #{playerAvatar}, " +
            " #{password}, " +
            " #{email}, " +
            " #{nationality}, " +
            " #{province}, " +
            " #{city}, " +
            " #{address}, " +
            " #{platformId}, " +
            " #{registrationTime}, " +
            " #{status}, " +
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
        "   `player_id`, ",
        "   `name`, ",
        "   `nick_name`, ",
        "   `real_name`, ",
        "   `phone_number`, ",
        "   `gender`, ",
        "   `player_avatar`, ",
        "   `password`, ",
        "   `email`, ",
        "   `nationality`, ",
        "   `province`, ",
        "   `city`, ",
        "   `address`, ",
        "   `platform_id`, ",
        "   `registration_time`, ",
        "   `status`, ",
        "   `extend`, ",
        "   `creator`, ",
        "   `updater`, ",
        "   `create_time`, ",
        "   `update_time` ",
        ") values ",
        " <foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\" nullable=\"true\"> ",
        " (",
        "   #{item.playerId}, ",
        "   #{item.name}, ",
        "   #{item.nickName}, ",
        "   #{item.realName}, ",
        "   #{item.phoneNumber}, ",
        "   #{item.gender}, ",
        "   #{item.playerAvatar}, ",
        "   #{item.password}, ",
        "   #{item.email}, ",
        "   #{item.nationality}, ",
        "   #{item.province}, ",
        "   #{item.city}, ",
        "   #{item.address}, ",
        "   #{item.platformId}, ",
        "   #{item.registrationTime}, ",
        "   #{item.status}, ",
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


    /** 改 */
    @Update({"<script>",
        " update `user` ",
        " <trim prefix=\"set\" suffixOverrides=\",\"> ",
        "     <if test=\"playerId != null\">`player_id` = #{playerId},</if> ",
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
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.name != null and query.name != ''\">and name like CONCAT('%', #{query.name}, '%')</if> ",
            "     <if test=\"query.nickName != null and query.nickName != ''\">and nick_name like CONCAT('%', #{query.nickName}, '%')</if> ",
            "     <if test=\"query.realName != null and query.realName != ''\">and real_name like CONCAT('%', #{query.realName}, '%')</if> ",
            "     <if test=\"query.phoneNumber != null and query.phoneNumber != ''\">and phone_number like CONCAT('%', #{query.phoneNumber}, '%')</if> ",
            "     <if test=\"query.gender != null\">and `gender` = #{query.gender}</if> ",
            "     <if test=\"query.playerAvatar != null and query.playerAvatar != ''\">and player_avatar like CONCAT('%', #{query.playerAvatar}, '%')</if> ",
            "     <if test=\"query.password != null and query.password != ''\">and password like CONCAT('%', #{query.password}, '%')</if> ",
            "     <if test=\"query.email != null and query.email != ''\">and email like CONCAT('%', #{query.email}, '%')</if> ",
            "     <if test=\"query.nationality != null and query.nationality != ''\">and nationality like CONCAT('%', #{query.nationality}, '%')</if> ",
            "     <if test=\"query.province != null and query.province != ''\">and province like CONCAT('%', #{query.province}, '%')</if> ",
            "     <if test=\"query.city != null and query.city != ''\">and city like CONCAT('%', #{query.city}, '%')</if> ",
            "     <if test=\"query.address != null and query.address != ''\">and address like CONCAT('%', #{query.address}, '%')</if> ",
            "     <if test=\"query.platformId != null and query.platformId != ''\">and platform_id like CONCAT('%', #{query.platformId}, '%')</if> ",
            "     <if test=\"query.registrationTime != null\">and `registration_time` = #{query.registrationTime}</if> ",
            "     <if test=\"query.status != null\">and `status` = #{query.status}</if> ",
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
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.name != null and query.name != ''\">and name like CONCAT('%', #{query.name}, '%')</if> ",
            "     <if test=\"query.nickName != null and query.nickName != ''\">and nick_name like CONCAT('%', #{query.nickName}, '%')</if> ",
            "     <if test=\"query.realName != null and query.realName != ''\">and real_name like CONCAT('%', #{query.realName}, '%')</if> ",
            "     <if test=\"query.phoneNumber != null and query.phoneNumber != ''\">and phone_number like CONCAT('%', #{query.phoneNumber}, '%')</if> ",
            "     <if test=\"query.gender != null\">and `gender` = #{query.gender}</if> ",
            "     <if test=\"query.playerAvatar != null and query.playerAvatar != ''\">and player_avatar like CONCAT('%', #{query.playerAvatar}, '%')</if> ",
            "     <if test=\"query.password != null and query.password != ''\">and password like CONCAT('%', #{query.password}, '%')</if> ",
            "     <if test=\"query.email != null and query.email != ''\">and email like CONCAT('%', #{query.email}, '%')</if> ",
            "     <if test=\"query.nationality != null and query.nationality != ''\">and nationality like CONCAT('%', #{query.nationality}, '%')</if> ",
            "     <if test=\"query.province != null and query.province != ''\">and province like CONCAT('%', #{query.province}, '%')</if> ",
            "     <if test=\"query.city != null and query.city != ''\">and city like CONCAT('%', #{query.city}, '%')</if> ",
            "     <if test=\"query.address != null and query.address != ''\">and address like CONCAT('%', #{query.address}, '%')</if> ",
            "     <if test=\"query.platformId != null and query.platformId != ''\">and platform_id like CONCAT('%', #{query.platformId}, '%')</if> ",
            "     <if test=\"query.registrationTime != null\">and `registration_time` = #{query.registrationTime}</if> ",
            "     <if test=\"query.status != null\">and `status` = #{query.status}</if> ",
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
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.name != null and query.name != ''\">and name like CONCAT('%', #{query.name}, '%')</if> ",
            "     <if test=\"query.nickName != null and query.nickName != ''\">and nick_name like CONCAT('%', #{query.nickName}, '%')</if> ",
            "     <if test=\"query.realName != null and query.realName != ''\">and real_name like CONCAT('%', #{query.realName}, '%')</if> ",
            "     <if test=\"query.phoneNumber != null and query.phoneNumber != ''\">and phone_number like CONCAT('%', #{query.phoneNumber}, '%')</if> ",
            "     <if test=\"query.gender != null\">and `gender` = #{query.gender}</if> ",
            "     <if test=\"query.playerAvatar != null and query.playerAvatar != ''\">and player_avatar like CONCAT('%', #{query.playerAvatar}, '%')</if> ",
            "     <if test=\"query.password != null and query.password != ''\">and password like CONCAT('%', #{query.password}, '%')</if> ",
            "     <if test=\"query.email != null and query.email != ''\">and email like CONCAT('%', #{query.email}, '%')</if> ",
            "     <if test=\"query.nationality != null and query.nationality != ''\">and nationality like CONCAT('%', #{query.nationality}, '%')</if> ",
            "     <if test=\"query.province != null and query.province != ''\">and province like CONCAT('%', #{query.province}, '%')</if> ",
            "     <if test=\"query.city != null and query.city != ''\">and city like CONCAT('%', #{query.city}, '%')</if> ",
            "     <if test=\"query.address != null and query.address != ''\">and address like CONCAT('%', #{query.address}, '%')</if> ",
            "     <if test=\"query.platformId != null and query.platformId != ''\">and platform_id like CONCAT('%', #{query.platformId}, '%')</if> ",
            "     <if test=\"query.registrationTime != null\">and `registration_time` = #{query.registrationTime}</if> ",
            "     <if test=\"query.status != null\">and `status` = #{query.status}</if> ",
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
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.name != null and query.name != ''\">and name like CONCAT('%', #{query.name}, '%')</if> ",
            "     <if test=\"query.nickName != null and query.nickName != ''\">and nick_name like CONCAT('%', #{query.nickName}, '%')</if> ",
            "     <if test=\"query.realName != null and query.realName != ''\">and real_name like CONCAT('%', #{query.realName}, '%')</if> ",
            "     <if test=\"query.phoneNumber != null and query.phoneNumber != ''\">and phone_number like CONCAT('%', #{query.phoneNumber}, '%')</if> ",
            "     <if test=\"query.gender != null\">and `gender` = #{query.gender}</if> ",
            "     <if test=\"query.playerAvatar != null and query.playerAvatar != ''\">and player_avatar like CONCAT('%', #{query.playerAvatar}, '%')</if> ",
            "     <if test=\"query.password != null and query.password != ''\">and password like CONCAT('%', #{query.password}, '%')</if> ",
            "     <if test=\"query.email != null and query.email != ''\">and email like CONCAT('%', #{query.email}, '%')</if> ",
            "     <if test=\"query.nationality != null and query.nationality != ''\">and nationality like CONCAT('%', #{query.nationality}, '%')</if> ",
            "     <if test=\"query.province != null and query.province != ''\">and province like CONCAT('%', #{query.province}, '%')</if> ",
            "     <if test=\"query.city != null and query.city != ''\">and city like CONCAT('%', #{query.city}, '%')</if> ",
            "     <if test=\"query.address != null and query.address != ''\">and address like CONCAT('%', #{query.address}, '%')</if> ",
            "     <if test=\"query.platformId != null and query.platformId != ''\">and platform_id like CONCAT('%', #{query.platformId}, '%')</if> ",
            "     <if test=\"query.registrationTime != null\">and `registration_time` = #{query.registrationTime}</if> ",
            "     <if test=\"query.status != null\">and `status` = #{query.status}</if> ",
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
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.name != null and query.name != ''\">and name like CONCAT('%', #{query.name}, '%')</if> ",
            "     <if test=\"query.nickName != null and query.nickName != ''\">and nick_name like CONCAT('%', #{query.nickName}, '%')</if> ",
            "     <if test=\"query.realName != null and query.realName != ''\">and real_name like CONCAT('%', #{query.realName}, '%')</if> ",
            "     <if test=\"query.phoneNumber != null and query.phoneNumber != ''\">and phone_number like CONCAT('%', #{query.phoneNumber}, '%')</if> ",
            "     <if test=\"query.gender != null\">and `gender` = #{query.gender}</if> ",
            "     <if test=\"query.playerAvatar != null and query.playerAvatar != ''\">and player_avatar like CONCAT('%', #{query.playerAvatar}, '%')</if> ",
            "     <if test=\"query.password != null and query.password != ''\">and password like CONCAT('%', #{query.password}, '%')</if> ",
            "     <if test=\"query.email != null and query.email != ''\">and email like CONCAT('%', #{query.email}, '%')</if> ",
            "     <if test=\"query.nationality != null and query.nationality != ''\">and nationality like CONCAT('%', #{query.nationality}, '%')</if> ",
            "     <if test=\"query.province != null and query.province != ''\">and province like CONCAT('%', #{query.province}, '%')</if> ",
            "     <if test=\"query.city != null and query.city != ''\">and city like CONCAT('%', #{query.city}, '%')</if> ",
            "     <if test=\"query.address != null and query.address != ''\">and address like CONCAT('%', #{query.address}, '%')</if> ",
            "     <if test=\"query.platformId != null and query.platformId != ''\">and platform_id like CONCAT('%', #{query.platformId}, '%')</if> ",
            "     <if test=\"query.registrationTime != null\">and `registration_time` = #{query.registrationTime}</if> ",
            "     <if test=\"query.status != null\">and `status` = #{query.status}</if> ",
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

