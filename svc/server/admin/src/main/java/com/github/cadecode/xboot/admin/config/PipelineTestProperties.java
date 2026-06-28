package com.github.cadecode.xboot.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * Pipeline 测试配置示例
 * <p>
 * 每个业务模块维护自己的 filter-selectors 配置，
 * 通过 {@link com.github.cadecode.xboot.common.extension.pipeline.selector.FilterSelectorFactory} 解析。
 *
 * @author Cade Li
 * @since 2026/6/28
 */
@Data
@ConfigurationProperties(prefix = "pipeline-test")
public class PipelineTestProperties {

    /**
     * 过滤器启用配置
     * <p>
     * key: ExtensionType.getType() 值
     * value: 启用的 filter bean name 列表
     */
    private Map<String, List<String>> filterSelectors;

}
