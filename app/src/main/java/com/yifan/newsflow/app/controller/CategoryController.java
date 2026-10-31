package com.yifan.newsflow.app.controller;

import com.yifan.newsflow.app.domain.CategoryListVo;
import com.yifan.newsflow.app.domain.CategoryVo;
import com.yifan.newsflow.module.entity.Category;
import com.yifan.newsflow.module.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/category/list")
    public CategoryListVo list() {
        List<CategoryVo> list = new ArrayList<>();
        for (Category category : categoryService.listAll()) {
            list.add(new CategoryVo()
                    .setCategoryId(category.getId())
                    .setName(category.getName())
                    .setImage(category.getImage())
                    .setDescription(category.getDescription()));
        }
        return new CategoryListVo()
                .setList(list);
    }

}
