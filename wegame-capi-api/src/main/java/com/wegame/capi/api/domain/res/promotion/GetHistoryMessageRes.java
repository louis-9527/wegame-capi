package com.wegame.capi.api.domain.res.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GetHistoryMessageRes {

    private String refId;

    private Integer totalCount;
    private Integer totalPages;
    private Integer page;
    private Integer limit;

    private List<MessageRes> messages;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageRes implements Serializable {

        private static final long serialVersionUID = 2046614827967424757L;

        private String  playerName;
        private String channelId;
        private String missionId;
        private String missionName;
        private String missionDescription;
        private String rewardId;
        private String rewardName;
        private String rewardDescription;
        private String pId;
        private Integer type;
        private Date createTime;


    }
}
