package com.yifan.newsflow.module.service;

import com.yifan.newsflow.module.entity.OgnlDemo;
import com.yifan.newsflow.module.mapper.OgnlDemoMapper;
import com.yifan.newsflow.module.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OgnlDemoService {

    @Autowired
    private OgnlDemoMapper ognlDemoMapper;

    public OgnlDemo save(Long ognlDemoId, String name, Integer status) {
        if (name == null || name.isBlank()) {
            throw new RuntimeException("name is required");
        }
        if (status == null) {
            throw new RuntimeException("status is required");
        }
        Integer now = TimeUtils.currentSeconds();
        if (ognlDemoId == null) {
            OgnlDemo demo = new OgnlDemo()
                    .setName(name.trim())
                    .setStatus(status)
                    .setCreateTime(now)
                    .setUpdateTime(now);
            int rows = ognlDemoMapper.insert(demo);
            log.info("新增演示数据，影响行数={}，自增id={}", rows, demo.getId());
            return ognlDemoMapper.getById(demo.getId());
        }
        OgnlDemo demo = new OgnlDemo()
                .setId(ognlDemoId)
                .setName(name.trim())
                .setStatus(status)
                .setUpdateTime(now);
        int rows = ognlDemoMapper.update(demo);
        log.info("更新演示数据，影响行数={}，id={}", rows, ognlDemoId);
        OgnlDemo saved = ognlDemoMapper.getById(ognlDemoId);
        if (saved == null) {
            throw new RuntimeException("demo not found: " + ognlDemoId);
        }
        return saved;
    }

}
