package com.yifan.newsflow.console.controller;

import com.yifan.newsflow.module.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ArticleController {

    @Autowired
    private ArticleService articleService;

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
