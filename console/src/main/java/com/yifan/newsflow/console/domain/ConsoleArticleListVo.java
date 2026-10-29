package com.yifan.newsflow.console.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ConsoleArticleListVo {

    private List<ConsoleArticleVo> list;
    private Long total;
    private Integer pageSize;

}
