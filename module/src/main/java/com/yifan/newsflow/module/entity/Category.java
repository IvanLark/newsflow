package com.yifan.newsflow.module.entity;

import com.yifan.newsflow.module.utils.TimeUtils;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Category {

    private Long id;
    private String name;
    private String image;
    private String description;
    private Integer createTime;
    private Integer updateTime = TimeUtils.currentSeconds();
    private Integer isDeleted;
}
