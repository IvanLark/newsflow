package com.yifan.newsflow.module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yifan.newsflow.module.util.TimeUtils;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("temp_student")
public class TempStudent {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer age;
    private String className;
    private Integer createTime;
    private Integer updateTime = TimeUtils.currentSeconds();
    private Integer isDeleted;
}
