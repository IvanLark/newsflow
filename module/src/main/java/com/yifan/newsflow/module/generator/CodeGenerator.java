package com.yifan.newsflow.module.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.apache.ibatis.type.JdbcType;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class CodeGenerator {

    private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/newsflow?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static final String PARENT_PACKAGE = "com.yifan.newsflow.module";
    private static final String TABLE_NAME = "category";
    private static final String ENTITY_TEMPLATE = "/templates/newsflow/entity.java";
    private static final String MAPPER_TEMPLATE = "/templates/newsflow/mapper.java";
    private static final String MAPPER_XML_TEMPLATE = "/templates/newsflow/mapper.xml";
    private static final String SERVICE_TEMPLATE = "/templates/newsflow/service.java";

    public static void main(String[] args) {
        Path moduleDir = resolveModuleDir();
        String javaDir = moduleDir.resolve("src/main/java").toString();
        String xmlDir = moduleDir.resolve("src/main/resources/mapper").toString();

        FastAutoGenerator.create(JDBC_URL, USERNAME, PASSWORD)
                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    if (JdbcType.TINYINT == metaInfo.getJdbcType()) {
                        return DbColumnType.INTEGER;
                    }
                    return typeRegistry.getColumnType(metaInfo);
                }))
                .globalConfig(builder -> builder.author("yifan").outputDir(javaDir).disableOpenDir())
                .packageConfig(builder -> builder
                        .parent(PARENT_PACKAGE)
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .pathInfo(Map.of(OutputFile.xml, xmlDir)))
                .strategyConfig(builder -> builder
                        .addInclude(TABLE_NAME)
                        .entityBuilder().javaTemplate(ENTITY_TEMPLATE).enableFileOverride()
                        .mapperBuilder().mapperTemplate(MAPPER_TEMPLATE).mapperXmlTemplate(MAPPER_XML_TEMPLATE).enableFileOverride()
                        .serviceBuilder().serviceTemplate(SERVICE_TEMPLATE).formatServiceFileName("%sService")
                        .disableServiceImpl()
                        .enableFileOverride()
                        .controllerBuilder().disable())
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();

        System.out.println("生成完成，代码目录：" + javaDir);
        System.out.println("生成完成，XML 目录：" + xmlDir);
    }

    private static Path resolveModuleDir() {
        Path classpathModuleDir = moduleDirFromClasspath();
        if (classpathModuleDir != null) {
            return classpathModuleDir;
        }
        Path current = Path.of("").toAbsolutePath();
        for (Path dir = current; dir != null; dir = dir.getParent()) {
            if (isModuleDir(dir)) {
                return dir;
            }
            Path nested = dir.resolve("module");
            if (isModuleDir(nested)) {
                return nested;
            }
        }
        throw new IllegalStateException("未找到 module 模块目录，当前目录：" + current);
    }

    private static Path moduleDirFromClasspath() {
        URL location = CodeGenerator.class.getProtectionDomain().getCodeSource().getLocation();
        if (location == null) {
            return null;
        }
        Path classesDir = Path.of(URLDecoder.decode(location.getPath(), StandardCharsets.UTF_8));
        if (!classesDir.endsWith(Path.of("target", "classes"))) {
            return null;
        }
        Path moduleDir = classesDir.getParent().getParent();
        return isModuleDir(moduleDir) ? moduleDir : null;
    }

    private static boolean isModuleDir(Path dir) {
        return Files.exists(dir.resolve("src/main/resources/templates/newsflow"));
    }

}
