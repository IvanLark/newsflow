package com.yifan.newsflow.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.yifan.newsflow")
@MapperScan("com.yifan.newsflow.module.mapper")
public class NewsflowAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsflowAppApplication.class, args);
    }

}
