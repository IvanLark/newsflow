package com.yifan.newsflow.module.mapper;

import com.yifan.newsflow.module.entity.TempStudent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TempStudentMapper {

    @Select("SELECT id, name, age, class_name, create_time, update_time, is_deleted FROM temp_student WHERE id = #{id} AND is_deleted = 0")
    TempStudent getById(@Param("id") Long id);

    @Select("SELECT id, name, age, class_name, create_time, update_time, is_deleted FROM temp_student WHERE id = #{id}")
    TempStudent extractById(@Param("id") Long id);

    int update(@Param("tempStudent") TempStudent tempStudent);

    int insert(@Param("tempStudent") TempStudent tempStudent);

    @Update("UPDATE temp_student SET is_deleted = 1, update_time = #{updateTime} WHERE id = #{id} AND is_deleted = 0")
    int delete(@Param("id") Long id, @Param("updateTime") Integer updateTime);

}
