package com.yifan.newsflow.module.entity;

import com.yifan.newsflow.module.utils.TimeUtils;
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
    private Long categoryId;
    private Integer publishTime;
    private Integer createTime;
    private Integer updateTime = TimeUtils.currentSeconds();
    private Integer isDeleted;

}
