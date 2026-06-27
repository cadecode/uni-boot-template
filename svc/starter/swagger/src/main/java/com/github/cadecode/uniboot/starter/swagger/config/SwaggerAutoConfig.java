package com.github.cadecode.uniboot.starter.swagger.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * swagger 自动配置
 *
 * @author Cade Li
 * @since 2024/4/28
 */
@RequiredArgsConstructor
@EnableKnife4j
@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(name = "uni-boot.swagger.title")
public class SwaggerAutoConfig {

    @ConditionalOnMissingBean
    @Bean
    public OpenAPI openAPI(SwaggerProperties prop) {
        Contact contact = new Contact()
                .name(prop.getContactName())
                .url(prop.getContactUrl())
                .email(prop.getContactEmail());
        return new OpenAPI()
                .info(new Info()
                        .title(prop.getTitle())
                        .description(prop.getDescription())
                        .version(prop.getVersion())
                        .contact(contact));
    }
}
