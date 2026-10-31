package com.yifan.newsflow.app.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CategoryVo {

    private Long categoryId;
    private String name;
    private String image;
    private String description;

}
