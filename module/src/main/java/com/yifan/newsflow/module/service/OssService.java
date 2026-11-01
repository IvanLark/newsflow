package com.yifan.newsflow.module.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.yifan.newsflow.module.config.OssProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Service
public class OssService {

    @Autowired(required = false)
    private OSS ossClient;

    @Autowired
    private OssProperties ossProperties;

    public String upload(String objectKey, InputStream inputStream, long size) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(size);
        requireClient().putObject(ossProperties.getBucket(), objectKey, inputStream, metadata);
        return buildUrl(objectKey);
    }

    public byte[] download(String objectKey) {
        try (OSSObject object = requireClient().getObject(ossProperties.getBucket(), objectKey);
             InputStream inputStream = object.getObjectContent();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            inputStream.transferTo(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("读取 OSS 文件失败：" + objectKey, e);
        }
    }

    public byte[] downloadByUrl(String url) {
        return download(objectKeyOf(url));
    }

    public String objectKeyOf(String url) {
        String path = URI.create(url).getPath();
        if (path == null || path.length() <= 1) {
            throw new RuntimeException("无法从地址中解析文件路径：" + url);
        }
        return path.substring(1);
    }

    public String buildUrl(String objectKey) {
        String urlPrefix = ossProperties.getUrlPrefix();
        if (urlPrefix != null && !urlPrefix.isBlank()) {
            return urlPrefix.endsWith("/") ? urlPrefix + objectKey : urlPrefix + "/" + objectKey;
        }
        String host = ossProperties.getEndpoint().replaceFirst("^https?://", "");
        return "https://" + ossProperties.getBucket() + "." + host + "/" + objectKey;
    }

    private OSS requireClient() {
        if (ossClient == null) {
            throw new RuntimeException("OSS 未初始化，请检查环境变量和 OSS 配置");
        }
        return ossClient;
    }

}
