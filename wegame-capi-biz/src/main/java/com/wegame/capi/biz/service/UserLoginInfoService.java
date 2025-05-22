package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.user.UserLoginInfoConverter;
import com.wegame.capi.biz.domain.user.UserLoginInfoDo;
import com.wegame.capi.dao.mapper.user.UserLoginInfoMapper;
import com.wegame.capi.dao.mapper.domain.user.UserLoginInfoPo;
import com.wegame.capi.dao.mapper.domain.user.UserLoginInfoQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.defaultThreadPoolExecutor;

/**
 * po服务
 * @author genereate
 */
@Slf4j
@Service
public class UserLoginInfoService {

    @Autowired
    UserLoginInfoMapper userLoginInfoMapper;

    /**
     * 新增
     *
     * @param userLoginInfoDo 用户登录信息
     * @return 主键id
     */
    public Long add(UserLoginInfoDo userLoginInfoDo) {
        AssertUtil.assertNotNull(userLoginInfoDo, ErrorCode.ERR_PARAM);
        UserLoginInfoPo po = UserLoginInfoConverter.doToPo(userLoginInfoDo, OptEnum.ADD);
        AssertUtil.assertNotNull(po, ErrorCode.ERR_PARAM);
        userLoginInfoMapper.insert(po);
        return po.getId();
    }

    /**
     * 批量新增
     *
     * @param doList 用户登录信息列表
     * @return true/false
     */
    public boolean batchAdd(List<UserLoginInfoDo> doList) {
        AssertUtil.assertNotEmpty(doList, ErrorCode.ERR_PARAM);
        userLoginInfoMapper.batchInsert(UserLoginInfoConverter.doToPoList(doList, OptEnum.ADD));
        return true;
    }

    /**
     * 删除：物理删除
     *
     * @param id 主键id
     * @return true/false
     */
    public boolean delete(Long id) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        userLoginInfoMapper.deleteById(id);
        return true;
    }

    /**
     * 删除：批量物理删除
     *
     * @param ids 主键id列表
     * @return true/false
     */
    public boolean batchDelete(List<Long> ids) {
        AssertUtil.assertNotEmpty(ids, ErrorCode.ERR_PARAM);
        userLoginInfoMapper.deleteByIds(ids);
        return true;
    }

    /**
     * 修改
     *
     * @param userLoginInfoDo 用户登录信息
     * @return true/false
     */
    public boolean update(UserLoginInfoDo userLoginInfoDo) {
        AssertUtil.assertNotNull(userLoginInfoDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(userLoginInfoDo.getId(), ErrorCode.ERR_PARAM);
        userLoginInfoMapper.updateById(UserLoginInfoConverter.doToPo(userLoginInfoDo, OptEnum.UPDATE));
        return true;
    }

    /**
     * 查询：根据主键id查询
     *
     * @param id 主键id
     * @return UserLoginInfoDo 用户登录信息
     */
    public UserLoginInfoDo get(Long id) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        return UserLoginInfoConverter.poToDo(userLoginInfoMapper.selectById(id));
    }

    /**
     * 查询：根据条件查询
     *
     * @param query 查询条件
     * @return UserLoginInfoDo 用户登录信息
     */
    public UserLoginInfoDo selectOne(UserLoginInfoQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return UserLoginInfoConverter.poToDo(userLoginInfoMapper.selectOne(query));
    }

    /**
     * 查询Id：根据条件查询
     *
     * @param query 查询条件
     * @return id
     */
     public Long selectId(UserLoginInfoQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return userLoginInfoMapper.selectId(query);
     }

    /**
     * 查询：根据id列表
     *
     * @param ids id列表
     * @return List<UserLoginInfoDo> 用户登录信息列表
     */
    public List<UserLoginInfoDo> listByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return UserLoginInfoConverter.poToDoList(userLoginInfoMapper.listByIds(ids));
    }

    /**
     * 查询全部
     *
     * @return List<UserLoginInfoDo> 用户登录信息列表
     */
    public List<UserLoginInfoDo> listAll() {
        return UserLoginInfoConverter.poToDoList(userLoginInfoMapper.listAll());
    }

    /**
     * 查询id列表
     *
     * @param query 查询参数
     * @return List<Long> id列表
     */
    public List<Long> listId(UserLoginInfoQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return userLoginInfoMapper.listId(query);
    }

    /**
     * 查询列表
     *
     * @param query 查询参数
     * @return List<UserLoginInfoDo> 用户登录信息列表
     */
    public List<UserLoginInfoDo> list(UserLoginInfoQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return UserLoginInfoConverter.poToDoList(userLoginInfoMapper.list(query));
    }

    /**
     * 数量统计
     *
     * @param query 查询参数
     * @return 数量
     */
    public long count(UserLoginInfoQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return userLoginInfoMapper.count(query);
    }

    ///////////////  above auto generated ///////////////

    @Async(defaultThreadPoolExecutor)
    public void asyncAddOrUpdate(UserLoginInfoDo userLoginInfoDo) {
        try {
            Long id = userLoginInfoMapper.selectId(UserLoginInfoQuery.builder()
                    .channelId(userLoginInfoDo.getChannelId())
                    .gameId(userLoginInfoDo.getGameId())
                    .userId(userLoginInfoDo.getUserId())
                    .playerId(userLoginInfoDo.getPlayerId())
                    .build());
            if (Objects.isNull(id)) {
                userLoginInfoDo.setRegisterTime(new Date());
                add(userLoginInfoDo);
            } else {
                UserLoginInfoDo updateDo = UserLoginInfoDo.builder().id(id).loginTime(userLoginInfoDo.getLoginTime()).build();
                update(updateDo);
            }
        } catch (Exception e) {
            log.warn("asyncAddOrUpdate Exception, req: {}, errMsg: {}", userLoginInfoDo, e.getMessage(), e);
        }
    }
}