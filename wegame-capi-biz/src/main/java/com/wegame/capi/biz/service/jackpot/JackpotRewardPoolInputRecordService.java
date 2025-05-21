package com.wegame.capi.biz.service.jackpot;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.jackpot.JackpotRewardPoolInputRecordConverter;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.dao.mapper.business.JackpotRewardPoolInputRecordMapper;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolInputRecordPo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolInputRecordQuery;
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
public class JackpotRewardPoolInputRecordService {

    @Autowired
    JackpotRewardPoolInputRecordMapper jackpotRewardPoolInputRecordMapper;

    /**
     * 新增
     *
     * @param jackpotRewardPoolInputRecordDo jackpot奖池流入记录
     * @return 主键id
     */
    public Long add(JackpotRewardPoolInputRecordDo jackpotRewardPoolInputRecordDo) {
        AssertUtil.assertNotNull(jackpotRewardPoolInputRecordDo, ErrorCode.ERR_PARAM);
        JackpotRewardPoolInputRecordPo po = JackpotRewardPoolInputRecordConverter.doToPo(jackpotRewardPoolInputRecordDo, OptEnum.ADD);
        AssertUtil.assertNotNull(po, ErrorCode.ERR_PARAM);
        jackpotRewardPoolInputRecordMapper.insert(po);
        return po.getId();
    }

    /**
     * 批量新增
     *
     * @param doList jackpot奖池流入记录列表
     * @return true/false
     */
    public boolean batchAdd(List<JackpotRewardPoolInputRecordDo> doList) {
        AssertUtil.assertNotEmpty(doList, ErrorCode.ERR_PARAM);
        jackpotRewardPoolInputRecordMapper.batchInsert(JackpotRewardPoolInputRecordConverter.doToPoList(doList, OptEnum.ADD));
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
        jackpotRewardPoolInputRecordMapper.deleteById(id);
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
        jackpotRewardPoolInputRecordMapper.deleteByIds(ids);
        return true;
    }

    /**
     * 修改
     *
     * @param jackpotRewardPoolInputRecordDo jackpot奖池流入记录
     * @return true/false
     */
    public boolean update(JackpotRewardPoolInputRecordDo jackpotRewardPoolInputRecordDo) {
        AssertUtil.assertNotNull(jackpotRewardPoolInputRecordDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(jackpotRewardPoolInputRecordDo.getId(), ErrorCode.ERR_PARAM);
        jackpotRewardPoolInputRecordMapper.updateById(JackpotRewardPoolInputRecordConverter.doToPo(jackpotRewardPoolInputRecordDo, OptEnum.UPDATE));
        return true;
    }

    /**
     * 查询：根据主键id查询
     *
     * @param id 主键id
     * @return JackpotRewardPoolInputRecordDo jackpot奖池流入记录
     */
    public JackpotRewardPoolInputRecordDo get(Long id) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        return JackpotRewardPoolInputRecordConverter.poToDo(jackpotRewardPoolInputRecordMapper.selectById(id));
    }

    /**
     * 查询：根据条件查询
     *
     * @param query 查询条件
     * @return JackpotRewardPoolInputRecordDo jackpot奖池流入记录
     */
    public JackpotRewardPoolInputRecordDo selectOne(JackpotRewardPoolInputRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return JackpotRewardPoolInputRecordConverter.poToDo(jackpotRewardPoolInputRecordMapper.selectOne(query));
    }

    /**
     * 查询Id：根据条件查询
     *
     * @param query 查询条件
     * @return id
     */
     public Long selectId(JackpotRewardPoolInputRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return jackpotRewardPoolInputRecordMapper.selectId(query);
     }

    /**
     * 查询：根据id列表
     *
     * @param ids id列表
     * @return List<JackpotRewardPoolInputRecordDo> jackpot奖池流入记录列表
     */
    public List<JackpotRewardPoolInputRecordDo> listByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return JackpotRewardPoolInputRecordConverter.poToDoList(jackpotRewardPoolInputRecordMapper.listByIds(ids));
    }

    /**
     * 查询全部
     *
     * @return List<JackpotRewardPoolInputRecordDo> jackpot奖池流入记录列表
     */
    public List<JackpotRewardPoolInputRecordDo> listAll() {
        return JackpotRewardPoolInputRecordConverter.poToDoList(jackpotRewardPoolInputRecordMapper.listAll());
    }

    /**
     * 查询id列表
     *
     * @param query 查询参数
     * @return List<Long> id列表
     */
    public List<Long> listId(JackpotRewardPoolInputRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return jackpotRewardPoolInputRecordMapper.listId(query);
    }

    /**
     * 查询列表
     *
     * @param query 查询参数
     * @return List<JackpotRewardPoolInputRecordDo> jackpot奖池流入记录列表
     */
    public List<JackpotRewardPoolInputRecordDo> list(JackpotRewardPoolInputRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return JackpotRewardPoolInputRecordConverter.poToDoList(jackpotRewardPoolInputRecordMapper.list(query));
    }

    /**
     * 数量统计
     *
     * @param query 查询参数
     * @return 数量
     */
    public long count(JackpotRewardPoolInputRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return jackpotRewardPoolInputRecordMapper.count(query);
    }

    ///////////////  above auto generated ///////////////
}