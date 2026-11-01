package com.yifan.newsflow.console.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ArticleExcelVo {

    @ExcelProperty("封面图")
    private String coverImages;

    @ExcelProperty("标题")
    private String title;

    @ExcelProperty("作者")
    private String author;

    @ExcelProperty("正文")
    private String content;

    @ExcelProperty("分类ID")
    private Long categoryId;

    @ExcelProperty("发布时间")
    private String publishTime;

    @ExcelProperty("创建时间")
    private String createTime;

    @ExcelProperty("更新时间")
    private String updateTime;

}
