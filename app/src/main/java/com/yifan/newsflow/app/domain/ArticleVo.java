package com.yifan.newsflow.app.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ArticleVo {

    private Long articleId;
    private String wallImage;
    private String title;
    private Integer publishTime;

}
