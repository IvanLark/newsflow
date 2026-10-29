package com.yifan.newsflow.console;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.yifan.newsflow")
@MapperScan("com.yifan.newsflow.module.mapper")
public class NewsflowConsoleApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsflowConsoleApplication.class, args);
    }

}
