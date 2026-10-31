package com.yifan.newsflow.console.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConsoleCategoryVo {

    private Long categoryId;
    private String name;
    private String image;
    private String description;
    private String createTime;
    private String updateTime;

}
