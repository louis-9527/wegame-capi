package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "任务响应模型")
public class DailyMissionVo implements  Serializable {
    private static final long serialVersionUID = -1704392640858325682L;

    @ApiModelProperty(value = "刷新时间", required = true)
    private Long refreshTime;
    @ApiModelProperty(value = "任务列表", required = true)
    private List<DailyMission> dailyMissionList;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ApiModel(description = "任务")
    public static class DailyMission implements  Serializable {
        @ApiModelProperty(value = "任务ID", required = true)
        private String dailyMissionId;
        @ApiModelProperty(value = "目标进度", required = true)
        private String target;
        @ApiModelProperty(value = "奖励", required = true)
        private String reward;
        @ApiModelProperty(value = "任务描述", required = true)
        private String description;
        @ApiModelProperty(value = "icon地址", required = true)
        private String iconUrl;
        @ApiModelProperty(value = "当前进度", required = true)
        private String experience;
        @ApiModelProperty(value = "状态 1锁定 2进行中 3完成", required = true)
        private Integer status;
        @ApiModelProperty(value = "星级", required = true)
        private Integer starLevel;
        @ApiModelProperty(value = "奖励id", required = true)
        private String rewardConfigId;
    }
}
