package com.yifan.newsflow.module.service;

import com.yifan.newsflow.module.entity.Category;
import com.yifan.newsflow.module.mapper.CategoryMapper;
import com.yifan.newsflow.module.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public Category getById(Long id) {
        log.info("查询记录，id={}", id);
        return categoryMapper.getById(id);
    }

    public Category extractById(Long id) {
        log.info("提取记录，id={}", id);
        return categoryMapper.extractById(id);
    }

    public int update(Category category) {
        log.info("更新记录，id={}", category.getId());
        return categoryMapper.update(category);
    }

    public int insert(Category category) {
        log.info("插入记录，id={}", category.getId());
        return categoryMapper.insert(category);
    }

    public boolean delete(Long id) {
        log.info("删除记录，id={}", id);
        return categoryMapper.delete(id, TimeUtils.currentSeconds()) > 0;
    }

    public List<Category> listAll() {
        log.info("查询分类列表");
        return categoryMapper.listAll();
    }

    public List<Long> listIdsByName(String keyword) {
        log.info("按名称查询分类ID，keyword={}", keyword);
        return categoryMapper.listIdsByName(keyword);
    }

    public Long edit(Long categoryId, String name, String image, String description) {
        if (name == null || name.isBlank()) {
            throw new RuntimeException("name is required");
        }
        if (image == null || image.isBlank()) {
            throw new RuntimeException("image is required");
        }
        if (description == null || description.isBlank()) {
            throw new RuntimeException("description is required");
        }
        Category category = new Category()
                .setName(name)
                .setImage(image)
                .setDescription(description)
                .setIsDeleted(0);
        if (categoryId == null) {
            category.setCreateTime(TimeUtils.currentSeconds());
            if (insert(category) <= 0) {
                throw new RuntimeException("insert category failed");
            }
            return category.getId();
        }
        if (categoryId <= 0) {
            throw new RuntimeException("category id must be positive");
        }
        if (extractById(categoryId) == null) {
            throw new RuntimeException("category not found: " + categoryId);
        }
        category.setId(categoryId);
        if (update(category) <= 0) {
            throw new RuntimeException("update category failed: " + categoryId);
        }
        return categoryId;
    }

}
