package com.yifan.newsflow.module.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "newsflow.oss")
public class OssProperties {

    private String endpoint;
    private String region;
    private String bucket;
    private String urlPrefix;

}
