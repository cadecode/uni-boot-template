package com.github.cadecode.xboot.admin.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Pipeline 测试配置注册
 * <p>
 * 示例：业务模块通过 @Configuration + @EnableConfigurationProperties 注册自己的 Properties
 *
 * @author Cade Li
 * @since 2026/6/28
 */
@Configuration
@EnableConfigurationProperties(PipelineTestProperties.class)
public class PipelineTestConfig {
}
