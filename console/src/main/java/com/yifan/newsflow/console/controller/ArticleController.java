package com.yifan.newsflow.console.controller;

import com.yifan.newsflow.console.domain.ConsoleArticleInfoVo;
import com.yifan.newsflow.console.domain.ConsoleArticleListVo;
import com.yifan.newsflow.console.domain.ConsoleArticleVo;
import com.yifan.newsflow.module.entity.Article;
import com.yifan.newsflow.module.entity.Category;
import com.yifan.newsflow.module.service.ArticleService;
import com.yifan.newsflow.module.service.CategoryService;
import com.yifan.newsflow.module.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ArticleController {

    private static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/article/list")
    public ConsoleArticleListVo list(@RequestParam("page") int page,
                                     @RequestParam(value = "keyword", required = false) String keyword) {
        List<Article> articles = articleService.pageForConsole(page, keyword);
        Map<Long, Category> categoryMap = new HashMap<>();
        for (Category category : categoryService.listAll()) {
            categoryMap.put(category.getId(), category);
        }
        List<ConsoleArticleVo> list = new ArrayList<>();
        for (Article article : articles) {
            Category category = categoryMap.get(article.getCategoryId());
            if (category == null) {
                continue;
            }
            list.add(new ConsoleArticleVo()
                    .setArticleId(article.getId())
                    .setWallImage(article.getCoverImages().split("\\$")[0])
                    .setTitle(article.getTitle())
                    .setCategoryName(category.getName())
                    .setPublishTime(TimeUtils.format(article.getPublishTime(), TIME_PATTERN))
                    .setCreateTime(TimeUtils.format(article.getCreateTime(), TIME_PATTERN))
                    .setUpdateTime(TimeUtils.format(article.getUpdateTime(), TIME_PATTERN)));
        }
        return new ConsoleArticleListVo()
                .setList(list)
                .setTotal(articleService.countAll(keyword))
                .setPageSize(ArticleService.PAGE_SIZE);
    }

    @RequestMapping("/article/info")
    public ConsoleArticleInfoVo info(@RequestParam("articleId") Long articleId) {
        Article article = articleService.getById(articleId);
        if (article == null) {
            return null;
        }
        Category category = categoryService.getById(article.getCategoryId());
        if (category == null) {
            // TODO 后续改造全局异常处理器之后再来处理分类不存在的情况
            return null;
        }
        return new ConsoleArticleInfoVo()
                .setArticleId(article.getId())
                .setCoverImages(List.of(article.getCoverImages().split("\\$")))
                .setTitle(article.getTitle())
                .setAuthor(article.getAuthor())
                .setContent(article.getContent())
                .setPublishTime(TimeUtils.format(article.getPublishTime(), TIME_PATTERN))
                .setCreateTime(TimeUtils.format(article.getCreateTime(), TIME_PATTERN))
                .setUpdateTime(TimeUtils.format(article.getUpdateTime(), TIME_PATTERN))
                .setCategoryId(article.getCategoryId())
                .setCategoryName(category.getName())
                .setCategoryImage(category.getImage())
                .setCategoryDescription(category.getDescription());
    }

    @RequestMapping("/article/create")
    public String create(@RequestParam("coverImages") String coverImages,
                         @RequestParam("title") String title,
                         @RequestParam("author") String author,
                         @RequestParam("content") String content,
                         @RequestParam("publishTime") String publishTime,
                         @RequestParam("categoryId") Long categoryId) {
        Long articleId;
        try {
            articleId = articleService.edit(null, coverImages, title.trim(), author.trim(), content, publishTime, categoryId);
        } catch (RuntimeException e) {
            log.error("新增文章失败", e);
            return "新增失败：" + e.getMessage();
        }
        return String.valueOf(articleId);
    }

    @RequestMapping("/article/update")
    public String update(@RequestParam("articleId") Long articleId,
                         @RequestParam("coverImages") String coverImages,
                         @RequestParam("title") String title,
                         @RequestParam("author") String author,
                         @RequestParam("content") String content,
                         @RequestParam("publishTime") String publishTime,
                         @RequestParam("categoryId") Long categoryId) {
        Long updatedId;
        try {
            updatedId = articleService.edit(articleId, coverImages, title.trim(), author.trim(), content, publishTime, categoryId);
        } catch (RuntimeException e) {
            log.error("修改文章失败", e);
            return "更新失败：" + e.getMessage();
        }
        return String.valueOf(updatedId);
    }

    @RequestMapping("/article/delete")
    public String delete(@RequestParam("articleId") Long articleId) {
        return articleService.delete(articleId) ? "成功" : "失败";
    }

}
