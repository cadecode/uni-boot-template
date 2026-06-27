package com.github.cadecode.uniboot.starter.swagger.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * swagger 配置项
 *
 * @author Cade Li
 * @date 2024/4/28
 */
@Data
@ConfigurationProperties("uni-boot.swagger")
public class SwaggerProperties {

    /**
     * 文档标题
     */
    private String title;

    /**
     * 文档描述
     */
    private String description;

    /**
     * 版本
     */
    private String version;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 项目地址
     */
    private String contactUrl;

    /**
     * 邮箱
     */
    private String contactEmail;

}
