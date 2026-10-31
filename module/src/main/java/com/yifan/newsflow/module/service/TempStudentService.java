package com.yifan.newsflow.module.service;

import com.yifan.newsflow.module.entity.TempStudent;
import com.yifan.newsflow.module.mapper.TempStudentMapper;
import com.yifan.newsflow.module.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TempStudentService {

    @Autowired
    private TempStudentMapper tempStudentMapper;

    public TempStudent getById(Long id) {
        log.info("查询记录，id={}", id);
        return tempStudentMapper.getById(id);
    }

    public TempStudent extractById(Long id) {
        log.info("提取记录，id={}", id);
        return tempStudentMapper.extractById(id);
    }

    public int update(TempStudent tempStudent) {
        log.info("更新记录，id={}", tempStudent.getId());
        return tempStudentMapper.update(tempStudent);
    }

    public int insert(TempStudent tempStudent) {
        log.info("插入记录，id={}", tempStudent.getId());
        return tempStudentMapper.insert(tempStudent);
    }

    public boolean delete(Long id) {
        log.info("删除记录，id={}", id);
        return tempStudentMapper.delete(id, TimeUtils.currentSeconds()) > 0;
    }

}
