package com.yifan.newsflow.module.mapper;

import com.yifan.newsflow.module.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("SELECT id, name, image, description, create_time, update_time, is_deleted FROM category WHERE id = #{id} AND is_deleted = 0")
    Category getById(@Param("id") Long id);

    @Select("SELECT id, name, image, description, create_time, update_time, is_deleted FROM category WHERE id = #{id}")
    Category extractById(@Param("id") Long id);

    @Select("SELECT id, name, image, description, create_time, update_time, is_deleted FROM category WHERE is_deleted = 0 ORDER BY id")
    List<Category> listAll();

    @Select("SELECT id FROM category WHERE is_deleted = 0 AND name LIKE CONCAT('%', #{keyword}, '%')")
    List<Long> listIdsByName(@Param("keyword") String keyword);

    int update(@Param("category") Category category);

    int insert(@Param("category") Category category);

    @Update("UPDATE category SET is_deleted = 1, update_time = #{updateTime} WHERE id = #{id} AND is_deleted = 0")
    int delete(@Param("id") Long id, @Param("updateTime") Integer updateTime);

}
