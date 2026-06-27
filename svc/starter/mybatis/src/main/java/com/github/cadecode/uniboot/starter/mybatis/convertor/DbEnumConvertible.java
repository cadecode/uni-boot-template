package com.github.cadecode.uniboot.starter.mybatis.convertor;

/**
 * 枚举到数据库转换接口
 *
 * @author Cade Li
 * @since 2023/6/8
 */
public interface DbEnumConvertible {

    /**
     * 返回用于数据库持久化的字段内容
     * <p>数据库使用数值类型存储，如 tinyint unsigned 0~255
     */
    Integer dbValue();
}
