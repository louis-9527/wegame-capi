package com.wegame.capi.biz.domain.biz.bizinterfere;

import com.google.common.collect.Lists;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BizInterfereSubTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 业务干预上下文
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BizInterfereContextDo implements Serializable {
    private static final long serialVersionUID = 10872473243L;
    /**
     * 是否有业务干预
     */
    private boolean isBizInterfere;
    /**
     * gameType
     *
     * @see GameTypeEnum
     */
    private GameTypeEnum gameType;
    /**
     * 业务干预奖池快照
     */
    private Map<BizInterfereSubTypeEnum, Long> bizInterfereJackpotSnapshotMap;
    /**
     * 使用的业务干预奖池快照
     */
    private BizInterfereSubTypeEnum inUseJackPotSnapshotType;

    /**
     * 不能随机的奖池类型
     */
    @Builder.Default
    private List<BizInterfereSubTypeEnum> unableJackPotTypeList = Lists.newArrayList();

    public void addUnableJackPotType(BizInterfereSubTypeEnum unableJackPotType) {
        this.unableJackPotTypeList.add(unableJackPotType);
    }

    public Long getOrDefaultZeroInUseJackPotSnapshotAmount() {
        return Optional.ofNullable(bizInterfereJackpotSnapshotMap.get(inUseJackPotSnapshotType)).orElse(0L);
    }

}
