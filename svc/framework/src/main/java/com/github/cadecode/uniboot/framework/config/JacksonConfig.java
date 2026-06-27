package com.github.cadecode.uniboot.framework.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

/**
 * Jackson配置
 *
 * @author Cade Li
 * @since 2023/4/12
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
        return builder -> {
            // 关闭空对象序列化失败，如 new Object()
            builder.failOnEmptyBeans(false);

            // Long 序列化为字符串（防止前端溢出）
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);

            // LocalDate 和 LocalDateTime 的格式化方式
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 设置 LocalDate / LocalTime / LocalDateTime 序列化
            builder.serializers(new LocalDateSerializer(dateFormatter));
            builder.deserializers(new LocalDateDeserializer(dateFormatter));

            builder.serializers(new LocalTimeSerializer(dateFormatter));
            builder.deserializers(new LocalTimeDeserializer(dateFormatter));

            builder.serializers(new LocalDateTimeSerializer(dateTimeFormatter));
            builder.deserializers(new LocalDateTimeDeserializer(dateTimeFormatter));
        };
    }
}
