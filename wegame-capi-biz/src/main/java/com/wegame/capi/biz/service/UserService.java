package com.wegame.capi.biz.service;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.api.domain.req.singlewallet.GetCreditReq;
import com.wegame.capi.api.domain.res.singlewallet.GetCreditRes;
import com.wegame.capi.biz.domain.user.UserDo;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.user.UserConverter;
import com.wegame.capi.client.domain.request.NotifyCreditChangeRequest;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.dao.mapper.domain.user.UserPo;
import com.wegame.capi.dao.mapper.domain.user.UserQuery;
import com.wegame.capi.dao.mapper.user.UserManualMapper;
import com.wegame.capi.dao.mapper.user.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.wegame.capi.common.Constants.CUSER_CREDIT_CHANGE;

/**
 * UserDo服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserManualMapper userManualMapper;
    @Autowired
    SingleWalletApi singleWalletApi;
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 新增
     *
     * @param userDo 玩家用户
     * @return 主键id
     */
    public Long add(UserDo userDo) {
        AssertUtil.assertNotNull(userDo, ErrorCode.ERR_PARAM);
        UserPo po = UserConverter.doToPo(userDo, OptEnum.ADD);
        userMapper.insert(po);
        return po.getId();
    }

    /**
     * 批量新增
     *
     * @param doList 玩家用户列表
     * @return true/false
     */
    public boolean batchAdd(List<UserDo> doList) {
        AssertUtil.assertNotEmpty(doList, ErrorCode.ERR_PARAM);
        userMapper.batchInsert(UserConverter.doToPoList(doList, OptEnum.ADD));
        return true;
    }


    /**
     * 修改
     *
     * @param userDo 玩家用户
     * @return true/false
     */
    public boolean update(UserDo userDo) {
        AssertUtil.assertNotNull(userDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(userDo.getId(), ErrorCode.ERR_PARAM);
        userMapper.updateById(UserConverter.doToPo(userDo, OptEnum.UPDATE));
        return true;
    }

    /**
     * 查询：根据主键id查询
     *
     * @param id 主键id
     * @return UserDo 玩家用户
     */
    @Cacheable(value = "USER_INFO")
    public UserDo get(Long id) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        return UserConverter.poToDo(userMapper.selectById(id));
    }

    /**
     * 查询：根据条件查询
     *
     * @param query 查询条件
     * @return UserDo 玩家用户
     */
    public UserDo selectOne(UserQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return UserConverter.poToDo(userMapper.selectOne(query));
    }

    /**
     * 查询Id：根据条件查询
     *
     * @param query 查询条件
     * @return id
     */
    public Long selectId(UserQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return userMapper.selectId(query);
    }

    /**
     * 查询：根据id列表
     *
     * @param ids id列表
     * @return List<UserDo> 玩家用户列表
     */
    public List<UserDo> listByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return UserConverter.poToDoList(userMapper.listByIds(ids));
    }

    /**
     * 查询全部
     *
     * @return List<UserDo> 玩家用户列表
     */
    public List<UserDo> listAll() {
        return UserConverter.poToDoList(userMapper.listAll());
    }

    /**
     * 查询id列表
     *
     * @param query 查询参数
     * @return List<Long> id列表
     */
    public List<Long> listId(UserQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return userMapper.listId(query);
    }

    /**
     * 查询列表
     *
     * @param query 查询参数
     * @return List<UserDo> 玩家用户列表
     */
    public List<UserDo> list(UserQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return UserConverter.poToDoList(userMapper.list(query));
    }

    /**
     * 数量统计
     *
     * @param query 查询参数
     * @return 数量
     */
    public long count(UserQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return userMapper.count(query);
    }

    ////

    /**
     * 新增或更新
     *
     * @param userDo
     * @return
     */
    @DSTransactional
    public Long insertOrUpdate(UserDo userDo) {
        try {
            UserPo userPo = UserConverter.doToPo(userDo, OptEnum.ADD);
            Long id = userManualMapper.selectIdByPlayerId(userPo.getPlayerId());
            if (Objects.isNull(id)) {
                // 新增
                userMapper.insert(userPo);
                AssertUtil.assertNotNull(userPo.getId(), ErrorCode.ERR_INTER, "login error, please try again later");
                return userPo.getId();
            } else {
                // 更新
                userPo.setId(id);
                userManualMapper.updateByPlayerId(userPo);
                return id;
            }
        } catch (Exception e) {
            log.error("insertOrUpdate err, errMsg: {}, userDo: {}", e.getMessage(), userDo, e);
            throw new BusinessException(ErrorCode.ERR_INTER, "login error, please try again later");
        }
    }

    public Long getCredit() {
        redisTemplate.opsForSet().remove(CUSER_CREDIT_CHANGE, UserInfoContext.getUserInfo().getPlayerName());
        GetCreditRes res = singleWalletApi.getCredit(GetCreditReq.build());
        return Objects.isNull(res.getCredit()) ? 0L : NumberUtil.mul(res.getCredit(), 100).longValue();
    }

    public Void notifyCreditChange(NotifyCreditChangeRequest request) {
        String hash = singleWalletApi.hash(request.getChannelId(), request.getHash(), request.getRefId(), DateUtil.toUTC(request.getCreateTime()));
        AssertUtil.assertTrue(StringUtils.equals(hash, request.getHash()), ErrorCode.ERR_PARAM);
        redisTemplate.opsForSet().add(CUSER_CREDIT_CHANGE, request.getPlayerName());
        // todo maidong 返回void结果
        return null;
    }

}