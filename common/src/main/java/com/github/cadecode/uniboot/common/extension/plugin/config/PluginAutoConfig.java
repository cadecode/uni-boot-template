package com.github.cadecode.uniboot.common.extension.plugin.config;

import com.github.cadecode.uniboot.common.extension.plugin.PluginContext;
import com.github.cadecode.uniboot.common.extension.plugin.PluginExecutor;
import com.github.cadecode.uniboot.common.extension.plugin.PluginSelectorExecutor;
import com.github.cadecode.uniboot.common.extension.plugin.PluginService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.config.EnablePluginRegistries;

/**
 * 插件自动配置类
 *
 * @author Cade
 * @since 2025/12/22
 */
// 启用 spring-plugin
@EnablePluginRegistries({PluginService.class})
@Configuration
public class PluginAutoConfig {

    @Bean
    public PluginExecutor pluginExecutor(PluginRegistry<PluginService, PluginContext> pluginRegistry) {
        return new PluginSelectorExecutor(pluginRegistry);
    }
}
