package com.github.cadecode.xboot.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 本项目配置
 *
 * @author Cade Li
 * @since 2024/4/28
 */
@Data
@Configuration
@ConfigurationProperties("x-boot.framework")
public class FrameworkConfig {

    private String version;

}
