package com.github.cadecode.uniboot.codegen.util;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;

/**
 * 数据库文档生成工具
 *
 * @author Cade
 * @since 2025/9/11
 */
@RequiredArgsConstructor
@Component
public class DbDocKit implements EnvironmentAware {

    public static final String BASE_CONFIG_VERSION_KEY = "uni-boot.framework.version";

    private final DataSource dataSource;

    private Environment environment;

    public void dbdoc(String fileType,
                      String fileName,
                      String fileOutputDir,
                      String version,
                      String description,
                      String tablePrefix,
                      String tableSuffix,
                      String[] tableNames) {
        EngineConfig engineConfig = getEngineConfig(fileType, fileName, fileOutputDir);

        // 指定生成逻辑
        // 当存在指定表、指定表前缀、指定表后缀时，将生成指定表，其余表不生成、并跳过忽略表配置
        ProcessConfig processConfig = ProcessConfig.builder()
                // 根据名称指定表生成
                .designatedTableName(Arrays.asList(tableNames))
                // 根据表前缀生成
                .designatedTablePrefix(StrUtil.isEmpty(tablePrefix) ? Collections.emptyList() : Collections.singletonList(tablePrefix))
                // 根据表后缀生成
                .designatedTableSuffix(StrUtil.isEmpty(tableSuffix) ? Collections.emptyList() : Collections.singletonList(tableSuffix))
                // 忽略表名
                .ignoreTableName(Collections.emptyList())
                // 忽略表前缀
                .ignoreTablePrefix(Collections.emptyList())
                // 忽略表后缀
                .ignoreTableSuffix(Collections.emptyList())
                .build();
        // 配置
        Configuration config = getConfiguration(version, description, engineConfig, processConfig);
        // 执行生成
        new DocumentationExecute(config).execute();
    }

    public void dbdocIgnore(String fileType,
                            String fileName,
                            String fileOutputDir,
                            String version,
                            String description,
                            String ignoreTablePrefix,
                            String ignoreTableSuffix,
                            String[] ignoreTableNames) {
        EngineConfig engineConfig = getEngineConfig(fileType, fileName, fileOutputDir);

        // 指定生成逻辑
        // 当存在指定表、指定表前缀、指定表后缀时，将生成指定表，其余表不生成、并跳过忽略表配置
        ProcessConfig processConfig = ProcessConfig.builder()
                // 根据名称指定表生成
                .designatedTableName(Collections.emptyList())
                // 根据表前缀生成
                .designatedTablePrefix(Collections.emptyList())
                // 根据表后缀生成
                .designatedTableSuffix(Collections.emptyList())
                // 忽略表名
                .ignoreTableName(Arrays.asList(ignoreTableNames))
                // 忽略表前缀
                .ignoreTablePrefix(StrUtil.isEmpty(ignoreTablePrefix) ? Collections.emptyList() : Collections.singletonList(ignoreTablePrefix))
                // 忽略表后缀
                .ignoreTableSuffix(StrUtil.isEmpty(ignoreTableSuffix) ? Collections.emptyList() : Collections.singletonList(ignoreTableSuffix))
                .build();
        // 配置
        Configuration config = getConfiguration(version, description, engineConfig, processConfig);
        // 执行生成
        new DocumentationExecute(config).execute();
    }

    private Configuration getConfiguration(String version, String description,
                                           EngineConfig engineConfig, ProcessConfig processConfig) {
        FlexDataSource flexDataSource = (FlexDataSource) dataSource;
        DataSource dataSource = flexDataSource.getDefaultDataSource();
        HikariDataSource hikariDataSource;
        // 兼容 HikariDataSource 和其他连接池
        if (dataSource instanceof HikariDataSource hDataSource) {
            hikariDataSource = hDataSource;
        } else {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(flexDataSource.getUrl());
            hikariConfig.setUsername((String) ReflectUtil.getFieldValue(dataSource, "username"));
            hikariConfig.setPassword((String) ReflectUtil.getFieldValue(dataSource, "password"));
            hikariDataSource = new HikariDataSource(hikariConfig);
        }
        // 从配置中读取版本号
        String versionProperty = StrUtil.nullToDefault(environment.getProperty(BASE_CONFIG_VERSION_KEY), "");
        return Configuration.builder()
                .version(StrUtil.emptyToDefault(version, versionProperty))
                .description(description)
                .dataSource(hikariDataSource)
                .engineConfig(engineConfig)
                .produceConfig(processConfig)
                .build();
    }

    private EngineConfig getEngineConfig(String fileType, String fileName, String fileOutputDir) {
        return EngineConfig.builder()
                .fileOutputDir(fileOutputDir)
                // 关闭，打开文件目录存在兼容性问题
                .openOutputDir(false)
                .fileType(EngineFileType.valueOf(fileType))
                .produceType(EngineTemplateType.freemarker)
                // 自定义文件名称
                .fileName(fileName)
                .build();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}