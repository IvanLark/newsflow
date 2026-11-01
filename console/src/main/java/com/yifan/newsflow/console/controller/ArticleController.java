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
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
public class ArticleController {

    private static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final int SPLIT_SIZE = 10;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    @Qualifier("articleExcelExecutor")
    private ThreadPoolTaskExecutor articleExcelExecutor;

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

    @RequestMapping("/article/exportZip")
    public String exportZip(HttpServletResponse response) {
        List<List<ArticleExcelVo>> groups = new ArrayList<>(SPLIT_SIZE);
        for (int i = 0; i < SPLIT_SIZE; i++) {
            groups.add(new ArrayList<>());
        }
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
            groups.get((int) (article.getId() % SPLIT_SIZE)).add(row);
        }

        List<String> names = new ArrayList<>();
        List<CompletableFuture<byte[]>> futures = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            List<ArticleExcelVo> rows = groups.get(i);
            if (rows.isEmpty()) {
                continue;
            }
            names.add("article-" + (i + 1) + ".xlsx");
            futures.add(CompletableFuture.supplyAsync(() -> {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                EasyExcel.write(out, ArticleExcelVo.class)
                        .sheet("文章")
                        .doWrite(rows);
                return out.toByteArray();
            }, articleExcelExecutor));
        }
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            log.error("导出文章压缩包失败", e);
            return "导出失败：" + e.getMessage();
        }

        InputStream[] streams = new InputStream[futures.size()];
        for (int i = 0; i < futures.size(); i++) {
            streams[i] = new ByteArrayInputStream(futures.get(i).join());
        }
        response.setContentType("application/zip");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("文章列表", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".zip");
        try {
            ZipUtil.zip(response.getOutputStream(), names.toArray(new String[0]), streams);
        } catch (Exception e) {
            log.error("导出文章压缩包失败", e);
            return "导出失败：" + e.getMessage();
        }
        log.info("导出文章压缩包完成，共 {} 个文件", names.size());
        return null;
    }

    @RequestMapping("/article/importZip")
    public String importZip(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "导入失败：file is required";
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".zip")) {
            return "导入失败：file must be zip";
        }
        Map<Long, Category> categoryMap = new HashMap<>();
        for (Category category : categoryService.listAll()) {
            categoryMap.put(category.getId(), category);
        }

        File tempDir = FileUtil.mkdir(new File(System.getProperty("java.io.tmpdir"), "newsflow-import-" + System.nanoTime()));
        try (InputStream inputStream = file.getInputStream()) {
            ZipUtil.unzip(inputStream, tempDir, StandardCharsets.UTF_8);
        } catch (Exception e) {
            FileUtil.del(tempDir);
            log.error("解压文章压缩包失败", e);
            return "导入失败：" + e.getMessage();
        }

        List<File> excelFiles = new ArrayList<>();
        for (File excelFile : FileUtil.loopFiles(tempDir)) {
            String name = excelFile.getName().toLowerCase();
            if (name.endsWith(".xlsx") || name.endsWith(".xls")) {
                excelFiles.add(excelFile);
            }
        }
        if (excelFiles.isEmpty()) {
            FileUtil.del(tempDir);
            return "导入失败：no excel in zip";
        }

        List<CompletableFuture<List<Article>>> futures = new ArrayList<>();
        for (File excelFile : excelFiles) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                List<Article> articles = new ArrayList<>();
                List<ArticleExcelVo> rows = EasyExcel.read(excelFile).head(ArticleExcelVo.class).sheet().doReadSync();
                for (ArticleExcelVo row : rows) {
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
                    articles.add(new Article()
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
                            .setIsDeleted(0));
                }
                return articles;
            }, articleExcelExecutor));
        }
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            FileUtil.del(tempDir);
            log.error("读取文章压缩包失败", e);
            return "导入失败：" + e.getMessage();
        }

        List<Article> articles = new ArrayList<>();
        for (CompletableFuture<List<Article>> future : futures) {
            articles.addAll(future.join());
        }
        int count;
        try {
            count = articleService.batchInsert(articles);
        } catch (Exception e) {
            FileUtil.del(tempDir);
            log.error("批量插入文章失败", e);
            return "导入失败：" + e.getMessage();
        }
        FileUtil.del(tempDir);
        log.info("导入文章压缩包完成，共 {} 条", count);
        return "导入成功：" + count;
    }

}
