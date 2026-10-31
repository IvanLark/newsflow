package com.yifan.newsflow.console.controller;

import com.yifan.newsflow.console.domain.ConsoleCategoryInfoVo;
import com.yifan.newsflow.console.domain.ConsoleCategoryListVo;
import com.yifan.newsflow.console.domain.ConsoleCategoryVo;
import com.yifan.newsflow.module.entity.Category;
import com.yifan.newsflow.module.service.ArticleService;
import com.yifan.newsflow.module.service.CategoryService;
import com.yifan.newsflow.module.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class CategoryController {

    private static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleService articleService;

    @RequestMapping("/category/list")
    public ConsoleCategoryListVo list() {
        List<ConsoleCategoryVo> list = new ArrayList<>();
        for (Category category : categoryService.listAll()) {
            list.add(new ConsoleCategoryVo()
                    .setCategoryId(category.getId())
                    .setName(category.getName())
                    .setImage(category.getImage())
                    .setDescription(category.getDescription())
                    .setCreateTime(TimeUtils.format(category.getCreateTime(), TIME_PATTERN))
                    .setUpdateTime(TimeUtils.format(category.getUpdateTime(), TIME_PATTERN)));
        }
        return new ConsoleCategoryListVo()
                .setList(list);
    }

    @RequestMapping("/category/info")
    public ConsoleCategoryInfoVo info(@RequestParam("categoryId") Long categoryId) {
        Category category = categoryService.getById(categoryId);
        if (category == null) {
            return null;
        }
        return new ConsoleCategoryInfoVo()
                .setCategoryId(category.getId())
                .setName(category.getName())
                .setImage(category.getImage())
                .setDescription(category.getDescription())
                .setCreateTime(TimeUtils.format(category.getCreateTime(), TIME_PATTERN))
                .setUpdateTime(TimeUtils.format(category.getUpdateTime(), TIME_PATTERN));
    }

    @RequestMapping("/category/create")
    public String create(@RequestParam("name") String name,
                         @RequestParam("image") String image,
                         @RequestParam("description") String description) {
        Long categoryId;
        try {
            categoryId = categoryService.edit(null, name.trim(), image, description);
        } catch (RuntimeException e) {
            log.error("新增分类失败", e);
            return "新增失败：" + e.getMessage();
        }
        return String.valueOf(categoryId);
    }

    @RequestMapping("/category/update")
    public String update(@RequestParam("categoryId") Long categoryId,
                         @RequestParam("name") String name,
                         @RequestParam("image") String image,
                         @RequestParam("description") String description) {
        Long updatedId;
        try {
            updatedId = categoryService.edit(categoryId, name.trim(), image, description);
        } catch (RuntimeException e) {
            log.error("修改分类失败", e);
            return "更新失败：" + e.getMessage();
        }
        return String.valueOf(updatedId);
    }

    @RequestMapping("/category/delete")
    public String delete(@RequestParam("categoryId") Long categoryId) {
        if (!categoryService.delete(categoryId)) {
            return "失败";
        }
        articleService.deleteByCategoryId(categoryId);
        return "成功";
    }

}
