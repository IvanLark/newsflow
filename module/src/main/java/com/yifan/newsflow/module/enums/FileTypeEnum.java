package com.yifan.newsflow.module.enums;

public enum FileTypeEnum {

    IMAGE(1, "image", "图片"),
    VIDEO(2, "video", "视频"),
    FILE(3, "file", "文件");

    private final int code;
    private final String dir;
    private final String cnName;

    FileTypeEnum(int code, String dir, String cnName) {
        this.code = code;
        this.dir = dir;
        this.cnName = cnName;
    }

    public int getCode() {
        return code;
    }

    public String getDir() {
        return dir;
    }

    public String getCnName() {
        return cnName;
    }

    public static FileTypeEnum fromCode(Integer code) {
        if (code != null) {
            for (FileTypeEnum type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
        }
        throw new RuntimeException("unknown file type: " + code);
    }

}
