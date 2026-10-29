package com.yifan.newsflow.app.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ArticleListVo {

    private List<ArticleVo> list;
    private Boolean isEnd;

}
