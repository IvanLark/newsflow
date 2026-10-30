package com.yifan.newsflow.module.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OgnlDemo {

    private Long id;
    private String name;
    private Integer status;
    private Integer createTime;
    private Integer updateTime;

}
