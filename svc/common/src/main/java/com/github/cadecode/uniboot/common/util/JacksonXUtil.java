package com.github.cadecode.uniboot.common.util;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Jackson XML 工具类
 *
 * @author Cade Li
 * @date 2022/10/13
 */
@Component
public class JacksonXUtil implements InitializingBean {

    private static final String STANDARD_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static XmlMapper XML_MAPPER;

    public static XmlMapper getMapper() {
        return XML_MAPPER;
    }

    /**
     * 转换对象到 xml 串
     *
     * @param bean 需要转换的对象
     * @return xml 字符串
     */
    public static String toXml(Object bean) {
        return toXml(bean, true);
    }

    /**
     * 转换对象到 xml 串
     *
     * @param bean     需要转换的对象
     * @param isPretty 是否美化输出（换行）
     * @return xml 字符串
     */
    public static String toXml(Object bean, boolean isPretty) {
        if (bean == null) {
            return null;
        }
        if (bean instanceof String) {
            return (String) bean;
        }
        try {
            if (!isPretty) {
                return XML_MAPPER.writeValueAsString(bean);
            }
            return XML_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("转换 Bean 到 xml 出错", e);
        }
    }

    /**
     * 转换字符串到自定义对象
     *
     * @param xml   需要转换的字符串
     * @param clazz 自定义对象的 class 对象
     * @return 自定义对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T toBean(String xml, Class<T> clazz) {
        if (StrUtil.isEmpty(xml) || Objects.isNull(clazz)) {
            return null;
        }
        if (clazz.equals(String.class)) {
            return (T) xml;
        }
        try {
            return XML_MAPPER.readValue(xml, clazz);
        } catch (Exception e) {
            throw new RuntimeException("转换 xml 到 Bean 出错", e);
        }
    }

    /**
     * 转换字符串到集合对象，可以保留泛型
     *
     * @param xml           需要转换的字符串
     * @param typeReference 自定义对象的 TypeReference 对象
     * @return 自定义对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T toBean(String xml, TypeReference<T> typeReference) {
        if (StrUtil.isEmpty(xml) || Objects.isNull(typeReference)) {
            return null;
        }
        if (typeReference.getType().equals(String.class)) {
            return (T) xml;
        }
        try {
            return XML_MAPPER.readValue(xml, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("转换 xml 到 Bean 出错", e);
        }
    }

    @Override
    public void afterPropertiesSet() {
        XML_MAPPER = XmlMapper.builder()
                // 取消默认转换 timestamps 形式
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                // 忽略空Bean转xml的错误
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                // 忽略在 xml 字符串中存在，但是在 java 对象中不存在对应属性的情况。防止错误
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                // 忽略标签的大小写敏感
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .build();

        // 列入对象的全部字段
        XML_MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // 统一所有的日期格式为以下的样式，即 yyyy-MM-dd HH:mm:ss
        XML_MAPPER.setDateFormat(new SimpleDateFormat(STANDARD_FORMAT_PATTERN));
    }
}
