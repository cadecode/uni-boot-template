package com.github.cadecode.uniboot.starter.mybatis.config;

import com.github.cadecode.uniboot.starter.mybatis.listener.BaseEntityListener;
import com.github.cadecode.uniboot.starter.mybatis.model.BaseEntity;
import com.mybatisflex.spring.boot.ConfigurationCustomizer;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import com.mybatisflex.spring.boot.SqlSessionFactoryBeanCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis-flex 配置类
 * <p>
 * 固定配置可在此配置类中配置，灵活配置推荐使用配置类
 *
 * @author Cade Li
 * @since 2024/4/24
 */
@Configuration
public class MyBatisFlexConfig {

    @ConditionalOnMissingBean
    @Bean
    public BaseEntityListener baseInsertUpdateListener() {
        return new BaseEntityListener();
    }

    /**
     * global-config 配置器示例
     * <p>
     * mybatis-flex 自身配置
     */
    @ConditionalOnMissingBean
    @Bean
    public MyBatisFlexCustomizer myBatisFlexCustomizer(BaseEntityListener baseEntityListener) {
        return globalConfig -> {
            globalConfig.registerInsertListener(baseEntityListener, BaseEntity.class);
            globalConfig.registerUpdateListener(baseEntityListener, BaseEntity.class);
            globalConfig.registerSetListener(baseEntityListener, BaseEntity.class);
        };
    }

    /**
     * config 配置器示例
     * <p>
     * mybatis 原生配置，可注册多个
     */
    // @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {};
    }

    /**
     * SqlSessionFactoryBean 配置器示例
     * <p>
     * SqlSessionFactory 配置，可注册多个
     */
    // @Bean
    public SqlSessionFactoryBeanCustomizer sqlSessionFactoryBeanCustomizer() {
        return factoryBean -> {};
    }
}
