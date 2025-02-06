package com.wegame.capi.biz.domain.biz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * 投注记录批量操作结果Do
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BetRecordBatchResultDo implements Serializable {
    private static final long serialVersionUID = -35434547343525888L;
    /**
     * 成功条数
     */
    private Long successCount;
    /**
     * 重复的key id list
     */
    private List<Long> dupKeyIdList;

    /**
     * 一般操作失败id列表
     */
    private List<Long> normalFailIdList;
}

