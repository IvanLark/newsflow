package com.yifan.newsflow.module.service;

import com.yifan.newsflow.module.entity.Article;
import com.yifan.newsflow.module.mapper.ArticleMapper;
import com.yifan.newsflow.module.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    public List<Article> list() {
        log.info("查询文章列表");
        return articleMapper.list();
    }

    public Article getById(Long articleId) {
        log.info("查询文章详情，articleId={}", articleId);
        return articleMapper.getById(articleId);
    }

    public boolean create(String coverImages, String title, String author, String content, Integer publishTime) {
        int now = TimeUtils.currentSeconds();
        Article article = new Article()
                .setCoverImages(coverImages)
                .setTitle(title)
                .setAuthor(author)
                .setContent(content)
                .setPublishTime(publishTime)
                .setCreateTime(now)
                .setUpdateTime(now)
                .setIsDeleted(0);
        log.info("新增文章，title={}", title);
        return articleMapper.insert(article) > 0;
    }

    public boolean update(Long articleId, String coverImages, String title, String author, String content, Integer publishTime) {
        Article article = new Article()
                .setId(articleId)
                .setCoverImages(coverImages)
                .setTitle(title)
                .setAuthor(author)
                .setContent(content)
                .setPublishTime(publishTime)
                .setUpdateTime(TimeUtils.currentSeconds())
                .setIsDeleted(0);
        log.info("修改文章，articleId={}", articleId);
        return articleMapper.update(article) > 0;
    }

    public boolean delete(Long articleId) {
        log.info("删除文章，articleId={}", articleId);
        return articleMapper.delete(articleId, TimeUtils.currentSeconds()) > 0;
    }

}
