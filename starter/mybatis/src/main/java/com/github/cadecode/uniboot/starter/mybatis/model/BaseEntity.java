package com.github.cadecode.uniboot.starter.mybatis.model;

import java.time.LocalDateTime;

/**
 * 基本字段操作接口
 * <p>方便处理 create_time,create_user,update_time,update_user 字段
 *
 * @author Cade Li
 * @since 2024/4/28
 */
public interface BaseEntity {

    LocalDateTime getCreateTime();

    void setCreateTime(LocalDateTime dateTime);

    String getCreateUser();

    void setCreateUser(String user);

    LocalDateTime getUpdateTime();

    void setUpdateTime(LocalDateTime dateTime);

    String getUpdateUser();

    void setUpdateUser(String user);

    default Object set(Object entity, String property, Object value) {
        return value;
    }
}
