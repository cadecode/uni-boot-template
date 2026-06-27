package com.github.cadecode.uniboot.codegen.generator;

import com.mybatisflex.codegen.config.EntityConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.PackageConfig;
import com.mybatisflex.codegen.entity.Table;
import com.mybatisflex.codegen.generator.IGenerator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity
 *
 * @author Cade Li
 * @since 2024/4/25
 */
public class EntityConvertGenerator implements IGenerator {

    private String templatePath = "/template/enjoy/entityConvert.tpl";

    @Override
    public String getTemplatePath() {
        return templatePath;
    }

    @Override
    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    @Override
    public void generate(Table table, GlobalConfig globalConfig) {
        if (!globalConfig.isEntityGenerateEnable()) {
            return;
        }
        PackageConfig packageConfig = globalConfig.getPackageConfig();
        EntityConfig entityConfig = globalConfig.getEntityConfig();
        // convert 类名
        String entityConvertClassName = table.buildEntityClassName() + "Convert";
        // convert 包路径
        String entityConvertPackage = packageConfig.getBasePackage() + ".convert";
        String entityConvertPackagePath = entityConvertPackage.replace(".", "/");
        File entityConvertJavaFile = new File(packageConfig.getSourceDir(), entityConvertPackagePath + "/" +
                entityConvertClassName + ".java");

        if (entityConvertJavaFile.exists() && !entityConfig.isOverwriteEnable()) {
            return;
        }

        Map<String, Object> params = new HashMap<>(4);
        params.put("table", table);
        params.put("entityConfig", entityConfig);
        params.put("packageConfig", packageConfig);
        params.put("javadocConfig", globalConfig.getJavadocConfig());
        params.put("entityConvertPackage", entityConvertPackage);
        params.put("entityConvertClassName", entityConvertClassName);

        globalConfig.getTemplateConfig().getTemplate().generate(params, templatePath, entityConvertJavaFile);
    }
}
