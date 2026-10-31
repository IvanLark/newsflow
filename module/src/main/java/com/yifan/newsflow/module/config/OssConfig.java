package com.yifan.newsflow.module.config;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Slf4j
@Configuration
public class OssConfig {

    private static final String ACCESS_KEY_ID = "OSS_ACCESS_KEY_ID";
    private static final String ACCESS_KEY_SECRET = "OSS_ACCESS_KEY_SECRET";

    @Autowired
    private OssProperties ossProperties;

    @Bean(destroyMethod = "shutdown")
    @Conditional(OssConfiguredCondition.class)
    public OSS ossClient() {
        ClientBuilderConfiguration configuration = new ClientBuilderConfiguration();
        configuration.setSignatureVersion(SignVersion.V4);
        configuration.setConnectionTimeout(5000);
        configuration.setSocketTimeout(5000);
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(ossProperties.getEndpoint())
                .region(ossProperties.getRegion())
                .credentialsProvider(new DefaultCredentialProvider(
                        System.getenv(ACCESS_KEY_ID),
                        System.getenv(ACCESS_KEY_SECRET)))
                .clientConfiguration(configuration)
                .build();
        log.info("OSS 客户端初始化完成，bucket={}", ossProperties.getBucket());
        return ossClient;
    }

    static class OssConfiguredCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Environment environment = context.getEnvironment();
            boolean configured = hasText(environment.getProperty("newsflow.oss.endpoint"))
                    && hasText(environment.getProperty("newsflow.oss.region"))
                    && hasText(environment.getProperty("newsflow.oss.bucket"))
                    && hasText(System.getenv(ACCESS_KEY_ID))
                    && hasText(System.getenv(ACCESS_KEY_SECRET));
            if (!configured) {
                log.warn("OSS 配置不完整，跳过 OSS 客户端初始化");
            }
            return configured;
        }

        private boolean hasText(String value) {
            return value != null && !value.isBlank();
        }
    }

}
