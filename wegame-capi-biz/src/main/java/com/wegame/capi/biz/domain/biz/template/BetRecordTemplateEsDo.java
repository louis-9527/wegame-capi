package com.wegame.capi.biz.domain.biz.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BetRecordTemplateEsDo implements Serializable {
    private static final long serialVersionUID = -49339619792477969L;
    @Id
    @Field(type = FieldType.Keyword)
    private Long id;
    /**
     * 游戏ID
     */
    private String gameId;
    /**
     * 投注类型
     */
    @Field(type = FieldType.Keyword)
    private String betType;
    /**
     * 投注子类型
     */
    private String betSubType;
    /**
     * 倍数
     */
    @Field(type = FieldType.Double)
    private Double multiple;
    /**
     * 模版类型 common
     */
    @Field(type = FieldType.Keyword)
    private String templateType;
    /**
     * hash
     */
    private String hash;

    /**
     * bytes： List<T>的json格式后compress. T为具体游戏的模版
     */
    private byte[] bytes;
}
