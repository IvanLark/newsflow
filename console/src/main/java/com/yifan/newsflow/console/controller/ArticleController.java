package com.yifan.newsflow.console.controller;

import com.yifan.newsflow.console.domain.ConsoleArticleInfoVo;
import com.yifan.newsflow.console.domain.ConsoleArticleListVo;
import com.yifan.newsflow.console.domain.ConsoleArticleVo;
import com.yifan.newsflow.module.entity.Article;
import com.yifan.newsflow.module.service.ArticleService;
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
public class ArticleController {

    private static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private ArticleService articleService;

    @RequestMapping("/article/list")
    public ConsoleArticleListVo list(@RequestParam("page") int page) {
        List<Article> articles = articleService.pageForConsole(page);
        List<ConsoleArticleVo> list = new ArrayList<>();
        for (Article article : articles) {
            list.add(new ConsoleArticleVo()
                    .setArticleId(article.getId())
                    .setWallImage(article.getCoverImages().split("\\$")[0])
                    .setTitle(article.getTitle())
                    .setPublishTime(TimeUtils.format(article.getPublishTime(), TIME_PATTERN))
                    .setCreateTime(TimeUtils.format(article.getCreateTime(), TIME_PATTERN))
                    .setUpdateTime(TimeUtils.format(article.getUpdateTime(), TIME_PATTERN)));
        }
        return new ConsoleArticleListVo()
                .setList(list)
                .setTotal(articleService.countAll())
                .setPageSize(ArticleService.PAGE_SIZE);
    }

    @RequestMapping("/article/info")
    public ConsoleArticleInfoVo info(@RequestParam("articleId") Long articleId) {
        Article article = articleService.getById(articleId);
        if (article == null) {
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
                .setUpdateTime(TimeUtils.format(article.getUpdateTime(), TIME_PATTERN));
    }

    @RequestMapping("/article/create")
    public String create(@RequestParam("coverImages") String coverImages,
                         @RequestParam("title") String title,
                         @RequestParam("author") String author,
                         @RequestParam("content") String content,
                         @RequestParam("publishTime") String publishTime) {
        try {
            Long articleId = articleService.create(coverImages, title, author, content, publishTime);
            return articleId != null ? "自增id为：" + articleId : "失败";
        } catch (RuntimeException e) {
            log.error("新增文章失败", e);
            return "失败";
        }
    }

    @RequestMapping("/article/update")
    public String update(@RequestParam("articleId") Long articleId,
                         @RequestParam("coverImages") String coverImages,
                         @RequestParam("title") String title,
                         @RequestParam("author") String author,
                         @RequestParam("content") String content,
                         @RequestParam("publishTime") String publishTime) {
        try {
            return articleService.update(articleId, coverImages, title, author, content, publishTime) ? "成功" : "失败";
        } catch (RuntimeException e) {
            log.error("修改文章失败", e);
            return "失败";
        }
    }

    @RequestMapping("/article/delete")
    public String delete(@RequestParam("articleId") Long articleId) {
        return articleService.delete(articleId) ? "成功" : "失败";
    }

}
