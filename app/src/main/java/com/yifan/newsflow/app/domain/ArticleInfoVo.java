package com.yifan.newsflow.app.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ArticleInfoVo {

    private Long articleId;
    private List<String> coverImages;
    private String title;
    private String author;
    private String content;
    private Integer publishTime;

}
