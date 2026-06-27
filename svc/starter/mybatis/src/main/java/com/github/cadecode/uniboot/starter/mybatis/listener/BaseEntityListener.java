package com.github.cadecode.uniboot.starter.mybatis.listener;

import com.github.cadecode.uniboot.starter.mybatis.model.BaseEntity;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.SetListener;
import com.mybatisflex.annotation.UpdateListener;

import java.time.LocalDateTime;

/**
 * 基础字段操作监听器
 * <p>
 * 添加、更新时，填充 create_time,update_time
 *
 * @author Cade Li
 * @since 2024/4/28
 */
public class BaseEntityListener implements InsertListener, UpdateListener, SetListener {

    @Override
    public void onInsert(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            baseEntity.setCreateTime(LocalDateTime.now());
        }
    }

    @Override
    public void onUpdate(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            baseEntity.setUpdateTime(LocalDateTime.now());
        }
    }

    @Override
    public Object onSet(Object entity, String property, Object value) {
        if (entity instanceof BaseEntity baseEntity) {
            return baseEntity.set(entity, property, value);
        }
        return value;
    }
}
