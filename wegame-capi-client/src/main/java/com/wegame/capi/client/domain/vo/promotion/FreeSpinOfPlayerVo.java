package com.wegame.capi.client.domain.vo.promotion;

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
@EqualsAndHashCode
public class FreeSpinOfPlayerVo implements Serializable {
    private static final long serialVersionUID = 889955515756526338L;
    @ApiModelProperty("playerName")
    private String playerName;
    @ApiModelProperty("refId")
    private String refId;
    @ApiModelProperty("current")
    private List<FreeSpin> current;
    @ApiModelProperty("others")
    private List<FreeSpin> others;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class FreeSpin implements Serializable {
        private static final long serialVersionUID = 85667361882570293L;
        @ApiModelProperty("channelId")
        private String channelId;
        @ApiModelProperty("campaignId")
        private String campaignId;
        @ApiModelProperty("initCount")
        private Integer initCount;
        @ApiModelProperty("remainingCount")
        private Integer remainingCount;
        @ApiModelProperty("expireDate")
        private Integer expireDate;
        @ApiModelProperty("gameId")
        private String gameId;
        @ApiModelProperty("gameIdRef")
        private String gameIdRef;
        @ApiModelProperty("gameName")
        private String gameName;
        @ApiModelProperty("totalWin")
        private Integer totalWin;
        @ApiModelProperty("freeBet")
        private Double freeBet;
    }
}
