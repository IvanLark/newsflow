package com.yifan.newsflow.module.mapper;

import com.yifan.newsflow.module.entity.OgnlDemo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OgnlDemoMapper {

    OgnlDemo getById(@Param("id") Long id);

    int insert(@Param("demo") OgnlDemo demo);

    int update(@Param("demo") OgnlDemo demo);

}
