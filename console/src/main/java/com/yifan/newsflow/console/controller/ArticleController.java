package com.yifan.newsflow.console.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.yifan.newsflow.console.domain.ArticleExcelVo;
import com.yifan.newsflow.console.domain.ConsoleArticleInfoVo;
import com.yifan.newsflow.console.domain.ConsoleArticleListVo;
import com.yifan.newsflow.console.domain.ConsoleArticleVo;
import com.yifan.newsflow.module.entity.Article;
import com.yifan.newsflow.module.entity.Category;
import com.yifan.newsflow.module.service.ArticleService;
import com.yifan.newsflow.module.service.CategoryService;
import com.yifan.newsflow.module.utils.TimeUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

    @RequestMapping("/article/export")
    public String export(HttpServletResponse response) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("文章列表", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        List<ArticleExcelVo> rows = new ArrayList<>();
        for (Article article : articleService.listAll()) {
            ArticleExcelVo row = new ArticleExcelVo();
            row.setCoverImages(article.getCoverImages());
            row.setTitle(article.getTitle());
            row.setAuthor(article.getAuthor());
            row.setContent(article.getContent());
            row.setCategoryId(article.getCategoryId());
            row.setPublishTime(TimeUtils.format(article.getPublishTime(), TIME_PATTERN));
            row.setCreateTime(TimeUtils.format(article.getCreateTime(), TIME_PATTERN));
            row.setUpdateTime(TimeUtils.format(article.getUpdateTime(), TIME_PATTERN));
            rows.add(row);
        }
        try {
            EasyExcel.write(response.getOutputStream(), ArticleExcelVo.class)
                    .sheet("文章")
                    .doWrite(rows);
        } catch (Exception e) {
            log.error("导出文章失败", e);
            return "导出失败：" + e.getMessage();
        }
        log.info("导出文章完成，共 {} 条", rows.size());
        return null;
    }

    @RequestMapping("/article/import")
    public String importExcel(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "导入失败：file is required";
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !(filename.toLowerCase().endsWith(".xlsx") || filename.toLowerCase().endsWith(".xls"))) {
            return "导入失败：file must be xlsx";
        }
        Map<Long, Category> categoryMap = new HashMap<>();
        for (Category category : categoryService.listAll()) {
            categoryMap.put(category.getId(), category);
        }
        AtomicInteger count = new AtomicInteger();
        try (InputStream inputStream = file.getInputStream()) {
            EasyExcel.read(inputStream, ArticleExcelVo.class, new PageReadListener<ArticleExcelVo>(dataList -> {
                for (ArticleExcelVo row : dataList) {
                    if (row.getCoverImages() == null || row.getCoverImages().isBlank()
                            || row.getTitle() == null || row.getTitle().isBlank()
                            || row.getAuthor() == null || row.getAuthor().isBlank()
                            || row.getContent() == null || row.getContent().isBlank()
                            || row.getPublishTime() == null || row.getPublishTime().isBlank()) {
                        continue;
                    }
                    if (row.getCategoryId() == null || !categoryMap.containsKey(row.getCategoryId())) {
                        continue;
                    }
                    int now = TimeUtils.currentSeconds();
                    Article article = new Article()
                            .setCoverImages(row.getCoverImages())
                            .setTitle(row.getTitle())
                            .setAuthor(row.getAuthor())
                            .setContent(row.getContent())
                            .setCategoryId(row.getCategoryId())
                            .setPublishTime(TimeUtils.parseDate(row.getPublishTime(), TIME_PATTERN))
                            .setCreateTime(row.getCreateTime() == null || row.getCreateTime().isBlank()
                                    ? now : TimeUtils.parseDate(row.getCreateTime(), TIME_PATTERN))
                            .setUpdateTime(row.getUpdateTime() == null || row.getUpdateTime().isBlank()
                                    ? now : TimeUtils.parseDate(row.getUpdateTime(), TIME_PATTERN))
                            .setIsDeleted(0);
                    articleService.insert(article);
                    count.incrementAndGet();
                }
            })).sheet().doRead();
        } catch (Exception e) {
            log.error("导入文章失败", e);
            return "导入失败：" + e.getMessage();
        }
        log.info("导入文章完成，共 {} 条", count.get());
        return "导入成功：" + count.get();
    }

}
