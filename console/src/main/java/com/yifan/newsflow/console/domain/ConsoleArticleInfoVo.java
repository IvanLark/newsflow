package com.yifan.newsflow.console.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ConsoleArticleInfoVo {

    private Long articleId;
    private List<String> coverImages;
    private String title;
    private String author;
    private String content;
    private String publishTime;
    private String createTime;
    private String updateTime;

}
