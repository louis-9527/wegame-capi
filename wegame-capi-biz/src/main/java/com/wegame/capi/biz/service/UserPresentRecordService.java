package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.user.UserPresentRecordConverter;
import com.wegame.capi.biz.domain.user.UserPresentRecordDo;
import com.wegame.capi.dao.mapper.user.UserPresentRecordMapper;
import com.wegame.capi.dao.mapper.domain.user.UserPresentRecordPo;
import com.wegame.capi.dao.mapper.domain.user.UserPresentRecordQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * po服务
 * @author genereate
 */
@Service
public class UserPresentRecordService {

    @Autowired
    UserPresentRecordMapper userPresentRecordMapper;

    /**
     * 新增
     *
     * @param userPresentRecordDo 用户赠送记录
     * @return 主键id
     */
    public Long add(UserPresentRecordDo userPresentRecordDo) {
        AssertUtil.assertNotNull(userPresentRecordDo, ErrorCode.ERR_PARAM);
        UserPresentRecordPo po = UserPresentRecordConverter.doToPo(userPresentRecordDo, OptEnum.ADD);
        AssertUtil.assertNotNull(po, ErrorCode.ERR_PARAM);
        userPresentRecordMapper.insert(po);
        return po.getId();
    }

    /**
     * 批量新增
     *
     * @param doList 用户赠送记录列表
     * @return true/false
     */
    public boolean batchAdd(List<UserPresentRecordDo> doList) {
        AssertUtil.assertNotEmpty(doList, ErrorCode.ERR_PARAM);
        userPresentRecordMapper.batchInsert(UserPresentRecordConverter.doToPoList(doList, OptEnum.ADD));
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
        userPresentRecordMapper.deleteById(id);
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
        userPresentRecordMapper.deleteByIds(ids);
        return true;
    }

    /**
     * 修改
     *
     * @param userPresentRecordDo 用户赠送记录
     * @return true/false
     */
    public boolean update(UserPresentRecordDo userPresentRecordDo) {
        AssertUtil.assertNotNull(userPresentRecordDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(userPresentRecordDo.getId(), ErrorCode.ERR_PARAM);
        userPresentRecordMapper.updateById(UserPresentRecordConverter.doToPo(userPresentRecordDo, OptEnum.UPDATE));
        return true;
    }

    /**
     * 查询：根据主键id查询
     *
     * @param id 主键id
     * @return UserPresentRecordDo 用户赠送记录
     */
    public UserPresentRecordDo get(Long id) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        return UserPresentRecordConverter.poToDo(userPresentRecordMapper.selectById(id));
    }

    /**
     * 查询：根据条件查询
     *
     * @param query 查询条件
     * @return UserPresentRecordDo 用户赠送记录
     */
    public UserPresentRecordDo selectOne(UserPresentRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return UserPresentRecordConverter.poToDo(userPresentRecordMapper.selectOne(query));
    }

    /**
     * 查询Id：根据条件查询
     *
     * @param query 查询条件
     * @return id
     */
     public Long selectId(UserPresentRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return userPresentRecordMapper.selectId(query);
     }

    /**
     * 查询：根据id列表
     *
     * @param ids id列表
     * @return List<UserPresentRecordDo> 用户赠送记录列表
     */
    public List<UserPresentRecordDo> listByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return UserPresentRecordConverter.poToDoList(userPresentRecordMapper.listByIds(ids));
    }

    /**
     * 查询全部
     *
     * @return List<UserPresentRecordDo> 用户赠送记录列表
     */
    public List<UserPresentRecordDo> listAll() {
        return UserPresentRecordConverter.poToDoList(userPresentRecordMapper.listAll());
    }

    /**
     * 查询id列表
     *
     * @param query 查询参数
     * @return List<Long> id列表
     */
    public List<Long> listId(UserPresentRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return userPresentRecordMapper.listId(query);
    }

    /**
     * 查询列表
     *
     * @param query 查询参数
     * @return List<UserPresentRecordDo> 用户赠送记录列表
     */
    public List<UserPresentRecordDo> list(UserPresentRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return UserPresentRecordConverter.poToDoList(userPresentRecordMapper.list(query));
    }

    /**
     * 数量统计
     *
     * @param query 查询参数
     * @return 数量
     */
    public long count(UserPresentRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return userPresentRecordMapper.count(query);
    }

    ///////////////  above auto generated ///////////////
}