package com.github.cadecode.xboot.common.extension.pipeline.config;

import com.github.cadecode.xboot.common.extension.pipeline.selector.FilterSelectorFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Pipeline 自动配置
 *
 * @author Cade Li
 * @since 2025/12/23
 */
@Configuration
@EnableConfigurationProperties(PipelineProperties.class)
public class PipelineAutoConfig {

    @ConditionalOnMissingBean
    @Bean
    public FilterSelectorFactory filterSelectorFactory(PipelineProperties properties) {
        return new FilterSelectorFactory(properties);
    }
}
