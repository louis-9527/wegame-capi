package com.wegame.capi.dao.repository.domain.jackpot;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * jackpot_reward_pool_input_record：jackpot奖池流入记录
 *
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JackpotRewardPoolInputRecordPo implements Serializable {
    private static final long serialVersionUID = -7285481505430335488L;
    /**
     * 主键ID/投注记录ID
     */
    @Id
    @Field("_id")
    private Long id;
    /**
     * 类型.10派彩后流入
     */
    private Integer type;
    /**
     * grand金额
     */
    private Long grandAmount;
    /**
     * major金额
     */
    private Long majorAmount;
    /**
     * minor金额
     */
    private Long minorAmount;
    /**
     * mini金额
     */
    private Long miniAmount;
    /**
     * 累计flag：0未累计， 1已累计
     */
    private Integer accFlag;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新人
     */
    private String updater;
    /**
     * 更新时间
     */
    private Long updateTime;

    ///////////////  above auto generated ///////////////
    /**
     * 在指定时间过期(未来时间，mongodb ttl触发删除时间)
     */
    private Date expireAtTime;
}

