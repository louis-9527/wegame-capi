package com.wegame.capi.client.domain.request.generate;

import com.wegame.base.Validate;
import com.wegame.base.domain.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javafx.util.Pair;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "模版生产请求参数")
public class TemplateGenerateRequest extends BaseRequest implements Validate, Serializable {
    private static final long serialVersionUID = -7256326783223327817L;
    @ApiModelProperty(value = "2.1 符合脚本数量：模拟程式重复进行到满足数量为止，运行中终端印出进度", required = true)
    private Integer total_num;
    @ApiModelProperty(value = "2.2 指定输出路径：符合的脚本输出路径", required = true)
    private String export_dir;
    @ApiModelProperty(value = "2.3 脚本资讯：脚本的标签", required = true)
    private String info;
    @ApiModelProperty(value = "2.4 赔率区间", required = true)
    private Pair<Integer, Integer> odds_range;
    @ApiModelProperty(value = "2.5 是否触发免费游戏", required = true)
    private Integer mg_is_fg;
    @ApiModelProperty(value = "2.6 免费游戏的赔率区间", required = true)
    private Integer fg_odds_range;
    @ApiModelProperty(value = "2.7 主游戏中Scatter符号出现数量区间", required = true)
    private Integer mg_sc_range;
    @ApiModelProperty(value = "2.8 主游戏乘倍百搭数量区间", required = true)
    private Integer mg_mutiple_wild_num_range;
    @ApiModelProperty(value = "2.9 主游戏乘倍百搭最高倍数区间", required = true)
    private Integer mg_mutiple_wild_times_range;
    @ApiModelProperty(value = "2.10 主游戏n连区间", required = true)
    private Integer mg_n_made_range;
    @ApiModelProperty(value = "2.11 主游戏组合数区间", required = true)
    private Integer mg_combine_num_range;
    @ApiModelProperty(value = "2.12 免费游戏最终累积乘倍区间(免游是否有倍数上限?)最大91倍", required = true)
    private Integer fg_final_mutiple_wild_times_range;

    @Override
    public void validateParam() {
        // todo @麦冬
    }
}
