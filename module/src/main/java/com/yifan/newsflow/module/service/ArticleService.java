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

    public Long create(Article article) {
        int now = TimeUtils.currentSeconds();
        article.setCreateTime(now)
                .setUpdateTime(now)
                .setIsDeleted(0);
        log.info("新增文章，title={}", article.getTitle());
        return articleMapper.insert(article) > 0 ? article.getId() : null;
    }

    public boolean update(Article article) {
        article.setUpdateTime(TimeUtils.currentSeconds())
                .setIsDeleted(0);
        log.info("修改文章，articleId={}", article.getId());
        return articleMapper.update(article) > 0;
    }

    public boolean delete(Long articleId) {
        log.info("删除文章，articleId={}", articleId);
        return articleMapper.delete(articleId, TimeUtils.currentSeconds()) > 0;
    }

}
