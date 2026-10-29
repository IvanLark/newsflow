package com.yifan.newsflow.module.mapper;

import com.yifan.newsflow.module.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {

    List<Article> list();

    Article getById(@Param("articleId") Long articleId);

    int insert(@Param("article") Article article);

    int update(@Param("article") Article article);

    int delete(@Param("articleId") Long articleId, @Param("updateTime") Integer updateTime);

}
