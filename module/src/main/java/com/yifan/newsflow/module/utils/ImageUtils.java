package com.yifan.newsflow.module.utils;

import com.yifan.newsflow.module.service.OssService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageUtils {

    private static final Pattern SIZE_PATTERN = Pattern.compile("_(\\d+)x(\\d+)\\.");

    private ImageUtils() {
    }

    public static float getAr(String url, OssService ossService) {
        if (url == null || url.isEmpty()) {
            return 0F;
        }
        return computeAr(url, ossService);
    }

    public static int[] parseSize(String url) {
        if (url == null) {
            return null;
        }
        Matcher matcher = SIZE_PATTERN.matcher(url);
        if (!matcher.find()) {
            return null;
        }
        return new int[]{Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2))};
    }

    private static float computeAr(String url, OssService ossService) {
        int[] size = parseSize(url);
        if (isValid(size)) {
            return ratio(size);
        }
        size = loadSize(url, ossService);
        if (isValid(size)) {
            return ratio(size);
        }
        return 0F;
    }

    private static int[] loadSize(String url, OssService ossService) {
        if (ossService == null) {
            return null;
        }
        try {
            byte[] bytes = ossService.downloadByUrl(url);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            if (image == null) {
                return null;
            }
            return new int[]{image.getWidth(), image.getHeight()};
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isValid(int[] size) {
        return size != null && size[0] > 0 && size[1] > 0;
    }

    private static float ratio(int[] size) {
        return Math.round((float) size[0] / size[1] * 100) / 100F;
    }

}
