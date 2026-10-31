package com.yifan.newsflow.app.controller;

import com.yifan.newsflow.app.domain.ArticleInfoVo;
import com.yifan.newsflow.app.domain.ArticleListVo;
import com.yifan.newsflow.app.domain.ArticleVo;
import com.yifan.newsflow.app.domain.ArticleWallImageVo;
import com.yifan.newsflow.module.entity.Article;
import com.yifan.newsflow.module.entity.Category;
import com.yifan.newsflow.module.service.ArticleService;
import com.yifan.newsflow.module.service.CategoryService;
import com.yifan.newsflow.module.service.OssService;
import com.yifan.newsflow.module.utils.ImageUtils;
import com.yifan.newsflow.module.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ArticleController {

    private static final String LIST_TIME_PATTERN = "MM-dd";
    private static final String INFO_TIME_PATTERN = "yyyy-MM-dd";

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OssService ossService;

    @RequestMapping("/article/list")
    public ArticleListVo list(@RequestParam("page") int page,
                              @RequestParam(value = "keyword", required = false) String keyword) {
        List<Article> articles = articleService.pageForApp(page, keyword);
        Boolean isEnd = articles.size() < ArticleService.PAGE_SIZE;
        Map<Long, Category> categoryMap = new HashMap<>();
        for (Category category : categoryService.listAll()) {
            categoryMap.put(category.getId(), category);
        }
        List<ArticleVo> list = new ArrayList<>();
        for (Article article : articles) {
            Category category = categoryMap.get(article.getCategoryId());
            if (category == null) {
                continue;
            }
            list.add(new ArticleVo()
                    .setArticleId(article.getId())
                    .setWallImage(wallImageOf(article))
                    .setTitle(article.getTitle())
                    .setCategoryName(category.getName())
                    .setPublishTime(TimeUtils.format(article.getPublishTime(), LIST_TIME_PATTERN)));
        }
        return new ArticleListVo()
                .setList(list)
                .setIsEnd(isEnd);
    }

    @RequestMapping("/article/info")
    public ArticleInfoVo info(@RequestParam("articleId") Long articleId) {
        Article article = articleService.getById(articleId);
        if (article == null) {
            return null;
        }
        Category category = categoryService.getById(article.getCategoryId());
        if (category == null) {
            // TODO 后续改造全局异常处理器之后再来处理分类不存在的情况
            return null;
        }
        return new ArticleInfoVo()
                .setArticleId(article.getId())
                .setCoverImages(List.of(article.getCoverImages().split("\\$")))
                .setTitle(article.getTitle())
                .setAuthor(article.getAuthor())
                .setContent(article.getContent())
                .setPublishTime(TimeUtils.format(article.getPublishTime(), INFO_TIME_PATTERN))
                .setCategoryImage(category.getImage())
                .setCategoryName(category.getName())
                .setCategoryDescription(category.getDescription());
    }

    private ArticleWallImageVo wallImageOf(Article article) {
        String url = article.getCoverImages().split("\\$")[0];
        return new ArticleWallImageVo()
                .setUrl(url)
                .setAr(ImageUtils.getAr(url, ossService));
    }

}
