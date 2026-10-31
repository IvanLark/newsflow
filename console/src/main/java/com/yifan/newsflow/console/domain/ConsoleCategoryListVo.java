package com.yifan.newsflow.console.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ConsoleCategoryListVo {

    private List<ConsoleCategoryVo> list;

}
