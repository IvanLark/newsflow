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

    public static final int PAGE_SIZE = 10;

    private static final String PUBLISH_TIME_PATTERN = "yyyy-MM-dd";

    @Autowired
    private ArticleMapper articleMapper;

    public List<Article> pageForApp(int page, String keyword) {
        int offset = (Math.max(page, 1) - 1) * PAGE_SIZE;
        log.info("查询 APP 文章列表，page={}, offset={}, keyword={}", page, offset, keyword);
        return articleMapper.pageForApp(offset, PAGE_SIZE + 1, keyword);
    }

    public List<Article> pageForConsole(int page, String keyword) {
        int offset = (Math.max(page, 1) - 1) * PAGE_SIZE;
        log.info("查询后台文章列表，page={}, offset={}, keyword={}", page, offset, keyword);
        return articleMapper.pageForConsole(offset, PAGE_SIZE, keyword);
    }

    public long countAll(String keyword) {
        log.info("统计文章总数，keyword={}", keyword);
        return articleMapper.countAll(keyword);
    }

    public Article getById(Long articleId) {
        log.info("查询文章详情，articleId={}", articleId);
        return articleMapper.getById(articleId);
    }

    public Long edit(Long articleId, String coverImages, String title, String author, String content, String publishTime) {
        if (coverImages == null || coverImages.isBlank()) {
            throw new RuntimeException("cover images is required");
        }
        if (title == null || title.isBlank()) {
            throw new RuntimeException("title is required");
        }
        if (author == null || author.isBlank()) {
            throw new RuntimeException("author is required");
        }
        if (content == null || content.isBlank()) {
            throw new RuntimeException("content is required");
        }
        if (publishTime == null || publishTime.isBlank()) {
            throw new RuntimeException("publish time is required");
        }
        int publishSeconds = TimeUtils.parseDate(publishTime, PUBLISH_TIME_PATTERN);
        if (publishSeconds <= 0) {
            throw new RuntimeException("publish time must be positive");
        }
        int now = TimeUtils.currentSeconds();
        Article article = new Article()
                .setCoverImages(coverImages)
                .setTitle(title)
                .setAuthor(author)
                .setContent(content)
                .setPublishTime(publishSeconds)
                .setUpdateTime(now)
                .setIsDeleted(0);
        if (articleId == null) {
            article.setCreateTime(now);
            log.info("新增文章，title={}", title);
            if (articleMapper.insert(article) <= 0) {
                throw new RuntimeException("insert article failed");
            }
            return article.getId();
        }
        if (articleId <= 0) {
            throw new RuntimeException("article id must be positive");
        }
        if (articleMapper.existsById(articleId) == null) {
            throw new RuntimeException("article not found: " + articleId);
        }
        article.setId(articleId);
        log.info("修改文章，articleId={}", articleId);
        if (articleMapper.update(article) <= 0) {
            throw new RuntimeException("update article failed: " + articleId);
        }
        return articleId;
    }

    public boolean delete(Long articleId) {
        log.info("删除文章，articleId={}", articleId);
        return articleMapper.delete(articleId, TimeUtils.currentSeconds()) > 0;
    }

}
