package com.yifan.newsflow.module.service;

import com.yifan.newsflow.module.entity.FileRecord;
import com.yifan.newsflow.module.mapper.FileRecordMapper;
import com.yifan.newsflow.module.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FileRecordService {

    @Autowired
    private FileRecordMapper fileRecordMapper;

    public FileRecord getById(Long id) {
        log.info("查询记录，id={}", id);
        return fileRecordMapper.getById(id);
    }

    public FileRecord extractById(Long id) {
        log.info("提取记录，id={}", id);
        return fileRecordMapper.extractById(id);
    }

    public int update(FileRecord fileRecord) {
        log.info("更新记录，id={}", fileRecord.getId());
        return fileRecordMapper.update(fileRecord);
    }

    public int insert(FileRecord fileRecord) {
        log.info("插入记录，id={}", fileRecord.getId());
        return fileRecordMapper.insert(fileRecord);
    }

    public boolean delete(Long id) {
        log.info("删除记录，id={}", id);
        return fileRecordMapper.delete(id, TimeUtils.currentSeconds()) > 0;
    }

}
