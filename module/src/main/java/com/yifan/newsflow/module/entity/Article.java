package com.yifan.newsflow.module.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Article {

    private Long id;
    private String coverImages;
    private String title;
    private String author;
    private String content;
    private Integer publishTime;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;

}
