package com.wegame.capi.biz.domain;

import io.swagger.annotations.ApiModel;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@ApiModel(description = "模版生产请求参数")
public class TemplateGenerateDo implements Serializable {
    private static final long serialVersionUID = -7256326783223327817L;
    private Integer total_num;
    private String export_dir;
    private String info;
    private Pair<Integer, Integer> odds_range;
    private Integer mg_is_fg;
    private Integer fg_odds_range;
    private Integer mg_sc_range;
    private Integer mg_mutiple_wild_num_range;
    private Integer mg_mutiple_wild_times_range;
    private Integer mg_n_made_range;
    private Integer mg_combine_num_range;
    private Integer fg_final_mutiple_wild_times_range;
}
