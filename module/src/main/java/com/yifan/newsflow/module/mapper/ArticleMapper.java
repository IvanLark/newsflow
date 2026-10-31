package com.yifan.newsflow.module.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yifan.newsflow.module.entity.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}
