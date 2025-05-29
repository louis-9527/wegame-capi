package com.wegame.capi.biz.service;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.converter.user.UserConverter;
import com.wegame.capi.biz.domain.user.UserDo;
import com.wegame.capi.biz.domain.user.UserLoginInfoDo;
import com.wegame.capi.common.utils.AmountUtil;
import com.wegame.capi.dao.mapper.domain.user.UserPo;
import com.wegame.capi.dao.mapper.domain.user.UserQuery;
import com.wegame.capi.dao.mapper.user.UserManualMapper;
import com.wegame.capi.dao.mapper.user.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.defaultThreadPoolExecutor;

/**
 * po服务
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
    SingleWalletService singleWalletService;
    @Autowired
    UserLoginInfoService userLoginInfoService;

    /**
     * 新增
     *
     * @param userDo 用户
     * @return 主键id
     */
    public Long add(UserDo userDo) {
        AssertUtil.assertNotNull(userDo, ErrorCode.ERR_PARAM);
        UserPo po = UserConverter.doToPo(userDo, OptEnum.ADD);
        AssertUtil.assertNotNull(po, ErrorCode.ERR_PARAM);
        userMapper.insert(po);
        return po.getId();
    }

    /**
     * 批量新增
     *
     * @param doList 用户列表
     * @return true/false
     */
    public boolean batchAdd(List<UserDo> doList) {
        AssertUtil.assertNotEmpty(doList, ErrorCode.ERR_PARAM);
        userMapper.batchInsert(UserConverter.doToPoList(doList, OptEnum.ADD));
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
        userMapper.deleteById(id);
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
        userMapper.deleteByIds(ids);
        return true;
    }

    /**
     * 修改
     *
     * @param userDo 用户
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
     * @return UserDo 用户
     */
    public UserDo get(Long id) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        return UserConverter.poToDo(userMapper.selectById(id));
    }

    /**
     * 查询：根据条件查询
     *
     * @param query 查询条件
     * @return UserDo 用户
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
     * @return List<UserDo> 用户列表
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
     * @return List<UserDo> 用户列表
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
     * @return List<UserDo> 用户列表
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

    /// ////////////  above auto generated ///////////////
    @Cacheable(value = CacheConstants.USER_ID, key = "#playerId + '_'  + #pid")
    public String selectIdByPlayerIdAndPid(String playerId, String pid) {
        Long id = userManualMapper.selectIdByPlayerIdAndPid(playerId, pid);
        if (Objects.nonNull(id)) {
            return String.valueOf(id);
        } else {
            return null;
        }
    }

    @Async(defaultThreadPoolExecutor)
    @CachePut(value = CacheConstants.USER_ID, key = "#playerId + '_' + #pid")
    public String asyncPutUserIdCache(Long userId, String playerId, String pid) {
        return String.valueOf(userId);
    }

    public Long insert(UserDo userDo, String channelId, String gameId) {
        long startTime = DateUtil.getCurrentMilli();
        Long userId = userDo.getId();
        try {
            if (Objects.isNull(userId)) {
                UserPo userPo = UserConverter.doToPo(userDo, OptEnum.ADD);
                AssertUtil.assertNotNull(userPo, ErrorCode.ERR_PARAM);
                // 新增
                userMapper.insert(userPo);
                AssertUtil.assertNotNull(userPo.getId(), ErrorCode.ERR_INTER, "login error, please try again later");
                userId = userPo.getId();
            }
            userLoginInfoService.asyncAddOrUpdate(UserLoginInfoDo.builder()
                    .channelId(Long.parseLong(channelId))
                    .gameId(gameId)
                    .userId(userId)
                    .playerId(userDo.getPlayerId())
                    .loginTime(new Date())
                    .build());
            return userId;
        } catch (Exception e) {
            log.error("insert err, errMsg: {}, userDo: {}", e.getMessage(), userDo, e);
            throw new BusinessException(ErrorCode.ERR_INTER, "login error, please try again later");
        } finally {
            long costTime = DateUtil.getCurrentMilli() - startTime;
            if (costTime > 1000L) {
                log.info("insert cost time more than 1000: {}", costTime);
            } else if (costTime > 100L) {
                log.info("insert cost time more than 100: {}", costTime);
            } else {
                log.info("insert cost time: {}", costTime);
            }
        }
    }

    public Long getCredit() {
        BigDecimal credit = singleWalletService.getCredit();
        AssertUtil.assertNotNull(credit, ErrorCode.ERR_NET);
        return AmountUtil.toLong(credit);
    }
}