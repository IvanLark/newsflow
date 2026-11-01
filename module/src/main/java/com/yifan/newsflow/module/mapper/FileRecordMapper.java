package com.yifan.newsflow.module.mapper;

import com.yifan.newsflow.module.entity.FileRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface FileRecordMapper {

    @Select("SELECT id, type, url, create_time, update_time, is_deleted FROM file WHERE id = #{id} AND is_deleted = 0")
    FileRecord getById(@Param("id") Long id);

    @Select("SELECT id, type, url, create_time, update_time, is_deleted FROM file WHERE id = #{id}")
    FileRecord extractById(@Param("id") Long id);

    int update(@Param("fileRecord") FileRecord fileRecord);

    int insert(@Param("fileRecord") FileRecord fileRecord);

    @Update("UPDATE file SET is_deleted = 1, update_time = #{updateTime} WHERE id = #{id} AND is_deleted = 0")
    int delete(@Param("id") Long id, @Param("updateTime") Integer updateTime);

}
