package com.yifan.newsflow.module.entity;

import com.yifan.newsflow.module.utils.TimeUtils;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileRecord {

    private Long id;
    private Integer type;
    private String url;
    private Integer createTime;
    private Integer updateTime = TimeUtils.currentSeconds();
    private Integer isDeleted;
}
