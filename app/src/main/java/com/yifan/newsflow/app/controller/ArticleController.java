package com.yifan.newsflow.app.controller;

import com.yifan.newsflow.app.domain.ArticleInfoVo;
import com.yifan.newsflow.app.domain.ArticleListVo;
import com.yifan.newsflow.app.domain.ArticleVo;
import com.yifan.newsflow.module.entity.Article;
import com.yifan.newsflow.module.service.ArticleService;
import com.yifan.newsflow.module.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ArticleController {

    private static final String LIST_TIME_PATTERN = "MM-dd";
    private static final String INFO_TIME_PATTERN = "yyyy-MM-dd";

    @Autowired
    private ArticleService articleService;

    @RequestMapping("/article/list")
    public ArticleListVo list(@RequestParam("page") int page,
                              @RequestParam(value = "keyword", required = false) String keyword) {
        List<Article> articles = articleService.pageForApp(page, keyword);
        Boolean isEnd = articles.size() < ArticleService.PAGE_SIZE;
        List<ArticleVo> list = new ArrayList<>();
        for (Article article : articles) {
            list.add(new ArticleVo()
                    .setArticleId(article.getId())
                    .setWallImage(article.getCoverImages().split("\\$")[0])
                    .setTitle(article.getTitle())
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
        return new ArticleInfoVo()
                .setArticleId(article.getId())
                .setCoverImages(List.of(article.getCoverImages().split("\\$")))
                .setTitle(article.getTitle())
                .setAuthor(article.getAuthor())
                .setContent(article.getContent())
                .setPublishTime(TimeUtils.format(article.getPublishTime(), INFO_TIME_PATTERN));
    }

}
