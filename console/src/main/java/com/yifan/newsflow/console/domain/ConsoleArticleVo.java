package com.yifan.newsflow.console.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConsoleArticleVo {

    private Long articleId;
    private String wallImage;
    private String title;
    private String categoryName;
    private String publishTime;
    private String createTime;
    private String updateTime;

}
