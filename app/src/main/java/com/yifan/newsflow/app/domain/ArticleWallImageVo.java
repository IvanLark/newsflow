package com.yifan.newsflow.app.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ArticleWallImageVo {

    private String url;
    private Float ar;

}
