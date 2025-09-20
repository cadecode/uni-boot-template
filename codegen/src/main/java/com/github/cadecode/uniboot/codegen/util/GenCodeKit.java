package com.github.cadecode.uniboot.codegen.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.github.cadecode.uniboot.codegen.generator.EntityConvertGenerator;
import com.github.cadecode.uniboot.starter.mybatis.model.BaseEntity;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.EntityConfig.SwaggerVersion;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.PackageConfig;
import com.mybatisflex.codegen.config.StrategyConfig;
import com.mybatisflex.codegen.generator.GeneratorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 代码生成工具类
 *
 * @author Cade
 * @since 2025/9/11
 */
@RequiredArgsConstructor
@Component
public class GenCodeKit {

    private final DataSource dataSource;

    private final GlobalConfig globalConfig = new GlobalConfig();

    {
        // 注意：生成代码不覆盖现有文件！！
        // entity
        globalConfig.enableEntity()
                // 开启 lombok
                .setWithLombok(true)
                // 设置 jdk 版本
                .setJdkVersion(17)
                // 开始 swagger doc 版本
                .setWithSwagger(true)
                .setSwaggerVersion(SwaggerVersion.DOC)
                // interface
                .setImplInterfaces(BaseEntity.class, Serializable.class)
                .setOverwriteEnable(false);
        // mapper
        globalConfig.enableMapper()
                // 开启 mapper 注解
                .setMapperAnnotation(true)
                .setOverwriteEnable(false);
        globalConfig.enableMapperXml()
                .setOverwriteEnable(false);
        // service
        globalConfig.enableService()
                .setOverwriteEnable(false);
        globalConfig.enableServiceImpl()
                .setOverwriteEnable(false);
        // controller
        globalConfig.enableController()
                .setOverwriteEnable(false);

        // 模板配置
        globalConfig.getTemplateConfig()
                .setController("/template/enjoy/controller.tpl");
        // .setEntity("/template/enjoy/entity.tpl")
        // .setMapper("/template/enjoy/mapper.tpl")
        // .setMapperXml("/template/enjoy/mapperXml.tpl")
        // .setService("/template/enjoy/service.tpl")
        // .setServiceImpl("/template/enjoy/serviceImpl.tpl")
        ;

        // 自定义生成器
        // entityConvert
        GeneratorFactory.registerGenerator("entityConvert", new EntityConvertGenerator());
    }

    public void codegen(String outputDir,
                        String basePackage,
                        String author,
                        String tablePrefix,
                        String[] tableNames) {
        // javadoc
        globalConfig.getJavadocConfig()
                .setAuthor(StrUtil.emptyToDefault(author, System.getProperty("user.name")))
                .setSince(DateUtil.format(LocalDateTime.now(), "yyyy/M/dd"));

        // 策略配置
        StrategyConfig strategyConfig = globalConfig.getStrategyConfig();
        strategyConfig.setTablePrefix(tablePrefix);
        strategyConfig.setGenerateTable(tableNames);

        // 包配置
        PackageConfig packageConfig = globalConfig.getPackageConfig();
        packageConfig.setSourceDir(outputDir + "/src/main/java");
        packageConfig.setMapperXmlPath(outputDir + "/src/main/resources/mapper");
        packageConfig.setBasePackage(basePackage);
        // 按本项目风格定制
        packageConfig.setEntityPackage(packageConfig.getBasePackage() + ".bean.entity");
        packageConfig.setServiceImplPackage(packageConfig.getBasePackage() + ".serviceimpl");
        Generator generator = new Generator(dataSource, globalConfig);
        generator.generate();
    }
}
