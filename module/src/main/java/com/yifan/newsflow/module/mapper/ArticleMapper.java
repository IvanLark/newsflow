package com.yifan.newsflow.module.mapper;

import com.yifan.newsflow.module.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ArticleMapper {

    List<Article> pageForApp(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("keyword") String keyword, @Param("categoryIds") String categoryIds);

    List<Article> pageForConsole(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("keyword") String keyword, @Param("categoryIds") String categoryIds);

    long countAll(@Param("keyword") String keyword, @Param("categoryIds") String categoryIds);

    @Select("SELECT id, cover_images, title, author, content, category_id, publish_time, create_time, update_time, is_deleted " +
            "FROM article WHERE id = #{articleId} AND is_deleted = 0")
    Article getById(@Param("articleId") Long articleId);

    @Select("SELECT id, cover_images, title, author, content, category_id, publish_time, create_time, update_time, is_deleted " +
            "FROM article WHERE id = #{articleId}")
    Article extractById(@Param("articleId") Long articleId);

    @Select("SELECT id, cover_images, title, author, content, category_id, publish_time, create_time, update_time, is_deleted " +
            "FROM article WHERE category_id = #{categoryId} AND is_deleted = 0")
    List<Article> listByCategoryId(@Param("categoryId") Long categoryId);

    @Select("SELECT id, cover_images, title, author, content, category_id, publish_time, create_time, update_time, is_deleted " +
            "FROM article WHERE is_deleted = 0 ORDER BY id")
    List<Article> listAll();

    int insert(@Param("article") Article article);

    int update(@Param("article") Article article);

    @Update("UPDATE article SET is_deleted = 1, update_time = #{updateTime} " +
            "WHERE id = #{articleId} AND is_deleted = 0")
    int delete(@Param("articleId") Long articleId, @Param("updateTime") Integer updateTime);

}
