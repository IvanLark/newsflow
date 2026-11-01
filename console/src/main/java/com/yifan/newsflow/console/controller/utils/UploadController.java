package com.yifan.newsflow.console.controller.utils;

import com.yifan.newsflow.module.entity.FileRecord;
import com.yifan.newsflow.module.enums.FileTypeEnum;
import com.yifan.newsflow.module.service.FileRecordService;
import com.yifan.newsflow.module.service.OssService;
import com.yifan.newsflow.module.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RestController
public class UploadController {

    private static final String DATE_PATTERN = "yyMM/dd";

    @Autowired
    private OssService ossService;

    @Autowired
    private FileRecordService fileRecordService;

    @RequestMapping("/utils/upload")
    public String upload(@RequestParam("type") Integer type,
                         @RequestParam("file") MultipartFile file) {
        try {
            return save(type, file);
        } catch (RuntimeException e) {
            log.error("上传文件失败", e);
            return "上传失败：" + e.getMessage();
        }
    }

    private String save(Integer type, MultipartFile file) {
        FileTypeEnum fileType = FileTypeEnum.fromCode(type);
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("file is required");
        }
        String extension = extensionOf(file.getOriginalFilename());
        String objectKey = buildObjectKey(fileType, extension, file);
        String url;
        try (InputStream inputStream = file.getInputStream()) {
            url = ossService.upload(objectKey, inputStream, file.getSize());
        } catch (IOException e) {
            throw new RuntimeException("读取上传文件失败：" + e.getMessage(), e);
        }
        int now = TimeUtils.currentSeconds();
        fileRecordService.insert(new FileRecord()
                .setType(fileType.getCode())
                .setUrl(url)
                .setCreateTime(now)
                .setUpdateTime(now)
                .setIsDeleted(0));
        log.info("上传文件成功，type={}, url={}", fileType.getCnName(), url);
        return url;
    }

    private String buildObjectKey(FileTypeEnum fileType, String extension, MultipartFile file) {
        String baseName = baseName();
        if (fileType == FileTypeEnum.IMAGE) {
            int[] size = imageSize(file);
            baseName = baseName + "_" + size[0] + "x" + size[1];
        }
        String datePath = new SimpleDateFormat(DATE_PATTERN).format(new Date());
        return fileType.getDir() + "/" + datePath + "/" + baseName + "." + extension;
    }

    private String baseName() {
        int random = ThreadLocalRandom.current().nextInt(1000) + 1;
        return random + String.valueOf(System.currentTimeMillis());
    }

    private int[] imageSize(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                return new int[]{0, 0};
            }
            return new int[]{image.getWidth(), image.getHeight()};
        } catch (IOException e) {
            return new int[]{0, 0};
        }
    }

    private String extensionOf(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new RuntimeException("file name is required");
        }
        String name = Paths.get(originalFilename).getFileName().toString();
        int index = name.lastIndexOf('.');
        if (index < 0 || index == name.length() - 1) {
            throw new RuntimeException("file extension is required");
        }
        String extension = name.substring(index + 1).toLowerCase(Locale.ROOT);
        if (!extension.matches("[a-z0-9]{1,10}")) {
            throw new RuntimeException("invalid file extension: " + extension);
        }
        return extension;
    }

}
