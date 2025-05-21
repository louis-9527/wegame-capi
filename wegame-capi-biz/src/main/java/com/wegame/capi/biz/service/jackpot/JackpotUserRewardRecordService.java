package com.wegame.capi.biz.service.jackpot;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.jackpot.JackpotUserRewardRecordConverter;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.dao.mapper.business.JackpotUserRewardRecordMapper;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordPo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
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
public class JackpotUserRewardRecordService {

    @Autowired
    JackpotUserRewardRecordMapper jackpotUserRewardRecordMapper;

    /**
     * 新增
     *
     * @param jackpotUserRewardRecordDo 用户jackpot中奖记录
     * @return 主键id
     */
    public Long add(JackpotUserRewardRecordDo jackpotUserRewardRecordDo) {
        AssertUtil.assertNotNull(jackpotUserRewardRecordDo, ErrorCode.ERR_PARAM);
        JackpotUserRewardRecordPo po = JackpotUserRewardRecordConverter.doToPo(jackpotUserRewardRecordDo, OptEnum.ADD);
        AssertUtil.assertNotNull(po, ErrorCode.ERR_PARAM);
        jackpotUserRewardRecordMapper.insert(po);
        return po.getId();
    }

    /**
     * 批量新增
     *
     * @param doList 用户jackpot中奖记录列表
     * @return true/false
     */
    public boolean batchAdd(List<JackpotUserRewardRecordDo> doList) {
        AssertUtil.assertNotEmpty(doList, ErrorCode.ERR_PARAM);
        jackpotUserRewardRecordMapper.batchInsert(JackpotUserRewardRecordConverter.doToPoList(doList, OptEnum.ADD));
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
        jackpotUserRewardRecordMapper.deleteById(id);
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
        jackpotUserRewardRecordMapper.deleteByIds(ids);
        return true;
    }

    /**
     * 修改
     *
     * @param jackpotUserRewardRecordDo 用户jackpot中奖记录
     * @return true/false
     */
    public boolean update(JackpotUserRewardRecordDo jackpotUserRewardRecordDo) {
        AssertUtil.assertNotNull(jackpotUserRewardRecordDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(jackpotUserRewardRecordDo.getId(), ErrorCode.ERR_PARAM);
        jackpotUserRewardRecordMapper.updateById(JackpotUserRewardRecordConverter.doToPo(jackpotUserRewardRecordDo, OptEnum.UPDATE));
        return true;
    }

    /**
     * 查询：根据主键id查询
     *
     * @param id 主键id
     * @return JackpotUserRewardRecordDo 用户jackpot中奖记录
     */
    public JackpotUserRewardRecordDo get(Long id) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        return JackpotUserRewardRecordConverter.poToDo(jackpotUserRewardRecordMapper.selectById(id));
    }

    /**
     * 查询：根据条件查询
     *
     * @param query 查询条件
     * @return JackpotUserRewardRecordDo 用户jackpot中奖记录
     */
    public JackpotUserRewardRecordDo selectOne(JackpotUserRewardRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return JackpotUserRewardRecordConverter.poToDo(jackpotUserRewardRecordMapper.selectOne(query));
    }

    /**
     * 查询Id：根据条件查询
     *
     * @param query 查询条件
     * @return id
     */
     public Long selectId(JackpotUserRewardRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return jackpotUserRewardRecordMapper.selectId(query);
     }

    /**
     * 查询：根据id列表
     *
     * @param ids id列表
     * @return List<JackpotUserRewardRecordDo> 用户jackpot中奖记录列表
     */
    public List<JackpotUserRewardRecordDo> listByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return JackpotUserRewardRecordConverter.poToDoList(jackpotUserRewardRecordMapper.listByIds(ids));
    }

    /**
     * 查询全部
     *
     * @return List<JackpotUserRewardRecordDo> 用户jackpot中奖记录列表
     */
    public List<JackpotUserRewardRecordDo> listAll() {
        return JackpotUserRewardRecordConverter.poToDoList(jackpotUserRewardRecordMapper.listAll());
    }

    /**
     * 查询id列表
     *
     * @param query 查询参数
     * @return List<Long> id列表
     */
    public List<Long> listId(JackpotUserRewardRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return jackpotUserRewardRecordMapper.listId(query);
    }

    /**
     * 查询列表
     *
     * @param query 查询参数
     * @return List<JackpotUserRewardRecordDo> 用户jackpot中奖记录列表
     */
    public List<JackpotUserRewardRecordDo> list(JackpotUserRewardRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return JackpotUserRewardRecordConverter.poToDoList(jackpotUserRewardRecordMapper.list(query));
    }

    /**
     * 数量统计
     *
     * @param query 查询参数
     * @return 数量
     */
    public long count(JackpotUserRewardRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return jackpotUserRewardRecordMapper.count(query);
    }

    ///////////////  above auto generated ///////////////
}