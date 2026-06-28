package com.github.cadecode.xboot.common.extension.pipeline.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * Pipeline 配置项
 *
 * @author Cade Li
 * @since 2025/12/23
 */
@Data
@ConfigurationProperties(prefix = "x-boot.extension.pipeline")
public class PipelineProperties {

    /**
     * 过滤器启用配置
     * <p>
     * key: ExtensionType.getType() 值
     * value: 启用的 filter bean name 列表
     */
    private Map<String, List<String>> filterSelectors;

}
